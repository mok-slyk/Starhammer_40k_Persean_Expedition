package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.lazywizard.lazylib.MathUtils;

public class VortexWarheads extends BaseHullMod {

    public void advanceInCombat(ShipAPI ship, float amount) {
        for (WeaponAPI weapon: ship.getAllWeapons()) {
            if(weapon.getId().equals("shpe_vortextorp_reference")) {
                weapon.setMaxAmmo(Math.round(ship.getMutableStats().getMissileAmmoBonus().getPercentMod()/100f+1));
                //test:
                WeaponSpecAPI stats = weapon.getSpec();
            }
        }
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_variable_torpedo_tubes")) return false;
        if (ship.getVariant().hasHullMod("shpe_melta_warheads")) return false;
        if (ship.getVariant().hasHullMod("shpe_barrage_warheads")) return false;
        if (ship.getVariant().hasHullMod("shpe_seeking_warheads")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_variable_torpedo_tubes")) {
            return "Requires Variable Torpedo Tubes";
        }
        if (ship.getVariant().hasHullMod("shpe_vortex_warheads")) {
            return "Incompatible with Torpedos: Vortex Warheads";
        }
        if (ship.getVariant().hasHullMod("shpe_barrage_warheads")) {
            return "Incompatible with Torpedos: Barrage Warheads";
        }
        if (ship.getVariant().hasHullMod("shpe_seeking_warheads")) {
            return "Incompatible with Torpedos: Seeking Warheads";
        }
        return "Incompatible";
    }
}
