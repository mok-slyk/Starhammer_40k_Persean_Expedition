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

                glColor4ub(
                        (byte) currentStage.currentColor.getRed(),
                        (byte) currentStage.currentColor.getGreen(),
                        (byte) currentStage.currentColor.getBlue(),
                        (byte) (currentStage.currentOpacity * 255)
                );

                float stage1Radius = currentStage.currentWidth * 0.5f;
                float stage2Radius = nextStage.currentWidth * 0.5f;

                Vector2f point1Left = MathUtils.getPointOnCircumference(currentStage.getWorldPosition(), stage1Radius, angle + 90f);
                Vector2f point1Right = MathUtils.getPointOnCircumference(currentStage.getWorldPosition(), stage1Radius, angle - 90f);
                Vector2f point2Left = MathUtils.getPointOnCircumference(nextStage.getWorldPosition(), stage2Radius, angle + 90f);
                Vector2f point2Right = MathUtils.getPointOnCircumference(nextStage.getWorldPosition(), stage2Radius, angle - 90f);

                float ax = point2Right.getX() - point1Left.getX(), ay = point2Right.getY() - point1Left.getY(); // dir of diagonal BL->TR
                float bx = point2Left.getX() - point1Right.getX(), by = point2Left.getY() - point1Left.getY(); // dir of diagonal BR->TL
                float cx = point1Right.getX() - point1Left.getX(), cy = point1Right.getY() - point1Left.getY(); // vec from p0 to p1

                float denom = ax * by - ay * bx;

                float segmentLength = nextStage.currentPos - currentStage.currentPos;

                if (!engine.getViewport().isNearViewport(currentStage.getWorldPosition(), segmentLength * 2f)) {
                    continue;
                }

                // q weights per vertex — default 1.0 (works for rectangles)
                float q0 = 1f, q1 = 1f, q2 = 1f, q3 = 1f;

                if (Math.abs(denom) > 1e-6f) {
                    float t = (cx * by - cy * bx) / denom;
                    float s = (cx * ay - cy * ax) / denom;

                    log.info("id = "+j + "/" + layer.stages.size());
                    log.info("t = " + t);
                    log.info("s = " + s);

                    // t = how far along BL->TR the intersection is
                    // s = how far along BR->TL the intersection is

                    // q at each corner is proportional to the "reach" from that corner
                    // to the diagonal intersection point.

                    float iBL = 1f - t;
                    float iTR = t;
                    float iBR = 1f - s;
                    float iTL = s;

                    // q = 1 / (fraction of diagonal from this corner to intersection)
                    // Normalized so the smallest is 1.0
                    q0 = 1f / iBL;   // BL
                    q1 = 1f / iBR;   // BR
                    q2 = 1f / iTR;   // TR
                    q3 = 1f / iTL;   // TL

                    float minQ = Math.min(Math.min(q0, q1), Math.min(q2, q3));
                    q0 /= minQ; q1 /= minQ; q2 /= minQ; q3 /= minQ;
                }

                float texturePos = currentStage.currentPos / extension;

                // create first left corner
                glTexCoord4f(0f * q0, 0f * q0, 0f, q0);
                glVertex2f(point1Left.getX(), point1Left.getY());

                // create first right corner
                glTexCoord4f(1f * q1, 0f * q1, 0f, q1);
                glVertex2f(point1Right.getX(), point1Right.getY());

                texturePos = nextStage.currentPos / extension;

                glColor4ub(
                        (byte) nextStage.currentColor.getRed(),
                        (byte) nextStage.currentColor.getGreen(),
                        (byte) nextStage.currentColor.getBlue(),
                        (byte) (nextStage.currentOpacity * 255)
                );

                // create second right corner
                glTexCoord4f(1f * q2, 1f * q2, 0f, q2);
                glVertex2f(point2Right.getX(), point2Right.getY());

                // create second left corner
                glTexCoord4f(0f * q3, 1f * q3, 0f, q3);
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
