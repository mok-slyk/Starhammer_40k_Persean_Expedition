package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

import java.util.ArrayList;
import java.util.List;

public class TzeentchMark extends BaseHullMod {
    static String ID = "shpe_tzeentch_mark";
    static float DAMAGE_INCREASE_PERCENT = 10;
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (ShipAPI fighter: getFighters(ship)) {
            fighter.getMutableStats().getEnergyWeaponDamageMult().modifyFlat(ID, DAMAGE_INCREASE_PERCENT*0.01f);
            fighter.getMutableStats().getBallisticWeaponDamageMult().modifyFlat(ID, DAMAGE_INCREASE_PERCENT*0.01f);
            fighter.getMutableStats().getMissileWeaponDamageMult().modifyFlat(ID, DAMAGE_INCREASE_PERCENT*0.01f);
        }
        ship.getMutableStats().getEnergyWeaponDamageMult().modifyFlat(ID, DAMAGE_INCREASE_PERCENT*0.01f);
    }

    private List<ShipAPI> getFighters(ShipAPI carrier) {
        List<ShipAPI> result = new ArrayList<ShipAPI>();

        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (!ship.isFighter()) continue;
            if (ship.getWing() == null) continue;
            if (ship.getWing().getSourceShip() == carrier) {
                result.add(ship);
            }
        }

        return result;
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return ((int) DAMAGE_INCREASE_PERCENT) + "%";
        }
        return null;
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("shpe_khorne_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) return false;
        if (ship.getVariant().hasHullMod("shpe_slaanesh_mark")) return false;
        return ship != null;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("khorne_mark")) {
            return "Incompatible with Mark of Khorne";
        }
        if (ship.getVariant().hasHullMod("nurgle_mark")) {
            return "Incompatible with Mark of Nurgle";
        }
        if (ship.getVariant().hasHullMod("slaanesh_mark")) {
            return "Incompatible with Mark of Slaanesh";
        }
        return "Incompatible";
    }
}
