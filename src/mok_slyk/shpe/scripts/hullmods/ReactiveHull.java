package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class ReactiveHull extends BaseHullMod {
    public static float HEALTH_REGEN = 4f;
    public static float WEAPON_HEALTH_BONUS = 100f;

    public static float REPAIR_BONUS = 75f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getWeaponHealthBonus().modifyPercent(id, WEAPON_HEALTH_BONUS);
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - REPAIR_BONUS * 0.01f);
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - REPAIR_BONUS * 0.01f);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);
        int xCells = ship.getArmorGrid().getLeftOf()+ship.getArmorGrid().getRightOf();
        int yCells = ship.getArmorGrid().getAbove()+ship.getArmorGrid().getBelow();
        ArmorGridAPI grid = ship.getArmorGrid();
        for (int x = 0; x < xCells; x++) {
            for (int y = 0; y < yCells; y++) {
                grid.setArmorValue(x, y, Math.min(grid.getMaxArmorInCell(), grid.getArmorValue(x, y)+(grid.getMaxArmorInCell() * amount * 0.01f * HEALTH_REGEN)));
            }
        }
    }
}
