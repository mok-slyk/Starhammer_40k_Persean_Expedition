package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;
import mok_slyk.shpe.scripts.utils.SHPEUtils;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectileEffectTracker implements EveryFrameCombatPlugin {
    private static Logger log = Global.getLogger(ProjectileEffectTracker.class);
    public static List<DamagingProjectileAPI> piercingProjectiles = new ArrayList<>();
    private List<DamagingProjectileAPI> piercingRemove = new ArrayList<>();
    public static List<DamagingProjectileAPI> pulseSubmunitionProjectiles = new ArrayList<>();
    private List<DamagingProjectileAPI> pulseSubmunitionsRemove = new ArrayList<>();
    public static List<DamagingProjectileAPI> railSubmunitionProjectiles = new ArrayList<>();
    private List<DamagingProjectileAPI> railSubmunitionsRemove = new ArrayList<>();

    public static void ensureProjectileEffectTracker(CombatEngineAPI engine) {
        if (!engine.hasPluginOfClass(ProjectileEffectTracker.class)) {
            engine.addPlugin(new ProjectileEffectTracker());
        }
    }

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {

    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine.isPaused()) return;
        handlePiercingProjectiles();
        handlePulseSubmunitions(amount);
        handleRailSubmunitions();
    }

    private void handlePulseSubmunitions(float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        for (DamagingProjectileAPI projectile : pulseSubmunitionProjectiles) {
            projectile.setCustomData("shpe_lifetime", (float) projectile.getCustomData().get("shpe_lifetime") + amount);
            if ((float) projectile.getCustomData().get("shpe_lifetime") > 0.3f) {
                if (!projectile.wasRemoved() && !projectile.didDamage() && !projectile.isFading()) {
                    log.info("submunitions burst");
                    Vector2f direction = VectorUtils.rotate(new Vector2f(1, 0), projectile.getFacing());
                    Vector2f point = new Vector2f(projectile.getLocation());
                    engine.addHitParticle(point, new Vector2f(), 25f, 3, 0.1f, new Color(150, 170, 255));
                    for (int i = 0; i < 12; i++) {
                        engine.spawnProjectile(
                                projectile.getSource(),
                                projectile.getWeapon(),
                                "shpe_submunitions_cannon_ref",
                                Vector2f.add(point, (Vector2f) new Vector2f(direction).scale((float) (Math.random() * 25f) - 7f), null),
                                (float) (VectorUtils.getFacing(direction) - 12 + 24 * Math.random()),
                                new Vector2f()
                        );
                    }
                }
                pulseSubmunitionsRemove.add(projectile);
                engine.removeEntity(projectile);
            }
        }
        pulseSubmunitionProjectiles.removeAll(pulseSubmunitionsRemove);
        pulseSubmunitionsRemove.clear();
    }

    private void handleRailSubmunitions() {
        CombatEngineAPI engine = Global.getCombatEngine();
        for (DamagingProjectileAPI projectile : railSubmunitionProjectiles) {
            List<ShipAPI> targets = CombatUtils.getShipsWithinRange(projectile.getLocation(), 160);
            ShipAPI best = null;
            Vector2f bestPoint = null;
            float bestCost = Float.MAX_VALUE;
            for (ShipAPI target : targets) {
                if (target.getOwner() == projectile.getOwner() || target.getHullSize() == ShipAPI.HullSize.FIGHTER) continue;
                Vector2f point = CollisionUtils.getNearestPointOnBounds(projectile.getLocation(), target);
                if (target.getShield() != null && target.getShield().isOn()) {
                    point = SHPEUtils.getClosestPointOnCircleInCone(projectile.getLocation(), projectile.getFacing(), 160, 25, target.getShieldCenterEvenIfNoShield(), target.getShieldRadiusEvenIfNoShield());
                }
                if (point == null) continue;

                float cost = Math.abs((VectorUtils.getAngle(projectile.getLocation(), point)-projectile.getFacing() + 540) % 360 -180);

                //float dist = MathUtils.getDistance(target, projectile);
                //float cost = angle + dist/10;
                if (best == null || cost < bestCost) {
                    bestCost = cost;
                    best = target;
                    bestPoint = point;
                }
            }
            log.info(bestCost);
            if (best == null || bestCost > 25) continue;
            Vector2f point = new Vector2f(projectile.getLocation());
            Vector2f direction = VectorUtils.getDirectionalVector(point, bestPoint);
            engine.addHitParticle(point, new Vector2f(), 25f, 3, 0.1f, new Color(250, 250, 125));
            for (int i = 0; i < 8; i++) {
                engine.spawnProjectile(
                        projectile.getSource(),
                        projectile.getWeapon(),
                        "shpe_submunitions_railgun_ref",
                        Vector2f.add(point, (Vector2f) new Vector2f(direction).scale((float) (Math.random()*30f)-15f), null),
                        (float) (VectorUtils.getFacing(direction)-12+24*Math.random()),
                        new Vector2f()
                );
            }
            railSubmunitionsRemove.add(projectile);
            engine.removeEntity(projectile);
        }
        railSubmunitionProjectiles.removeAll(railSubmunitionsRemove);
        railSubmunitionsRemove.clear();
    }
    private void handlePiercingProjectiles() {
        CombatEngineAPI engine = Global.getCombatEngine();
        for (DamagingProjectileAPI projectile : piercingProjectiles) {
            float range = (float) projectile.getCustomData().get("shpe_weaponRange");
            Vector2f origin = (Vector2f) projectile.getCustomData().get("shpe_origin");
            float travel = MathUtils.getDistance(origin, projectile.getLocation());
            if (travel > range*1.1f) {
                log.info("scheduling deletion");
                piercingRemove.add(projectile);
                engine.removeEntity(projectile);
            }
        }
        piercingProjectiles.removeAll(piercingRemove);
        piercingRemove.clear();
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {

    }

    @Override
    public void init(CombatEngineAPI engine) {}
}
