package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.dark.shaders.util.ShaderLib;

import java.awt.*;

public class Hellforged extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getEntity() == null) {
            return;
        }
        ShipAPI ship = (ShipAPI) stats.getEntity();
        /*

        if (stats.getVariant().hasHullMod("shpe_tzeentch_mark")) {
            ship.setSprite(Global.getSettings().getSprite("graphics/ships/shpe_styx_tzeentch.png"));
        } else {
            ship.setSprite(Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()));
        }

         */

    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) {
            SpriteAPI sprite;
            try {
                sprite = Global.getSettings().getSprite("chaos_ships", "styx_tzeentch", false);
            } catch (RuntimeException ex) {
                sprite = null;
            }

            if (sprite != null) {
                float x = ship.getSpriteAPI().getCenterX();
                float y = ship.getSpriteAPI().getCenterY();
                float alpha = ship.getSpriteAPI().getAlphaMult();
                float angle = ship.getSpriteAPI().getAngle();
                Color color = ship.getSpriteAPI().getColor();

                ship.setSprite("chaos_ships", "styx_tzeentch");
                ShaderLib.overrideShipTexture(ship, "styx_tzeentch");

                ship.getSpriteAPI().setCenter(x, y);
                ship.getSpriteAPI().setAlphaMult(alpha);
                ship.getSpriteAPI().setAngle(angle);
                ship.getSpriteAPI().setColor(color);
            }
        }
    }
}
