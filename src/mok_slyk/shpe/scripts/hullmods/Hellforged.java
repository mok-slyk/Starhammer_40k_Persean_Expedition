package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.dark.shaders.util.ShaderLib;

import java.awt.*;

public class Hellforged extends BaseHullMod {
    public static float MARK_EFFECT_MULT = 1.5f;

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
        /*
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
         */



        float x = ship.getSpriteAPI().getCenterX();
        float y = ship.getSpriteAPI().getCenterY();
        float alpha = ship.getSpriteAPI().getAlphaMult();
        float angle = ship.getSpriteAPI().getAngle();
        Color color = ship.getSpriteAPI().getColor();

        String override = null;

        if (motherShipHasChaosMark(ship)) {
            ShipAPI source = ship.getWing().getSourceShip();
            if (source.getVariant().hasHullMod("shpe_khorne_mark")) {

            }
            if (source.getVariant().hasHullMod("shpe_tzeentch_mark")) {

            }
            if (source.getVariant().hasHullMod("shpe_nurgle_mark")) {

            }
            if (source.getVariant().hasHullMod("shpe_slaanesh_mark")) {

            }
        }

        if (hasChaosMark(ship)) {
            if (ship.getVariant().hasHullMod("shpe_tzeentch_mark")) {
                if (ship.getHullSpec().getBaseHullId().equals("shpe_styx")) override = "styx_tzeentch";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_acheron")) override = "acheron_tzeentch";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_devastation")) override = "devastation_tzeentch";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_slaughter")) override = "slaughter_tzeentch";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_idolator")) override = "idolator_tzeentch";
            }
            if (ship.getVariant().hasHullMod("shpe_khorne_mark")) {
                if (ship.getHullSpec().getBaseHullId().equals("shpe_styx")) override = "styx_khorne";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_acheron")) override = "acheron_khorne";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_devastation")) override = "devastation_khorne";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_slaughter")) override = "slaughter_khorne";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_idolator")) override = "idolator_khorne";
            }
            if (ship.getVariant().hasHullMod("shpe_slaanesh_mark")) {
                if (ship.getHullSpec().getBaseHullId().equals("shpe_styx")) override = "styx_slaanesh";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_acheron")) override = "acheron_slaanesh";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_devastation")) override = "devastation_slaanesh";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_slaughter")) override = "slaughter_slaanesh";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_idolator")) override = "idolator_slaanesh";
            }
            if (ship.getVariant().hasHullMod("shpe_nurgle_mark")) {
                if (ship.getHullSpec().getBaseHullId().equals("shpe_styx")) override = "styx_nurgle";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_acheron")) override = "acheron_nurgle";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_devastation")) override = "devastation_nurgle";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_slaughter")) override = "slaughter_nurgle";
                if (ship.getHullSpec().getBaseHullId().equals("shpe_idolator")) override = "idolator_nurgle";
            }
        }
        if (override != null) {
            ship.setSprite("chaos_ships", override);
            ShaderLib.overrideShipTexture(ship, override);
        }
        ship.getSpriteAPI().setCenter(x, y);
        ship.getSpriteAPI().setAlphaMult(alpha);
        ship.getSpriteAPI().setAngle(angle);
        ship.getSpriteAPI().setColor(color);


    }

    private boolean hasChaosMark(ShipAPI ship) {
        return ship.getVariant().hasHullMod("shpe_tzeentch_mark") || ship.getVariant().hasHullMod("shpe_khorne_mark")
                || ship.getVariant().hasHullMod("shpe_slaanesh_mark") || ship.getVariant().hasHullMod("shpe_nurgle_mark");
    }

    private boolean motherShipHasChaosMark(ShipAPI ship) {
        if (!ship.isFighter()) return false;
        ShipAPI source = ship.getWing().getSourceShip();
        return source.getVariant().hasHullMod("shpe_tzeentch_mark") || source.getVariant().hasHullMod("shpe_khorne_mark")
                || source.getVariant().hasHullMod("shpe_slaanesh_mark") || source.getVariant().hasHullMod("shpe_nurgle_mark");

    }
}
