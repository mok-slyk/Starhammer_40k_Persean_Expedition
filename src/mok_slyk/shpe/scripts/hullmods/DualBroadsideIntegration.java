package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.campaign.fleet.FleetData;

import java.util.Iterator;
import java.util.List;

@Deprecated
public class DualBroadsideIntegration  extends BaseHullMod {

    public static final float COST_REDUCTION  = 10;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //stats.getDynamic().getMod(Stats.LARGE_BALLISTIC_MOD).modifyFlat(id, -COST_REDUCTION);
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        List<WeaponAPI> weapons = ship.getAllWeapons();
        for (WeaponAPI weapon : weapons) {
           WeaponSlotAPI slot = weapon.getSlot();
           switch (slot.getId()) {
               case "MS0001":
                   //
           }
        }


    }

}
