package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class SlaaneshMark extends BaseHullMod {
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        super.applyEffectsAfterShipCreation(ship, id);
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_khorne_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("khorne_mark")) {
            return "Incompatible with Mark of Khorne";
        }
        if (ship.getVariant().hasHullMod("tzeentch_mark")) {
            return "Incompatible with Mark of Tzeentch";
        }
        if (ship.getVariant().hasHullMod("nurgle_mark")) {
            return "Incompatible with Mark of Nurgle";
        }
        return "Incompatible";
    }
}
