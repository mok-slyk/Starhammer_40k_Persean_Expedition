package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VariableTorpedoTubes extends BaseHullMod {
    public final String DEFAULT_STATS_ID = "shpe_plasmatorp_reference";
    public final String PREFIX = "shpe_";
    public final String SUFFIX = "_reference";
    public final Set<String> VALID_IDS = new HashSet<>(Arrays.asList("shpe_melta_warheads", "shpe_seeking_warheads", "shpe_vortex_warheads", "shpe_barrage_warheads"));
    public final int MAX_SLOT_COUNT = 8; //last slot: BIT23

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getEntity() == null) {
            return;
        }
        boolean weaponModified = false; //whether the ship has a relevant hullmod
        String relevantHullmodID = "nope";
        for (String hullModId: VALID_IDS) {
            if (stats.getVariant().hasHullMod(hullModId)) {
                weaponModified = true;
                relevantHullmodID = hullModId;
            }
        }
        String weaponSpecID;
        switch (relevantHullmodID) {
            case "shpe_melta_warheads":
                weaponSpecID = "shpe_meltatorp_reference";
                break;
            case "shpe_seeking_warheads":
                weaponSpecID = "shpe_seekingtorp_reference";
                break;
            case "shpe_vortex_warheads":
                weaponSpecID = "shpe_vortextorp_reference";
                break;
            case "shpe_barrage_warheads":
                weaponSpecID = "shpe_barragetorp_reference";
                break;
            default:
                weaponSpecID = DEFAULT_STATS_ID;
        }
        Collection<WeaponSlotAPI> slots = stats.getVariant().getHullSpec().getAllWeaponSlotsCopy();

        for (int i = 0; i < MAX_SLOT_COUNT; i++) {
            String slotID = "BIT" + i;

            boolean slotExists = false;
            for (WeaponSlotAPI slot: slots) {
                if (slot.getId().equals(slotID)) {
                    stats.getVariant().clearSlot(slotID);
                    stats.getVariant().addWeapon(slotID, weaponSpecID);
                    slotExists = true;
                    break;
                }
            }
            if (!slotExists) {
                break;
            }
        }
    }
}
