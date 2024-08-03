package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class JovianCannon extends BaseHullMod {
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_novacannon")) return false;
        if (ship.getVariant().hasHullMod("shpe_ryza_nova")) return false;
        if (ship.getVariant().hasHullMod("shpe_lathe_nova")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_novacannon")) {
            return "Requires Novacannon";
        }
        if (ship.getVariant().hasHullMod("shpe_ryza_cannon")) {
            return "Incompatible with Novacannon: Ryza Shells";
        }
        if (ship.getVariant().hasHullMod("shpe_lathe_cannon")) {
            return "Incompatible with Novacannon: Lathe Shells";
        }
        return "Incompatible";
    }
}
