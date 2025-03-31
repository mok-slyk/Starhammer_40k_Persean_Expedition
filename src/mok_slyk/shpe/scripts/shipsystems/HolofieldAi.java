package mok_slyk.shpe.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.opengl.DrawUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HolofieldAi implements ShipSystemAIScript {
    private static final Logger LOG = Global.getLogger(HolofieldAi.class);
    public static float DAMAGE_TO_THREAT_MULT = 1f;
    public static float ENEMY_THREAT_RANGE = 2000f;
    public static float THREAT_REQUIRED = 200f;
    private ShipAPI ship;
    private CombatEngineAPI engine;
    private ShipwideAIFlags flags;
    private ShipSystemAPI system;
    private IntervalUtil tracker = new IntervalUtil(0.5f, 1f);

    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.flags = flags;
        this.engine = engine;
        this.system = ship.getPhaseCloak();
    }

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        tracker.advance(amount);
        //System.out.println("huh");
        if (tracker.intervalElapsed()){
            float totalThreat = 0f;
            //consider threat from projectiles
            List<DamagingProjectileAPI> threateningProjectiles  = new ArrayList<>();
            for (MissileAPI missile: AIUtils.getNearbyEnemyMissiles(ship, 1000)) {
                if (missile.getDamageAmount() >= 100 && (missile.isGuided() || MathUtils.getShortestRotation(missile.getFacing(), VectorUtils.getAngle(missile.getLocation(), ship.getLocation())) < 35)) {
                    threateningProjectiles.add(missile);
                }
            }
            for (DamagingProjectileAPI projectile: CombatUtils.getProjectilesWithinRange(ship.getLocation(), 500)) {
                if (MathUtils.getShortestRotation(projectile.getFacing(), VectorUtils.getAngle(projectile.getLocation(), ship.getLocation())) < 35){
                    threateningProjectiles.add(projectile);
                }
            }
            float missileThreat = 0f;
            for (DamagingProjectileAPI projectile: threateningProjectiles) {
                missileThreat += projectile.getDamageAmount();
            }
            totalThreat += missileThreat * DAMAGE_TO_THREAT_MULT;
            //consider threat from ships
            float enemyThreat = 0f;
            for (ShipAPI enemy:AIUtils.getNearbyEnemies(ship, ENEMY_THREAT_RANGE)) {
                if (enemy.isDrone() || enemy.isFighter()) continue;
                enemyThreat += enemy.getHullSpec().getFleetPoints()/MathUtils.getDistance(ship, enemy)*10000;
            }
            totalThreat += enemyThreat;
            //factor how low the ship is into threat
            totalThreat = totalThreat * Math.min(5, 1/ship.getHullLevel());

            //TEST:
            //engine.addFloatingText(ship.getLocation(), String.valueOf(totalThreat), 1000f, Color.white, ship, 1f, 0.5f);
            //engine.addFloatingTextAlways(ship.getLocation(), String.valueOf(totalThreat), 100f, Color.white, ship, 1f,1f, 0.5f, 0f, 0f, 255f);
            //ship.getFluxTracker().showOverloadFloatyIfNeeded(String.valueOf(totalThreat), Color.white, 4f, true);

            if (system.isOn()){
                //decide whether to use the system based on threat
                if (totalThreat < THREAT_REQUIRED*0.75f) {
                    ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
                }
            } else  if (system.canBeActivated()) {
                //decide whether to deactivate the system based on threat
                if (totalThreat >= THREAT_REQUIRED) {
                    ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
                }
            }
        }
    }
}
