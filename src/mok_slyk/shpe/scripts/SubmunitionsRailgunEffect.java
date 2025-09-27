package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import org.apache.log4j.Logger;

public class SubmunitionsRailgunEffect implements OnFireEffectPlugin {
    private static Logger log = Global.getLogger(SubmunitionsRailgunEffect.class);
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ProjectileEffectTracker.ensureProjectileEffectTracker(engine);
        ProjectileEffectTracker.railSubmunitionProjectiles.add(projectile);
    }
}
