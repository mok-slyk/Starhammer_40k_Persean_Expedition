package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.dark.shaders.light.LightData;
import org.dark.shaders.light.LightEntry;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.plugins.MagicTrailPlugin;
import org.magiclib.util.MagicFakeBeam;

import java.awt.Color;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Utility for spawning fake beams for lance weapons etc. Heavily inspired by MagicFakeBeam
 */
public class LanceBeam {
    public static void spawnLanceBeam(CombatEngineAPI engine, Vector2f start, float angle, float range, Vector2f drift, SpriteAPI coreSprite, Color coreColorStart, Color coreColorEnd, Color coreColorFade, float coreWidthStart, float coreWidthEnd, float coreGrowthFactor, float coreTexLength, float coreTexScroll, float coreOpacityStart, float coreOpacityEnd, float coreIntro, float coreFull , float coreFade, SpriteAPI fringeSprite, Color fringeColorStart, Color fringeColorEnd, Color fringeColorFade, float fringeWidthStart, float fringeWidthEnd, float fringeGrowthFactor, float fringeTexLength, float fringeTexScroll, float fringeOpacityStart, float fringeOpacityEnd, float fringeIntro, float fringeFull , float fringeFade, float blendIn, float blendOut,  float damage, DamageType damageType, float emp, float impactSize, float force, ShipAPI source, boolean ignoreShields, boolean softFlux, OnHitEffectPlugin onHit) {
        //do collision
        CombatEntityAPI target = null;
        Vector2f beamEnd = MathUtils.getPointOnCircumference(start, range, angle);
        List<CombatEntityAPI> entities = CombatUtils.getEntitiesWithinRange(start, range*1.3f);
        if (!entities.isEmpty()) {
            for (CombatEntityAPI entity : entities) {
                if (entity.getCollisionClass() == CollisionClass.NONE) continue;
                Vector2f collisionPoint = null;
                if (entity instanceof ShipAPI ship) {
                    if (
                            entity != source
                            && !(source.isShipWithModules() && source.getChildModulesCopy().contains(entity))
                            && !(entity.getCollisionClass() == CollisionClass.FIGHTER && entity.getOwner() == source.getOwner() && !ship.getEngineController().isFlamedOut())
                            && CollisionUtils.getCollides(start, beamEnd, entity.getLocation(), entity.getCollisionRadius())
                    ){
                        collisionPoint = getLanceShipCollisionPoint(ship, start, beamEnd, ignoreShields);
                    }
                } else if ((entity instanceof CombatAsteroidAPI || (entity instanceof MissileAPI && entity.getOwner() != source.getOwner())) && CollisionUtils.getCollides(start, beamEnd, entity.getLocation(), entity.getCollisionRadius())) {
                    collisionPoint = MagicFakeBeam.getCollisionPointOnCircumference(start, beamEnd, entity.getLocation(), entity.getCollisionRadius());
                }
                if (collisionPoint != null && MathUtils.getDistanceSquared(start, collisionPoint) < MathUtils.getDistanceSquared(start, beamEnd)){
                    beamEnd = collisionPoint;
                    target = entity;
                }
            }
            //do pass-through?
        }

        if (target != null) {
            blendOut = 10;
            if (onHit != null) {
                boolean shieldHit = false;
                if (target.getShield() != null) {
                    shieldHit = target.getShield().isOn() && target.getShield().isWithinArc(beamEnd) && !ignoreShields;
                }
                onHit.onHit(null, target, beamEnd, shieldHit, null, engine);
            }
        }

        //engine.addHitParticle(beamEnd, new Vector2f(), 100, 1, 1, new Color(255, 255, 0));
        //draw beam

        if (MathUtils.isWithinRange(start, beamEnd, blendIn + blendOut)) {
            beamEnd = MathUtils.getPoint(start, blendIn + blendOut + 2, angle);
        }

        //Beam Light
        StandardLight light = new StandardLight(start, beamEnd, drift, drift, null, 0.5f, 30);
        light.setColor(fringeColorFade);
        light.fadeOut(fringeFull+fringeFade);
        LightShader.addLight(light);

        //Beam fringe:

        // 0.---1===2--.3
        drawLanceTrail(
                engine,
                start,
                beamEnd,
                angle,
                drift,
                fringeSprite,
                fringeColorStart,
                fringeColorEnd,
                fringeColorFade,
                fringeWidthStart,
                fringeWidthEnd,
                fringeGrowthFactor,
                fringeTexLength,
                fringeTexScroll,
                fringeOpacityStart,
                fringeOpacityEnd,
                fringeIntro,
                fringeFull,
                fringeFade,
                blendIn,
                blendOut
        );

        drawLanceTrail(
                engine,
                start,
                beamEnd,
                angle,
                drift,
                coreSprite,
                coreColorStart,
                coreColorEnd,
                coreColorFade,
                coreWidthStart,
                coreWidthEnd,
                coreGrowthFactor,
                coreTexLength,
                coreTexScroll,
                coreOpacityStart,
                coreOpacityEnd,
                coreIntro,
                coreFull,
                coreFade,
                blendIn,
                blendOut
        );

        //apply damage to primary target if necessary
        if (target != null && !(damage == 0 && emp == 0)){
            engine.applyDamage(
                    target,
                    beamEnd,
                    damage,
                    damageType,
                    emp,
                    ignoreShields,
                    softFlux,
                    source
            );
            //impact flash
            engine.addHitParticle(
                    beamEnd,
                    new Vector2f(),
                    (float) Math.random() * impactSize / 2 + impactSize,
                    1,
                    fringeFull + fringeFade,
                    fringeColorFade == null ? fringeColorEnd : fringeColorFade
            );
            engine.addHitParticle(
                    beamEnd,
                    new Vector2f(),
                    (float) Math.random() * impactSize / 4 + impactSize / 2,
                    1,
                    coreFull,
                    coreColorEnd
            );
        }
    }

