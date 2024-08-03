package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;

public class DrakeWings extends BaseHullMod {
    IntervalUtil intervalUtil = new IntervalUtil(0.33f, 0.35f);
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        intervalUtil.advance(amount);
        SpriteAPI sprite1 = Global.getSettings().getSprite("graphics/ships/shpe_heldrake00.png");
        SpriteAPI sprite2 = Global.getSettings().getSprite("graphics/ships/shpe_heldrake01.png");
        if (intervalUtil.intervalElapsed()) {
            if (ship.getSpriteAPI() == sprite1) {
                ship.setSprite(sprite2);
            } else {
                ship.setSprite(sprite1);
            }
        }

    }
}
