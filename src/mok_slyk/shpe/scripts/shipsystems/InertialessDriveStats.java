package mok_slyk.shpe.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class InertialessDriveStats extends BaseShipSystemScript {

    public static float SPEED_BONUS = 350f;
    public static float TURN_BONUS = 200f;
    private final IntervalUtil afterImageInterval = new IntervalUtil(0.20f,0.20f);

    public static Color IMAGE_COLOR = new Color(90,255,135,155);

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        //super.apply(stats, id, state, effectLevel);

        //Stats:
        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
            stats.getMaxTurnRate().unmodify(id);
        } else {
            stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
            stats.getAcceleration().modifyPercent(id, SPEED_BONUS * 3f * effectLevel);
            stats.getDeceleration().modifyPercent(id, SPEED_BONUS * 3f * effectLevel);
            stats.getTurnAcceleration().modifyFlat(id, TURN_BONUS * effectLevel);
            stats.getTurnAcceleration().modifyPercent(id, TURN_BONUS * 5f * effectLevel);
            stats.getMaxTurnRate().modifyFlat(id, 100f);
            stats.getMaxTurnRate().modifyPercent(id, 150f);
        }

        //Afterimages:
        ShipAPI ship = (ShipAPI)stats.getEntity();
        afterImageInterval.advance(Global.getCombatEngine().getElapsedInLastFrame());
        if (afterImageInterval.intervalElapsed() && (state != State.OUT) && MagicRender.screenCheck(0.5f, ship.getLocation())) {
            //ship.addAfterimage(IMAGE_COLOR,0f, 0f, -ship.getVelocity().x, -ship.getVelocity().y, 0f, 0.5f, 6f, 0.5f, true, false, false);
            //ShipAPI afterimage = Global.getCombatEngine().createFXDrone(ship.getVariant());
            SpriteAPI shipSprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
            MagicRender.battlespace(
                    shipSprite,
                    new Vector2f(ship.getLocation()),
                    new Vector2f(),
                    new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                    new Vector2f(),
                    ship.getFacing() - 90,
                    0,
                    IMAGE_COLOR,
                    true,
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    0.2f,
                    0.1f,
                    0.6f,
                    CombatEngineLayers.BELOW_SHIPS_LAYER
            );
        }
    }



    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("improved maneuverability", false);
        } else if (index == 1) {
            return new StatusData("+" + (int)SPEED_BONUS + " top speed", false);
        }
        return null;
    }
}
