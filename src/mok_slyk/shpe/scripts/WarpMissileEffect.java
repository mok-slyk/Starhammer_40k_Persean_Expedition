package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import com.fs.starfarer.api.impl.combat.SquallOnFireEffect;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.loading.MissileSpecAPI;
import com.fs.starfarer.api.loading.ProjectileSpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.weapons.MagicVectorThruster;
import mok_slyk.shpe.scripts.utils.LanceBeam;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;
import org.magiclib.util.MagicMisc;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mok_slyk.shpe.scripts.utils.RiftUtils.createStandardWarpRift;

public class WarpMissileEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin, OnHitEffectPlugin, DamageDealtModifier {
    protected static Map<MissileAPI, Float> missiles = new HashMap<>();
    private static final Logger LOG = Global.getLogger(WarpMissileEffect.class);

    protected String weaponId = null;

    public static class warpMissileData {
        private MissileAPI missile;
        private float startTimer;
        private float warpTimer;
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        LOG.info("start");
        for (Map.Entry<MissileAPI, Float> entry : missiles.entrySet()) {
            MissileAPI missile = entry.getKey();
            float timer = entry.getValue();
            LOG.info("got key, value");
            if(missile.getWeapon() != weapon) {
                continue;
            }
            timer-=amount;
            if (timer <= 0) {
                createStandardWarpRift(missile.getLocation(), engine);
                undoMissileWarpedEffect(missile);
                LOG.info("set alpha 1");
                if (missile.getCollisionClass() == CollisionClass.NONE) {
                    List<ShipAPI> ships = CombatUtils.getShipsWithinRange(missile.getLocation(), 2000);
                    LOG.info("got ships");
                    boolean insideShield = false;
                    boolean insideShip = false;
                    for (ShipAPI ship : ships) {
                        LOG.info("foreach start");
                        if (CollisionUtils.isPointWithinBounds(missile.getLocation(), ship))  insideShip = true;
                        LOG.info("checked insideship");
                        if (ship.getShield() != null &&
                                MathUtils.isPointWithinCircle(missile.getLocation(), ship.getShieldCenterEvenIfNoShield(), ship.getShieldRadiusEvenIfNoShield()) &&
                                ship.getShield().isWithinArc(missile.getLocation())
                        ) {
                            insideShield = true;
                        }
                        LOG.info("checked shield");
                    }
                    if (insideShield) {
                        if (insideShip) {
                            LOG.info("in shield");
                            missile.setCollisionClass(CollisionClass.MISSILE_FF);
                            //doHit(mi);
                            LOG.info("set collision");
                        }
                    } else {
                        missile.setCollisionClass(CollisionClass.MISSILE_FF);
                        LOG.info("set collision");
                    }
                }
            }
            LOG.info("checked timer");
            missiles.put(missile, timer);
            LOG.info("wrote new entry");
        }
        LOG.info("end");
    }

    public void doMissileWarpedEffect(MissileAPI missile){
        missile.setJitter("warp", new Color(255, 0, 255), 0.5f, 3, 2);
        missile.setGlowRadius(0);
        missile.setSpriteAlphaOverride(0.4f);
        missile.getEngineController().fadeToOtherColor("warp", new Color(0,0,0,0), new Color(0,0,0,0), 1, 1);
    }

    public void undoMissileWarpedEffect(MissileAPI missile){
        missile.setJitter("warp", new Color(255, 0, 255), 0, 0, 0);
        missile.setGlowRadius(missile.getSpec().getGlowRadius());
        missile.setSpriteAlphaOverride(1);
        missile.getEngineController().fadeToOtherColor("warp", new Color(255,0,0,0), new Color(0,255,0,0), 1, 1);
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        MissileAPI missile = (MissileAPI) projectile;
        float time = calculateWarpTime(missile);
        if (time > 0.01) {
            createStandardWarpRift(Vector2f.add(missile.getLocation(), (Vector2f) missile.getVelocity().normalise().scale(200f), null), engine);
            missiles.put(missile, time);
            missile.setCollisionClass(CollisionClass.NONE);
            doMissileWarpedEffect(missile);
            if (!weapon.getShip().hasListenerOfClass(WarpMissileEffect.class)) {
                weapon.getShip().addListener(this);
                weaponId = weapon.getId();
            }
        }

    }

    public static float calculateWarpTime(MissileAPI missile) {
        //find target
        ShipAPI ship = missile.getWeapon().getShip();
        ShipAPI target = null;
        if (missile.isGuided()) {
            target = (ShipAPI) ((GuidedMissileAI) missile.getMissileAI()).getTarget();
        }
        if (target != null && target.getShield() != null) {
            Vector2f hitPont = MagicFakeBeam.getCollisionPointOnCircumference(missile.getLocation(), target.getLocation(), target.getShield().getLocation(), target.getShield().getRadius());
            return calculateETA(missile.getLocation(), target.getLocation(), missile.getVelocity().length(), missile.getAcceleration(), missile.getMaxSpeed());
        } else {
            return 0;
        }
    }

    //assumes motion in a straight line
    public static float calculateETA(Vector2f start, Vector2f end, float v0, float a, float vMax){
        float s = Vector2f.sub(start, end, null).length(); //total distance to cover
        float dV = Math.abs(v0-vMax); //delta velocity to maximum
        float tA = dV/a; //time to fully accelerate
        float sA = (float) (v0*tA+0.5f*a*Math.pow(tA, 2)); //distance covered while fully accelerating
        if (s <= sA) {
            LOG.info("small eta");
            return (float) (-1*v0/a + Math.sqrt(Math.pow(v0/a, 2)+2*s/a));
        } else {
            LOG.info("big eta");
            float sC = s-sA;
            float tC = sC/vMax;
            return tA+tC;
        }

    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        LOG.info("onhit");
        doHit((MissileAPI) projectile, point, target, engine);

    }

    private static void doHit(MissileAPI missile, Vector2f point, CombatEntityAPI target, CombatEngineAPI engine) {
        //ProjectileSpecAPI projSpec = ((MissileAPI)missile.getWeaponSpec().getProjectileSpec());
        MissileSpecAPI mSpec = missile.getSpec();
        engine.applyDamage(target, point, missile.getDamageAmount(), missile.getDamageType(), missile.getEmpAmount(), true, false, missile.getSource());
        float radius = mSpec.getExplosionRadius();


        engine.addHitParticle(
                point,
                new Vector2f(),
                (float) Math.random() * radius / 2 + radius,
                1,
                0.8f,
                mSpec.getExplosionColor()
        );
        engine.addHitParticle(
                point,
                new Vector2f(),
                (float) Math.random() * radius / 4 + radius / 2,
                1,
                0.3f,
                mSpec.getExplosionColor()
        );

        missiles.remove(missile);
        LOG.info("removed missile");
    }

    @Override
    public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
        if (shieldHit && param instanceof MissileAPI) {
            MissileAPI m = (MissileAPI) param;
            if (m.getWeaponSpec() != null && m.getWeaponSpec().getWeaponId().equals(weaponId)) {
                LOG.info("in ifs");
                CombatEngineAPI engine = Global.getCombatEngine();
                LOG.info("got engine");
                //doHit(m, point, target, engine);

                damage.getModifier().modifyMult("warp_missile", 0);
                LOG.info("modified damage");
                return "warp_missile";
            }
        }
        return null;
    }
}


