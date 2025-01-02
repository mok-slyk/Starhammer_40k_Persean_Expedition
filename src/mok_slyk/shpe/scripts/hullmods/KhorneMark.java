package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import mok_slyk.shpe.scripts.campaign.ChaosGodsEventIntel;
import mok_slyk.shpe.scripts.campaign.GodEventIntel;

public class KhorneMark extends BaseHullMod {
    public static float RANGE_REDUCTION_PERCENT = 10;
    public static float DAMAGE_INCREASE_PERCENT = 20;
    public static float HULL_FRACTION_THRESHOLD = 0.6f;
    public static float EXTRA_DAMAGE_PERCENT = 20;
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float totalPlusPercent = DAMAGE_INCREASE_PERCENT;
        if (ChaosGodsEventIntel.get() != null && ChaosGodsEventIntel.get().gods[0].isStageActive(GodEventIntel.Stage.GIFT_1)) {
            if (ship.getHullLevel() <= HULL_FRACTION_THRESHOLD) {
                totalPlusPercent += EXTRA_DAMAGE_PERCENT;
            }
        }
        if (ship.getVariant().hasHullMod("shpe_hellforged")){
            totalPlusPercent *= Hellforged.MARK_EFFECT_MULT;
        }
        ship.getMutableStats().getEnergyWeaponDamageMult().modifyFlat("shpe_khorne_mark", totalPlusPercent*0.01f);
        ship.getMutableStats().getBallisticWeaponDamageMult().modifyFlat("shpe_khorne_mark", totalPlusPercent*0.01f);
        ship.getMutableStats().getMissileWeaponDamageMult().modifyFlat("shpe_khorne_mark", totalPlusPercent*0.01f);
        super.advanceInCombat(ship, amount);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 2 && ChaosGodsEventIntel.get() != null && ChaosGodsEventIntel.get().gods[0].isStageActive(GodEventIntel.Stage.GIFT_1)) {
            return "Weapon damage is increased by an additional "+(ship.getVariant().hasHullMod("shpe_hellforged") ? EXTRA_DAMAGE_PERCENT * Hellforged.MARK_EFFECT_MULT : EXTRA_DAMAGE_PERCENT)+"% if the ship's hull is below 60%.";
        } else if (index == 0) {
            return (ship.getVariant().hasHullMod("shpe_hellforged") ? RANGE_REDUCTION_PERCENT * Hellforged.MARK_EFFECT_MULT : RANGE_REDUCTION_PERCENT) + "%";
        } else if (index == 1) {
            return (ship.getVariant().hasHullMod("shpe_hellforged") ? DAMAGE_INCREASE_PERCENT * Hellforged.MARK_EFFECT_MULT : DAMAGE_INCREASE_PERCENT) + "%";
        } else {
            return "";
        }
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_slaanesh_mark")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) {
            return "Incompatible with Mark of Tzeentch";
        }
        if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) {
            return "Incompatible with Mark of Nurgle";
        }
        if (ship.getVariant().hasHullMod("shpe_slaanesh_mark")) {
            return "Incompatible with Mark of Slaanesh";
        }
        return "Incompatible";
    }
}
