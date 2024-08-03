package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
@Deprecated
public class VariableTorptubeOnFireEffect implements OnFireEffectPlugin {

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        String weaponID = "";
        ShipAPI ship = weapon.getShip();
        if (ship.getVariant().hasHullMod("shpe_melta_warheads")) {
            weaponID = "shpe_meltatorp_reference";
        } else if (ship.getVariant().hasHullMod("shpe_vortex_warheads")) {
            weaponID = "shpe_vortextorp_reference";
        } else if (ship.getVariant().hasHullMod("shpe_barrage_warheads")) {
            weaponID = "shpe_barragetorp_reference";
        } else if (ship.getVariant().hasHullMod("shpe_seeking_warheads")) {
            weaponID = "shpe_seekingtorp_reference";
        } else {
            weaponID = "shpe_plasmatorp_reference";
        }

        //create projectile
        DamagingProjectileAPI newProjectile = (DamagingProjectileAPI) engine.spawnProjectile(ship, weapon, weaponID, projectile.getLocation(), weapon.getCurrAngle(), ship.getVelocity());
        engine.removeEntity(projectile);
    }
}
