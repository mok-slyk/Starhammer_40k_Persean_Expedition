package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class LanceBeamObject {
    private static Logger log = Global.getLogger(LanceBeamObject.class);
    WeaponAPI weapon; //can be null for manually spawned in -> won't rotate
    int barrelIndex = 0;
    Vector2f pos;
    float angle = 0;

    List<LanceBeamLayer> layers = new ArrayList<>();

    float range;
    float extensionTime;
    float breakOffTime;

    //damage mechanical traits
    boolean didHit;
    float nonEmpDamage;
    DamageType damageType;
    float empDamage;
    boolean ignoreShields;
    boolean isSoftFlux;
    float fadeInTime;
    float damageWindowTime;
    float fadeOutTime;


    // maybe add fade damage here later??
    float hitStrengthMult = 1;
    OnHitEffectPlugin onHit;
    boolean applyOnHitToPierced;
    boolean doPierce = false;
    float pierceStrength = 0;
    float impact = 0;



    //beam state
    float extension = 0; // in absolute units
    float intensity = 0; // fraction of 1
    float age = 0;

    /**
     * Advances the beam and handles damage mechanics
     * @param amount Time since last call in seconds
     */
    public void advance(float amount, CombatEngineAPI engine) {
        age += amount;

        //move to match weapon if not broken off
        if (weapon != null && age < breakOffTime) {
            angle = weapon.getCurrAngle();
            //Vector2f offsetVector = VectorUtils.rotate((Vector2f) new Vector2f(1, 0).scale(extension), angle);
            pos = weapon.getFirePoint(barrelIndex);
        }

        //extend beam
        if (extension < range) {
            extension = Math.min(range, age/extensionTime * range);
        }

        ShipAPI source = null;
        //handle collision and damage:
        CombatEntityAPI target = null;
        List<LanceBeam.PierceHitData> pierced = new ArrayList<>();
        Vector2f beamEnd = MathUtils.getPointOnCircumference(pos, extension, angle);
        List<CombatEntityAPI> entities = CombatUtils.getEntitiesWithinRange(pos, extension*1.3f);
        if (!entities.isEmpty()) {
            for (CombatEntityAPI entity : entities) {
                if (entity.getCollisionClass() == CollisionClass.NONE) continue;
                Vector2f collisionPoint = null;
                boolean isHitableFighter = false;
                if (weapon != null) source = weapon.getShip();
                if (entity instanceof ShipAPI ship) {
                    if (
                            entity != source
                                    && source != null && !(source.isShipWithModules() && source.getChildModulesCopy().contains(entity))
                                    && !(entity.getCollisionClass() == CollisionClass.FIGHTER)
                                    && CollisionUtils.getCollides(pos, beamEnd, entity.getLocation(), entity.getCollisionRadius())
                    ){
                        collisionPoint = getLanceShipCollisionPoint(ship, pos, beamEnd, ignoreShields);
                    } else {
                        isHitableFighter = entity.getCollisionClass() == CollisionClass.FIGHTER && !(entity.getOwner() == source.getOwner() && !ship.getEngineController().isFlamedOut());
                    }
                } if ((entity instanceof CombatAsteroidAPI || (entity instanceof MissileAPI && entity.getOwner() != source.getOwner()) || isHitableFighter) && CollisionUtils.getCollides(pos, beamEnd, entity.getLocation(), entity.getCollisionRadius())) {
                    Vector2f piercePoint = MagicFakeBeam.getCollisionPointOnCircumference(pos, beamEnd, entity.getLocation(), entity.getCollisionRadius());
                    log.info(entity.getMass());
                    if (doPierce && (Math.random() * 2 * pierceStrength) > entity.getMass()) {
                        pierced.add(new LanceBeam.PierceHitData(entity, piercePoint));
                    } else {
                        collisionPoint = piercePoint;
                    }
                }
                if (collisionPoint != null && MathUtils.getDistanceSquared(pos, collisionPoint) < MathUtils.getDistanceSquared(pos, beamEnd)){
                    beamEnd = collisionPoint;
                    target = entity;
                }
            }
        }

        // do hit if needed
        if (!didHit && (target != null || pierced.size() > 0) && age > fadeInTime && age < fadeInTime + damageWindowTime) {
            didHit = true;

            if (!(nonEmpDamage == 0 && empDamage == 0)) {
                engine.applyDamage(
                        target,
                        beamEnd,
                        nonEmpDamage,
                        damageType,
                        empDamage,
                        ignoreShields,
                        isSoftFlux,
                        source
                );
            }

            if (target != null) {
                if (onHit != null) {
                    boolean shieldHit = false;
                    if (target.getShield() != null) {
                        shieldHit = target.getShield().isOn() && target.getShield().isWithinArc(beamEnd) && !ignoreShields;
                    }
                    onHit.onHit(null, target, beamEnd, shieldHit, null, engine);
                }
            }
        }

        //advance each stage
        for (LanceBeamLayer layer: layers) {
            layer.advance(amount);
        }
    }

    /**
     * Renders the beam but doesn't update its state
     */
    public void render() {
        CombatEngineAPI engine = Global.getCombatEngine();

        for (int i = 0; i < layers.size(); i++) { // TODO: make sure higher index layers go on top
            LanceBeamLayer layer = layers.get(i);
            int textureID = layer.sprite.getTextureId(); // TODO: handle animations

            glPushMatrix();
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glEnable(GL_BLEND);
            glBlendFunc(layer.blendModeSRC, layer.blendModeDEST);
            glBegin(GL_QUADS);

            for (int j = 0; j < layer.stages.size() - 1; j++) { //we descend the list, assumes the stages are sorted
                LanceBeamStage currentStage = layer.stages.get(i);
                LanceBeamStage nextStage = layer.stages.get(i+1);

                glColor4ub(
                        (byte) currentStage.currentColor.getRed(),
                        (byte) currentStage.currentColor.getGreen(),
                        (byte) currentStage.currentColor.getBlue(),
                        (byte) (currentStage.currentOpacity * 255)
                );

                float stage1Radius = currentStage.currentWidth * 0.5f;
                float stage2Radius = nextStage.currentWidth * 0.5f;

                Vector2f point1Left = MathUtils.getPointOnCircumference(currentStage.getWorldPosition(), stage1Radius, angle - 90f);
                Vector2f point1Right = MathUtils.getPointOnCircumference(currentStage.getWorldPosition(), stage1Radius, angle + 90f);
                Vector2f point2Left = MathUtils.getPointOnCircumference(nextStage.getWorldPosition(), stage2Radius, angle - 90f);
                Vector2f point2Right = MathUtils.getPointOnCircumference(nextStage.getWorldPosition(), stage2Radius, angle + 90f);

                float segmentLength = nextStage.currentPos - currentStage.currentPos;

                if (!engine.getViewport().isNearViewport(currentStage.getWorldPosition(), segmentLength * 2f)) {
                    // TODO: do what needed
                    continue;
                }

                float texturePos = currentStage.currentPos / extension;

                // create first left corner
                glTexCoord4f(0f, texturePos, 0f, 1f);
                glVertex2f(point1Left.getX(), point1Left.getY());

                // create first right corner
                glTexCoord4f(1f, texturePos, 0f, 1f);
                glVertex2f(point1Right.getX(), point1Right.getY());

                texturePos = nextStage.currentPos / extension;

                glColor4ub(
                        (byte) nextStage.currentColor.getRed(),
                        (byte) nextStage.currentColor.getGreen(),
                        (byte) nextStage.currentColor.getBlue(),
                        (byte) (nextStage.currentOpacity * 255)
                );

                // calculate widths
                float bottomWidth = point1Right.getX() - point1Left.getX();
                float topWidth = point2Right.getX() - point2Left.getX();

                // calculate q offset
                float qTop = topWidth/bottomWidth;

                // create second right corner
                glTexCoord4f(1f*qTop, texturePos*qTop, 0f, qTop);
                glVertex2f(point2Right.getX(), point2Right.getY());

                // create second left corner
                glTexCoord4f(0f, texturePos*qTop, 0f, qTop);
                glVertex2f(point2Left.getX(), point2Left.getY());

            }

            glEnd();
            glPopMatrix();
        }
    }

    public static Vector2f getLanceShipCollisionPoint(ShipAPI ship, Vector2f start, Vector2f end, boolean ignoreShields) {
        ShieldAPI shield = ship.getShield();
        Vector2f point = null;
        if (!ignoreShields && shield != null && shield.isOn()) {
            point = MagicFakeBeam.getShipCollisionPoint(start, end, ship, VectorUtils.getAngle(start, end));
        }
        if (point == null) {
            point = SHPEUtils.getLineEntityCollisionPoint(ship, start, end);
        }
        return point;
    }

    public static class PierceHitData{
        CombatEntityAPI target;
        Vector2f point;

        public PierceHitData(CombatEntityAPI target, Vector2f point) {
            this.target = target;
            this.point = point;
        }
    }
}
