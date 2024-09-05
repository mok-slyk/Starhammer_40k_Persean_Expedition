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
        ship.getMutableStats().getEnergyWeaponDamageMult().modifyFlat("shpe_khorne_mark", totalPlusPercent*0.01f);
        ship.getMutableStats().getBallisticWeaponDamageMult().modifyFlat("shpe_khorne_mark", totalPlusPercent*0.01f);
        ship.getMutableStats().getMissileWeaponDamageMult().modifyFlat("shpe_khorne_mark", totalPlusPercent*0.01f);
        super.advanceInCombat(ship, amount);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 2 && ChaosGodsEventIntel.get() != null && ChaosGodsEventIntel.get().gods[0].isStageActive(GodEventIntel.Stage.GIFT_1)) {
            return "Weapon damage is increased by an additional "+EXTRA_DAMAGE_PERCENT+"% if the ship's hull is below 60%.";
        } else if (index == 0) {
            return RANGE_REDUCTION_PERCENT + "%";
        } else if (index == 1) {
            return DAMAGE_INCREASE_PERCENT + "%";
        } else {
            return null;
        }
    }
}
