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

import java.awt.*;
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
    float extensionTime = 0.1f;
    float breakOffTime = 999f;

    //damage mechanical traits
    boolean didHit = false;
    float nonEmpDamage;
    DamageType damageType;
    float empDamage;
    boolean ignoreShields = false;
    boolean isSoftFlux = true;
    float fadeInTime;
    float damageWindowTime;
    float fadeOutTime;


    // maybe add fade damage here later??
    float hitStrengthMult = 1;
    OnHitEffectPlugin onHit;
    boolean applyOnHitToPierced = false;
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
        if (extensionTime > 0) extension = Math.min(range, age/extensionTime * range);
        else extension = range;


        ShipAPI source = null;
        //handle collision and damage:
        CombatEntityAPI target = null;
        List<LanceBeam.PierceHitData> pierced = new ArrayList<>();
        Vector2f beamEnd = MathUtils.getPointOnCircumference(pos, extension, angle);
        List<CombatEntityAPI> entities = CombatUtils.getEntitiesWithinRange(pos, extension*1.3f);

        if (weapon != null) source = weapon.getShip();
        int owner = 69;
        if (source != null) owner = source.getOwner();

        if (!entities.isEmpty()) {
            for (CombatEntityAPI entity : entities) {
                log.info("checking entity");
                if (entity.getCollisionClass() == CollisionClass.NONE) continue;
                Vector2f collisionPoint = null;
                boolean isHitableFighter = false;
                if (entity instanceof ShipAPI ship) {
                    if (
                            entity != source
                                    && (source == null || !(source.isShipWithModules() && source.getChildModulesCopy().contains(entity)))
                                    && !(entity.getCollisionClass() == CollisionClass.FIGHTER)
                                    && CollisionUtils.getCollides(pos, beamEnd, entity.getLocation(), entity.getCollisionRadius())
                    ){
                        collisionPoint = getLanceShipCollisionPoint(ship, pos, beamEnd, ignoreShields);
                    } else {
                        isHitableFighter = entity.getCollisionClass() == CollisionClass.FIGHTER && !(entity.getOwner() == owner && !ship.getEngineController().isFlamedOut());
                    }
                } if ((entity instanceof CombatAsteroidAPI || (entity instanceof MissileAPI && entity.getOwner() != owner) || isHitableFighter) && CollisionUtils.getCollides(pos, beamEnd, entity.getLocation(), entity.getCollisionRadius())) {
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
                    extension = MathUtils.getDistance(pos, beamEnd);
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

        log.info("beam state: extension: " + extension + ", age: " + age);
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
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glEnable(GL_BLEND);
            glBlendFunc(layer.blendModeSRC, layer.blendModeDEST);
            glBegin(GL_QUADS);

            for (int j = 0; j < layer.stages.size() - 1; j++) { //we descend the list, assumes the stages are sorted
                LanceBeamStage currentStage = layer.stages.get(j);
                LanceBeamStage nextStage = layer.stages.get(j+1);

                float stage1Radius = currentStage.currentWidth * 0.5f;
                float stage2Radius = nextStage.currentWidth * 0.5f;

                Vector2f point1Left = MathUtils.getPointOnCircumference(currentStage.getWorldPosition(), stage1Radius, angle + 90f);
                Vector2f point1Right = MathUtils.getPointOnCircumference(currentStage.getWorldPosition(), stage1Radius, angle - 90f);
                Vector2f point2Left = MathUtils.getPointOnCircumference(nextStage.getWorldPosition(), stage2Radius, angle + 90f);
                Vector2f point2Right = MathUtils.getPointOnCircumference(nextStage.getWorldPosition(), stage2Radius, angle - 90f);

                float segmentLength = nextStage.currentPos - currentStage.currentPos;

                if (!engine.getViewport().isNearViewport(currentStage.getWorldPosition(), segmentLength * 2f)) {
                    continue;
                }

                float texturePos = currentStage.currentPos / extension;
                log.info("index = "+j);
                log.info(texturePos+" = "+currentStage.currentPos+" / "+extension);

                int subdivisions = 8;

                for (int k = 0; k < subdivisions; k++) {
                    float texT0 = texturePos + (((float) k / subdivisions) * segmentLength) / extension;
                    float texT1 = texturePos + (((float) (k+1) / subdivisions) * segmentLength) / extension;

                    float segmentT0 = (float) k / subdivisions; //0 to 1 in segment space
                    float segmentT1 = (float) (k + 1) / subdivisions;

                    log.info("texT0 = " + texT0);
                    log.info("texT1 = " + texT1);

                    for (int l = 0; l < subdivisions; l++) {
                        float s0 = (float) l / subdivisions;
                        float s1 = (float) (l + 1) / subdivisions;

                        float[] v00 = SHPEUtils.bilerpQuad(point1Left, point1Right, point2Right, point2Left, s0, segmentT0);
                        float[] v10 = SHPEUtils.bilerpQuad(point1Left, point1Right, point2Right, point2Left, s1, segmentT0);
                        float[] v11 = SHPEUtils.bilerpQuad(point1Left, point1Right, point2Right, point2Left, s1, segmentT1);
                        float[] v01 = SHPEUtils.bilerpQuad(point1Left, point1Right, point2Right, point2Left, s0, segmentT1);

                        Color color = SHPEUtils.lerpColor(currentStage.currentColor, nextStage.currentColor, segmentT0);
                        int r = color.getRed(); int g = color.getGreen(); int b = color.getBlue(); int a = color.getAlpha();

                        glColor4ub((byte) r, (byte) g, (byte) b, (byte) a);

                        glTexCoord2f(s0, texT0);
                        glVertex2f(v00[0], v00[1]);

                        glTexCoord2f(s1, texT0);
                        glVertex2f(v10[0], v10[1]);


                        color = SHPEUtils.lerpColor(currentStage.currentColor, nextStage.currentColor, segmentT1);
                        r = color.getRed(); g = color.getGreen(); b = color.getBlue(); a = color.getAlpha();

                        glColor4ub((byte) r, (byte) g, (byte) b, (byte) a);

                        glTexCoord2f(s1, texT1);
                        glVertex2f(v11[0], v11[1]);

                        glTexCoord2f(s0, texT1);
                        glVertex2f(v01[0], v01[1]);
                    }
                }

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
