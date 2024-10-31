package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class AlienTargetingCore extends BaseHullMod {
    public static final float RANGE_INCREASE_PERCENT = 30f;
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, RANGE_INCREASE_PERCENT);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, RANGE_INCREASE_PERCENT);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return ((int) RANGE_INCREASE_PERCENT) + "%";
        } else {
            return null;
        }
    }
}
