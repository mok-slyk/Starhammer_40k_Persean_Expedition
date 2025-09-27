package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.apache.log4j.Logger;

public class SubmunitionsCannonEffect implements OnFireEffectPlugin {
    private static Logger log = Global.getLogger(SubmunitionsCannonEffect.class);
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ProjectileEffectTracker.ensureProjectileEffectTracker(engine);
        log.info("submunitions fire");
        projectile.setCustomData("shpe_lifetime", 0f);
        ProjectileEffectTracker.pulseSubmunitionProjectiles.add(projectile);
        new BurstOnFireEffect().onFire(projectile, weapon, engine);
    }
}
