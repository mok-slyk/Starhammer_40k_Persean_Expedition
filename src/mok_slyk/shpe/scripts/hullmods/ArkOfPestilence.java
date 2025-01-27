package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.List;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.getRandomPointInBounds;

public class ArkOfPestilence extends BaseHullMod {
    protected final static float INFECTION_RANGE = 200f;
    protected final static int STARTING_STACKS = 20;
    protected final static float HULL_DAMAGE_PERCENT = 1f;
    private final static String KEY = "ark_of_pestilence_data_key";
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship.isAlive()) {
            doPlagueParticles(ship, amount*ship.getMass()/8);
            for (ShipAPI enemy : AIUtils.getNearbyEnemies(ship, INFECTION_RANGE)) {
                if (enemy.isAlive()) {
                    PestilenceListener pestilenceListener = getOrAddPestilenceListener(enemy, ship);
                    pestilenceListener.stacks = STARTING_STACKS;
                    pestilenceListener.reInfectTimer = 10;
                }
            }
        }
    }

    protected PestilenceArkData getOrAddArkData (ShipAPI ship) {
        PestilenceArkData data = (PestilenceArkData) ship.getCustomData().get(KEY);
        if (data != null) {
            return data;
        }
        data = new PestilenceArkData();
        ship.setCustomData(KEY, data);
        return data;
    }

    protected static PestilenceListener getOrAddPestilenceListener(ShipAPI ship, ShipAPI source) {
        if (!ship.hasListenerOfClass(PestilenceListener.class)) {
            ship.addListener(new PestilenceListener(ship, source));
        }
        return ship.getListeners(PestilenceListener.class).get(0);
    }

    protected static void doPlagueParticles(ShipAPI ship, float amount) {
        float elapsed = amount;
        while (elapsed >= 0.1) {
            Vector2f vel = new Vector2f();
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f point = getRandomPointInBounds(ship);
            Vector2f point2 = getRandomPointInBounds(ship);
            float size = 20f;
            //size = Misc.getHitGlowSize(size, projectile.getDamage().getBaseDamage(), damageResult);
            float sizeMult = (float)Math.random()+0.5f;
            float dur = 0.5f;
            float rampUp = 0f;
            Color c = new Color(30, 60, 20, 40);
            Color inv = new Color(200, 100, 180, 30);
            engine.addNebulaParticle(point, vel, size, 1f + sizeMult,
                    rampUp, 0f, dur, c);
            engine.addNegativeNebulaParticle(point2, vel, size, 2f,
                    rampUp, 0f, dur, inv);
            elapsed -= 0.1;
            if (elapsed<0) elapsed = 0;
        }
    }

    protected static void doDamage(ShipAPI ship, ShipAPI source) {
        CombatEngineAPI engine = Global.getCombatEngine();
        Vector2f point = getRandomPointInBounds(ship);
        float damage = (0.01f*HULL_DAMAGE_PERCENT*ship.getMaxHitpoints());
        engine.applyDamage(ship, point, damage, DamageType.ENERGY, 0, true, false, source, false);
    }

    public static class PestilenceArkData {

    }
    public static class PestilenceListener implements AdvanceableListener {
        protected ShipAPI ship;
        protected ShipAPI source;
        protected int stacks = STARTING_STACKS;
        protected float reInfectTimer = 10;
        public IntervalUtil interval = new IntervalUtil(0.1f, 0.5f);
        public PestilenceListener(ShipAPI ship, ShipAPI source) {
            this.ship = ship;
            this.source = source;
        }
        @Override
        public void advance(float amount) {
            interval.advance(amount);
            if (stacks > 0) {
                if (interval.intervalElapsed()) {
                    stacks--;
                    doPlagueParticles(ship, amount*ship.getMass());
                    doDamage(ship, source);
                    for (ShipAPI ally : AIUtils.getNearbyAllies(ship, INFECTION_RANGE)) {
                        if (ally.isAlive()) {
                            PestilenceListener pestilenceListener = getOrAddPestilenceListener(ally, source);
                            if (pestilenceListener.reInfectTimer < 0) {
                                pestilenceListener.stacks = STARTING_STACKS;
                                pestilenceListener.reInfectTimer = 10;
                            }
                        }
                    }
                }
            } else if (reInfectTimer >= 0) {
                reInfectTimer -= amount;
            }
        }
    }
}
