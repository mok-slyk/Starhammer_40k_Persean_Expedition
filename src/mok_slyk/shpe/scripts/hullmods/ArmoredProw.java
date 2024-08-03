package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;

import java.util.Collection;

public class ArmoredProw extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getEntity() == null) {
            return;
        }
        ShipAPI ship = ((ShipAPI)stats.getEntity());
        String hullId = ship.getHullSpec().getHullId();
        String moduleId;
        switch (hullId) {
            case "shpe_retribution":
                moduleId = "shpe_battleship_torpedo_prow_Variant";
                break;
            case "shpe_apocalypse":
                moduleId = "shpe_battleship_nova_prow_Variant";
                break;
            default:
                moduleId = "shpe_battleship_nova_prow_Variant";
                //throw new IllegalArgumentException();
        }

        Collection<String> slots = stats.getVariant().getModuleSlots();
        ShipVariantAPI variant = Global.getSettings().getVariant(moduleId);
        String slotID = "PROW";
        //stats.getVariant().setModuleVariant(slotID, variant);
        //stats.getVariant().setModuleVariant(slotID, variant);
        //((ShipAPI) stats.getEntity()).getVariant().setModuleVariant(slotID, variant);
    }
}
