package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class SlaaneshMark extends BaseHullMod {
    public static float SPEED_BUFF = 10f;
    public static float HULL_DEBUFF = 10f;
    public static float FLUX_BUFF = 10f;
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getHullBonus().modifyMult(id, 1 - HULL_DEBUFF * 0.01f);
        stats.getMaxSpeed().modifyMult(id, 1 + (stats.getVariant().hasHullMod("shpe_hellforged") ? SPEED_BUFF * Hellforged.MARK_EFFECT_MULT : SPEED_BUFF) * 0.01f);
        stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1 - (stats.getVariant().hasHullMod("shpe_hellforged") ? FLUX_BUFF * Hellforged.MARK_EFFECT_MULT : FLUX_BUFF) * 0.01f);
        stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1 - (stats.getVariant().hasHullMod("shpe_hellforged") ? FLUX_BUFF * Hellforged.MARK_EFFECT_MULT : FLUX_BUFF) * 0.01f);
        stats.getMissileWeaponFluxCostMod().modifyMult(id, 1 - (stats.getVariant().hasHullMod("shpe_hellforged") ? FLUX_BUFF * Hellforged.MARK_EFFECT_MULT : FLUX_BUFF) * 0.01f);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) {
            return ((int) (ship.getVariant().hasHullMod("shpe_hellforged") ? SPEED_BUFF * Hellforged.MARK_EFFECT_MULT : SPEED_BUFF)) + "%";
        }
        if (index == 1) {
            return ((int) (ship.getVariant().hasHullMod("shpe_hellforged") ? FLUX_BUFF * Hellforged.MARK_EFFECT_MULT : FLUX_BUFF)) + "%";
        }
        if (index == 2) {
            return ((int) HULL_DEBUFF) + "%";
        }
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_khorne_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_khorne_mark")) {
            return "Incompatible with Mark of Khorne";
        }
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) {
            return "Incompatible with Mark of Tzeentch";
        }
        if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) {
            return "Incompatible with Mark of Nurgle";
        }
        return "Incompatible";
    }
}
