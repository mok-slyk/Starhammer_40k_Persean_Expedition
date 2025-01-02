package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class NurgleMark extends BaseHullMod {
    public static float EMP_RESISTANCE = 30f;
    public static float HULL_BUFF = 30f;
    public static float SPEED_DEBUFF = 20f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - (stats.getVariant().hasHullMod("shpe_hellforged") ? EMP_RESISTANCE * Hellforged.MARK_EFFECT_MULT : EMP_RESISTANCE) * 0.01f);
        stats.getHullBonus().modifyMult(id, 1f + (stats.getVariant().hasHullMod("shpe_hellforged") ? HULL_BUFF * Hellforged.MARK_EFFECT_MULT : HULL_BUFF) * 0.01f);
        stats.getMaxSpeed().modifyMult(id, 1f - SPEED_DEBUFF * 0.01f);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) {
            return ((int) (ship.getVariant().hasHullMod("shpe_hellforged") ? HULL_BUFF * Hellforged.MARK_EFFECT_MULT : HULL_BUFF)) + "%";
        }
        if (index == 1) {
            return ((int) (ship.getVariant().hasHullMod("shpe_hellforged") ? EMP_RESISTANCE * Hellforged.MARK_EFFECT_MULT : EMP_RESISTANCE)) + "%";
        }
        if (index == 2) {
            return ((int) SPEED_DEBUFF) + "%";
        }
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_khorne_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_slaanesh_mark")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_khorne_mark")) {
            return "Incompatible with Mark of Khorne";
        }
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) {
            return "Incompatible with Mark of Tzeentch";
        }
        if (ship.getVariant().hasHullMod("shpe_slaanesh_mark")) {
            return "Incompatible with Mark of Slaanesh";
        }
        return "Incompatible";
    }
}
