package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;

public class ReinforcedProw extends BaseHullMod {
    public static final float ARMOR_MULT = 2f;
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new ReinforcedProwListener(ship));
    }

    public static class ReinforcedProwListener implements AdvanceableListener {
        private boolean doOnce = true;
        ShipAPI ship;
        ReinforcedProwListener(ShipAPI ship) {
            this.ship = ship;
        }
        @Override
        public void advance(float amount) {
            if (doOnce) {
                ArmorGridAPI grid = ship.getArmorGrid();
                int x = (grid.getLeftOf()+ grid.getRightOf())/2;
                int y = (grid.getBelow()+ grid.getAbove())-5;
                grid.setArmorValue(x, y, grid.getArmorValue(x, y)*ARMOR_MULT);
                grid.setArmorValue(x-1, y, grid.getArmorValue(x-1, y)*ARMOR_MULT);
                grid.setArmorValue(x, y-1, grid.getArmorValue(x, y-1)*ARMOR_MULT);
                grid.setArmorValue(x-1, y-1, grid.getArmorValue(x-1, y-1)*ARMOR_MULT);
                grid.setArmorValue(x, y+1, grid.getArmorValue(x, y+1)*ARMOR_MULT);
                grid.setArmorValue(x-1, y+1, grid.getArmorValue(x-1, y+1)*ARMOR_MULT);

                //grid.setArmorValue(0, 4, grid.getArmorValue(1, 5)*500);
                doOnce = false;
            }
        }
    }
}
