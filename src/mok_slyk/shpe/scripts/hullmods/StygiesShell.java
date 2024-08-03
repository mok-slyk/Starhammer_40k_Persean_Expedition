package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.campaign.fleet.FleetData;

import java.util.List;

public class StygiesShell extends BaseHullMod {

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_broadside_batteries")) return false;
        if (ship.getVariant().hasHullMod("shpe_hecutor_shell")) return false;
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
        if (ship.getVariant().hasHullMod("shpe_hecutor_shell")) {
            return "Incompatible with Macrocannon: Hecutor Shells";
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
