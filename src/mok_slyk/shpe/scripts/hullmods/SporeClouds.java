package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import org.magiclib.util.MagicUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SporeClouds extends BaseHullMod {
    public static List<SpriteAPI> spores = null;
    //public static SpriteAPI testSpore = Global.getSettings().getSprite("spores", "spores_0001");
    //public int frame;
    private double elapsed;
    private static final float AMOUNT_PER_FRAME = 1f;

    public static final float HEALTH_MULT = 3f;
    public static final float HEALTH_REGEN = 0.02f;

    public static final float DAMAGE_MULT = 0.1f;

    public static final float RANGE_MULT = 1.3f;

    public static final float REGEN_CD = 3.0f;

    public static final float SIZE_MULT = 4f;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new SporeClouds.SporeCloudListener(ship, ship.getMass()));
    }
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);
    }


    private static List<SpriteAPI> makeSwarmList(String category, String prefix, int length) {
        List<SpriteAPI> list = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            String numString = String.valueOf(i);
            int numLength = numString.length();
            StringBuilder key = new StringBuilder(prefix);
            for (int j = 0; j < 4-numLength; j++) {
                key.append("0");
            }
            key.append(numString);
            list.add(Global.getSettings().getSprite(category, key.toString()));
        }
        return list;
    }


    public static class SporeCloudListener implements AdvanceableListener, DamageTakenModifier {
        protected ShipAPI ship;
        protected float maxHealth;
        protected float health;
        private final IntervalUtil frameTimer = new IntervalUtil(0.03f, 0.04f);
        private final IntervalUtil regenTimer = new IntervalUtil(0.05f, 0.05f);

        private float timeSinceDeath = 0f;
        private int frame = 1;
        public SporeCloudListener(ShipAPI ship, float mass) {
            this.ship = ship;
            this.maxHealth = mass* HEALTH_MULT;
            this.health = maxHealth;
        }
        @Override
        public void advance(float amount) {
            float percentage = health/maxHealth;
            boolean isAlive = ship.isAlive() && percentage > 0.01f;
            regenTimer.advance(amount);
            boolean doMechanics = regenTimer.intervalElapsed();

            if (isAlive) {
                float range = Math.max(ship.getSpriteAPI().getHeight(), ship.getSpriteAPI().getWidth()) * RANGE_MULT;
                float mechanicRange = range * 0.3f;
                advanceVisuals(ship, amount, range * 2, range * 2, percentage);

                for (ShipAPI enemy : AIUtils.getNearbyEnemies(ship, mechanicRange)) {
                    float enemyRange = Math.max(enemy.getSpriteAPI().getHeight(), enemy.getSpriteAPI().getWidth()) * 0.5f;
                    advanceVisuals(enemy, amount, enemyRange * 2, enemyRange * 2, percentage * 0.5f);
                    if (doMechanics) {
                        Vector2f point = CollisionUtils.getNearestPointOnBounds(ship.getLocation(), enemy);
                        Global.getCombatEngine().applyDamage(enemy, point, health * DAMAGE_MULT, DamageType.FRAGMENTATION, 0f, true, false, ship, true);
                    }
                }

                for (MissileAPI missile : AIUtils.getNearbyEnemyMissiles(ship, mechanicRange*0.9f)) {
                    float damageDealt = Math.max(health * DAMAGE_MULT, missile.getHitpoints())*0.5f;
                    if (damageDealt > health) {
                        health = 0;
                    } else {
                        health -= damageDealt;
                    }
                    Global.getCombatEngine().addFloatingDamageText(missile.getLocation(), damageDealt, new Color(141, 60, 211, 255), ship, null);
                    Global.getCombatEngine().applyDamage(missile, missile.getLocation(), health * DAMAGE_MULT, DamageType.FRAGMENTATION, 0f, true, false, ship, true);
                }

                for (CombatEntityAPI entity: CombatUtils.getEntitiesWithinRange(ship.getLocation(), mechanicRange)) {
                    if (entity.getOwner() == ship.getOwner() || !(entity instanceof ShipAPI) || !(((ShipAPI)entity).isFighter())) {
                        continue;
                    }
                    Global.getCombatEngine().applyDamage(entity, entity.getLocation(), health * DAMAGE_MULT, DamageType.FRAGMENTATION, 0f, true, false, ship, true);
                }

            } else {
                timeSinceDeath += amount;
            }

            if (isAlive || timeSinceDeath > REGEN_CD) {
                if (doMechanics) {
                    health = Math.min(maxHealth, health + maxHealth*HEALTH_REGEN);
                    if (health/maxHealth > 0.01f) {
                        timeSinceDeath = 0;
                    }
                }
            }

            if (ship.isAlive()) {
                float healthFraction = health/maxHealth;
                MagicUI.drawInterfaceStatusBar(ship, healthFraction, Misc.getPositiveHighlightColor(), Misc.getPositiveHighlightColor(), 1f, "Spores", Math.round(health));
            }
        }

        protected void advanceVisuals(ShipAPI ship, float amount, float width, float height, float intensity) {
            if (spores == null) {
                spores = makeSwarmList("spores", "spores_", 100);
            }
            frameTimer.advance(amount);
            if (frameTimer.intervalElapsed()) {
                frame++;
            }
            if (frame > spores.size()) {
                frame -= spores.size();
            }

            SpriteAPI shadow = Global.getSettings().getSprite("fx", "spore_shadow");

            Vector2f sizeVec = new Vector2f(width, height);
            //SpriteAPI testSpore = Global.getSettings().getSprite("spores", "spores_0001");
            MagicRender.singleframe(shadow, ship.getLocation(), sizeVec, ship.getFacing()-90f, new Color(255, 255,255, (int)(90*intensity+10)), false);
            MagicRender.singleframe(spores.get(frame-1), ship.getLocation(), sizeVec, ship.getFacing()-90f, new Color(255, 255,255,(int)(204*intensity+50)), false );

            //MagicRender.singleframe(testSpore, ship.getLocation(), sizeVec, ship.getFacing()-90f, new Color(255, 255,255,255), false );
        }


        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if (target == ship && damage.getType() != DamageType.OTHER) {
                float reduced = Math.min(health, damage.getDamage());
                Global.getCombatEngine().addFloatingDamageText(point, reduced, new Color(141, 60, 211, 255), target, null);
                if (damage.getDamage() > health) {
                    damage.getModifier().modifyFlat("spores", -health);
                    health = 0;
                } else {
                    health -= damage.getDamage();
                    damage.getModifier().modifyMult("spores", 0f);
                }
                return "spores";
            }
            return null;
        }
    }
}

