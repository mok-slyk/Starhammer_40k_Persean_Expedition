package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class HecutorShell extends BaseHullMod {

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_broadside_batteries")) return false;
        if (ship.getVariant().hasHullMod("shpe_stygies_shell")) return false;
        if (ship.getVariant().hasHullMod("shpe_pyros_shell")) return false;
        if (ship.getVariant().hasHullMod("shpe_disruptor_shell")) return false;
        if (ship.getVariant().hasHullMod("shpe_staravar_shell")) return false;
        if (ship.getVariant().hasHullMod("shpe_shard_shell")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_broadside_batteries")) {
            return "Requires Integrated Broadside Batteries";
        }
        if (ship.getVariant().hasHullMod("shpe_stygies_shell")) {
            return "Incompatible with Macrocannon: Stygies Shells";
        }
        if (ship.getVariant().hasHullMod("shpe_disruptor_shell")) {
            return "Incompatible with Macrocannon: Disruptor Shells";
        }
        if (ship.getVariant().hasHullMod("shpe_pyros_shell")) {
            return "Incompatible with Macrocannon: Pyros Shells";
        }
        if (ship.getVariant().hasHullMod("shpe_staravar_shell")) {
            return "Incompatible with Macrocannon: Staravar Shells";
        }
        if (ship.getVariant().hasHullMod("shpe_shard_shell")) {
            return "Incompatible with Macrocannon: Shard Shells";
        }
        return "Incompatible";
    }
}
