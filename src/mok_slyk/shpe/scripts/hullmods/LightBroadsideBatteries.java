package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LightBroadsideBatteries extends BaseHullMod {

    public final String DEFAULT_STATS_ID = "shpe_marsvi";
    public final String PREFIX = "shpe_";
    public final String SUFFIX = "_reference";
    public final Set<String> VALID_IDS = new HashSet<>(Arrays.asList("shpe_stygies_shell", "shpe_pyros_shell", "shpe_disruptor_shell", "shpe_hecutor_shell", "shpe_staravar_shell", "shpe_shard_shell", "shpe_nurgle_hives"));
    public final int MAX_SLOT_COUNT = 36; //last slot: BIL35

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
            case "shpe_stygies_shell":
                weaponSpecID = "shpe_light_stygies";
                break;
            case "shpe_pyros_shell":
                weaponSpecID = "shpe_light_pyros";
                break;
            case "shpe_hecutor_shell":
                weaponSpecID = "shpe_light_hecutor";
                break;
            case "shpe_disruptor_shell":
                weaponSpecID = "shpe_light_disruptor";
                break;
            case "shpe_staravar_shell":
                weaponSpecID = "shpe_light_staravar";
                break;
            case "shpe_shard_shell":
                weaponSpecID = "shpe_light_shard";
                break;
            case "shpe_nurgle_hives":
                weaponSpecID = "shpe_light_hive";
                break;
            default:
                weaponSpecID = "shpe_light_macro";
        }
        Collection<WeaponSlotAPI> slots = stats.getVariant().getHullSpec().getAllWeaponSlotsCopy();

        for (int i = 0; i < MAX_SLOT_COUNT; i++) {
            String slotID = "BIL" + i;

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
