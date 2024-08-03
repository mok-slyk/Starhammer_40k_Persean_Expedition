package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class BarrageWarheads extends BaseHullMod {
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_variable_torpedo_tubes")) return false;
        if (ship.getVariant().hasHullMod("shpe_vortex_warheads")) return false;
        if (ship.getVariant().hasHullMod("shpe_melta_warheads")) return false;
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
        if (ship.getVariant().hasHullMod("shpe_melta_warheads")) {
            return "Incompatible with Torpedos: Melta Warheads";
        }
        if (ship.getVariant().hasHullMod("shpe_seeking_warheads")) {
            return "Incompatible with Torpedos: Seeking Warheads";
        }
        return "Incompatible";
    }
}
