package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;

@Deprecated
public class VariableMacrocannonOnFireEffect implements OnFireEffectPlugin {

    public final float STYGIES_INACCURACY = 5f;

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        String weaponID = "";
        ShipAPI ship = weapon.getShip();
        int inaccuracyMult = 0;
        if (ship.getVariant().hasHullMod("shpe_stygies_shell")) {
            weaponID = "shpe_stygies_reference";
            inaccuracyMult = 1;
        } else if (ship.getVariant().hasHullMod("shpe_hecutor_shell")) {
            weaponID = "shpe_hecutor_reference";
        } else if (ship.getVariant().hasHullMod("shpe_pyros_shell")) {
            weaponID = "shpe_pyros_reference";
        } else if (ship.getVariant().hasHullMod("shpe_disruptor_shell")) {
            weaponID = "shpe_disruptor_reference";
        } else {
            weaponID = "shpe_marsvi";
        }

        //create projectile
        float angleOffset = inaccuracyMult*MathUtils.getRandomNumberInRange(-STYGIES_INACCURACY/2, STYGIES_INACCURACY/2);
        DamagingProjectileAPI newProjectile = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, weaponID, projectile.getLocation(), weapon.getCurrAngle()+angleOffset, ship.getVelocity());
        engine.removeEntity(projectile);
    }
}
