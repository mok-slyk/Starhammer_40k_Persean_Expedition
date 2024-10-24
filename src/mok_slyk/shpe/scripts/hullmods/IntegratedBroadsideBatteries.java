package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.WeaponSlotAPI;

import java.util.*;

public class IntegratedBroadsideBatteries extends BaseHullMod {

    public final String DEFAULT_STATS_ID = "shpe_marsvi";
    public final String PREFIX = "shpe_";
    public final String SUFFIX = "_reference";
    public final Set<String> VALID_IDS = new HashSet<>(Arrays.asList("shpe_stygies_shell", "shpe_pyros_shell", "shpe_disruptor_shell", "shpe_hecutor_shell", "shpe_staravar_shell",
            "shpe_shard_shell", "shpe_nurgle_hives", "shpe_atomic_shell"));
    public final int MAX_SLOT_COUNT = 24; //last slot: BIB23

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
                weaponSpecID = "shpe_stygies_reference";
                break;
            case "shpe_pyros_shell":
                weaponSpecID = "shpe_pyros_reference";
                break;
            case "shpe_hecutor_shell":
                weaponSpecID = "shpe_hecutor_reference";
                break;
            case "shpe_disruptor_shell":
                weaponSpecID = "shpe_disruptor_reference";
                break;
            case "shpe_staravar_shell":
                weaponSpecID = "shpe_staravar_reference";
                break;
            case "shpe_shard_shell":
                weaponSpecID = "shpe_shard_reference";
                break;
            case "shpe_nurgle_hives":
                weaponSpecID = "shpe_hive";
                break;
            case "shpe_atomic_shell":
                weaponSpecID = "shpe_atomic_reference";
                break;
            default:
                weaponSpecID = "shpe_marsvi_builtin";
        }
        Collection<WeaponSlotAPI> slots = stats.getVariant().getHullSpec().getAllWeaponSlotsCopy();

        for (int i = 0; i < MAX_SLOT_COUNT; i++) {
            String slotID = "BIB" + i;

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