    public static void drawLanceTrail(CombatEngineAPI engine, Vector2f start, Vector2f end, float angle, Vector2f drift, SpriteAPI sprite, Color colorStart, Color colorEnd, Color colorFade, float widthStart, float widthEnd, float growthFactor, float texLength, float texScroll, float opacityStart, float opacityEnd, float intro, float full , float fade, float blendIn, float blendOut){
        drawLanceTrail(engine, start, end, angle, drift, sprite, colorStart, colorEnd, colorFade, widthStart, widthEnd, growthFactor, texLength, texScroll, opacityStart, opacityEnd, intro, full, fade, blendIn, blendOut, 2);
    }

    public static void drawLanceTrail(CombatEngineAPI engine, Vector2f start, Vector2f end, float angle, Vector2f drift, SpriteAPI sprite, Color colorStart, Color colorEnd, Color colorFade, float widthStart, float widthEnd, float growthFactor, float texLength, float texScroll, float opacityStart, float opacityEnd, float intro, float full , float fade, float blendIn, float blendOut, int trailCount) {
        //float offset = (float) (Math.random()*100);
        float offset = -1f;

        for (int i = 0; i < trailCount; i++) {


            float coreID = MagicTrailPlugin.getUniqueID();

            // 0.---1===2--.3

            //0
            LanceTrailTracker.addTrailMemberAdvanced(
                    null,
                    coreID,
                    sprite,
                    start,
                    0,
                    0,
                    angle,
                    0,
                    0,
                    widthStart / 2,
                    widthStart * growthFactor / 2,
                    colorStart,
                    colorFade == null ? colorStart : colorFade,
                    0.5f*opacityStart,
                    intro,
                    full,
                    fade,
                    GL_SRC_ALPHA,
                    GL_ONE,
                    texLength,
                    texScroll,
                    offset,
                    drift,
                    null,
                    CombatEngineLayers.BELOW_INDICATORS_LAYER,
                    1
            );

            //1
            LanceTrailTracker.addTrailMemberAdvanced(null,
                    coreID,
                    sprite,
                    MathUtils.getPoint(start, blendIn, angle),
                    0,
                    0,
                    angle,
                    0,
                    0,
                    widthStart, //?
                    widthStart * growthFactor, //?
                    colorStart,
                    colorFade == null ? colorStart : colorFade,
                    opacityStart,
                    intro,
                    full,
                    fade,
                    GL_SRC_ALPHA,
                    GL_ONE,
                    texLength,
                    texScroll,
                    offset,
                    drift,
                    null,
                    CombatEngineLayers.BELOW_INDICATORS_LAYER,
                    1);
            //engine.addHitParticle(MathUtils.getPoint(start, blendIn, angle), new Vector2f(), 100, 1, 1, new Color(255, 0, 0));

            //2
            LanceTrailTracker.addTrailMemberAdvanced(null,
                    coreID,
                    sprite,
                    //MathUtils.getPoint(beamEnd, blendOut, angle + 180),
                    end,
                    0,
                    0,
                    angle,
                    0,
                    0,
                    widthEnd,
                    widthEnd * growthFactor,
                    colorEnd,
                    colorFade == null ? colorEnd : colorFade,
                    opacityEnd,
                    intro,
                    full,
                    fade,
                    GL_SRC_ALPHA,
                    GL_ONE,
                    texLength,
                    texScroll,
                    offset,
                    drift,
                    null,
                    CombatEngineLayers.BELOW_INDICATORS_LAYER,
                    1);
            //engine.addHitParticle(MathUtils.getPoint(beamEnd, blendOut, angle + 180), new Vector2f(), 100, 1, 1, new Color(255, 0, 0));

            //3
            LanceTrailTracker.addTrailMemberAdvanced(null,
                    coreID,
                    sprite,
                    //beamEnd,
                    MathUtils.getPoint(end, blendOut, angle),
                    0,
                    0,
                    angle,
                    0,
                    0,
                    widthEnd / 2,
                    widthEnd * growthFactor / 2,
                    colorEnd,
                    colorFade == null ? colorEnd : colorFade,
                    0.5f*opacityEnd,
                    intro,
                    full,
                    fade,
                    GL_SRC_ALPHA,
                    GL_ONE,
                    texLength,
                    texScroll,
                    offset,
                    drift,
                    null,
                    CombatEngineLayers.BELOW_INDICATORS_LAYER,
                    1);
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
}
