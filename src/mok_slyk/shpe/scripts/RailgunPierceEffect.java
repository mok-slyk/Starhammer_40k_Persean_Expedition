package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import mok_slyk.shpe.scripts.utils.SHPEUtils;

import static mok_slyk.shpe.scripts.ProjectileEffectTracker.ensureProjectileEffectTracker;

public class RailgunPierceEffect implements OnHitEffectPlugin, OnFireEffectPlugin {
    private static Logger log = Global.getLogger(RailgunPierceEffect.class);
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (shieldHit || point == null) return;
        if (!projectile.getCustomData().containsKey("shpe_weaponRange") || !projectile.getCustomData().containsKey("shpe_origin")) return;
        if (projectile.getCustomData().containsKey("shpe_pierceCount") && (int) projectile.getCustomData().get("shpe_pierceCount") > 1) return;
        if (projectile.getVelocity().length() < 0.0001f) return;
        Vector2f rayStart = new Vector2f();
        Vector2f direction = (Vector2f) new Vector2f(projectile.getVelocity()).normalise();
        Vector2f.add(point, (Vector2f) new Vector2f(direction).scale(1000f), rayStart);
        Vector2f exit = SHPEUtils.getLineEntityCollisionPoint(target, rayStart, point);
        if (exit != null) {
            Vector2f.add(exit, (Vector2f) new Vector2f(direction).scale(7f), exit);
            log.info("found exit");
            // engine.addFloatingText(exit, "exit", 20, Color.red, null, 0, 0);
            DamagingProjectileAPI proj = (DamagingProjectileAPI) engine.spawnProjectile(
                    projectile.getSource(),
                    projectile.getWeapon(),
                    "shpe_high_cap_railgun",
                    exit,
                    VectorUtils.getFacing(projectile.getVelocity()),
                    new Vector2f(0,0)
            );
            proj.setCustomData("shpe_weaponRange", projectile.getCustomData().get("shpe_weaponRange"));
            proj.setCustomData("shpe_origin", projectile.getCustomData().get("shpe_origin"));
            proj.setCustomData("shpe_pierceCount", (int) projectile.getCustomData().get("shpe_pierceCount") + 1);
            ProjectileEffectTracker.piercingProjectiles.remove(projectile);
            ProjectileEffectTracker.piercingProjectiles.add(proj);

            engine.spawnMuzzleFlashOrSmoke(projectile.getSource(), Vector2f.add(exit, (Vector2f) new Vector2f(direction).scale(-10f), null), Global.getSettings().getWeaponSpec("shpe_pierce_effect_ref"), VectorUtils.getFacing(projectile.getVelocity()));
            engine.spawnMuzzleFlashOrSmoke(projectile.getSource(), Vector2f.add(exit, (Vector2f) new Vector2f(direction).scale(-10f), null), Global.getSettings().getWeaponSpec("shpe_high_cap_railgun"), VectorUtils.getFacing(projectile.getVelocity()));

            // Vector2f exit2 = Vector2f.add(exit, (Vector2f) new Vector2f(direction).scale(40f), null);
            for (int i = 0; i < 6; i++) {
                engine.spawnProjectile(
                    projectile.getSource(),
                    projectile.getWeapon(),
                    "shpe_pierce_effect_ref",
                    Vector2f.add(exit, (Vector2f) new Vector2f(direction).scale((float) (Math.random()*25f)-7f), null),
                    (float) (VectorUtils.getFacing(direction)-15+30*Math.random()),
                    new Vector2f()
                );
            }
        } else {
            log.info("no exit");
        }
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        log.info("fire");
        ensureProjectileEffectTracker(engine);
        projectile.setCustomData("shpe_weaponRange" , weapon.getRange());
        projectile.setCustomData("shpe_origin" , new Vector2f(projectile.getLocation()));
        projectile.setCustomData("shpe_pierceCount", 0);
        ProjectileEffectTracker.piercingProjectiles.add(projectile);
    }
}
