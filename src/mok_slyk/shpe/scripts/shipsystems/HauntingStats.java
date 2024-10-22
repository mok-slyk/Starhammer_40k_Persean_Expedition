package mok_slyk.shpe.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.RiftUtils.*;

public class HauntingStats extends BaseShipSystemScript {
    final String ID = "shpe_haunting";
    private float lastTimeElapsed = 0;
    private boolean didWarpEffect = false;
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
        } else {
            return;
        }

        float amount = Global.getCombatEngine().getTotalElapsedTime(false) - lastTimeElapsed;
        lastTimeElapsed = Global.getCombatEngine().getTotalElapsedTime(false);


        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, id);
            didWarpEffect = false;
            return;
        }

        if (state == State.IN || state == State.ACTIVE) {
            ship.setPhased(true);
        }
        ship.setExtraAlphaMult(1-0.7f*effectLevel);
        if (player) {
            Global.getCombatEngine().getTimeMult().modifyMult(ID, 1-0.5f*effectLevel);
        }

        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
        } else {
            stats.getMaxSpeed().modifyFlat(id, 400f * effectLevel);
            stats.getAcceleration().modifyFlat(id, 600f * effectLevel);
            stats.getDeceleration().modifyFlat(id, 600f * effectLevel);
            stats.getMaxTurnRate().modifyMult(id, 5);
            stats.getTurnAcceleration().modifyMult(id, 5);
            //stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
        }

        float jitterFactor = 1-2*Math.abs(effectLevel-0.5f);
        if (state == State.IN || state == State.OUT) {
            ship.setJitter(ID, new Color(255, 40, 255, 200), jitterFactor, 3, ship.getCollisionRadius() * jitterFactor * 0.2f);
        }

        if (state == State.IN && !didWarpEffect && jitterFactor > 0.9f) {
            doWarpEffect(Global.getCombatEngine(), ship, 500);
            didWarpEffect = true;
        }

        if(state == State.ACTIVE) {
            didWarpEffect = false;
            ship.setJitter(ID, new Color(142, 0, 255, 200), 0.5f, 2, ship.getCollisionRadius()*0.1f);
        }



        if (state == State.OUT && !didWarpEffect && jitterFactor > 0.9f) {
            doWarpEffect(Global.getCombatEngine(), ship, 1000);
            didWarpEffect = true;
        }
    }

    private void doWarpEffect(CombatEngineAPI engine, ShipAPI ship, float velocity) {
        Vector2f loc = Vector2f.add(ship.getLocation(), VectorUtils.rotate(new Vector2f(0, 200), ship.getFacing()-90), null);
        createWarpRift(engine, loc, 50f, new Color(255, 0, 255), new Color(13, 16, 79), 2, 25);
        Vector2f.add(ship.getVelocity(), VectorUtils.rotate(new Vector2f(0, velocity), ship.getFacing()-90), ship.getVelocity());

    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
        } else {
            return;
        }

        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);

        Global.getCombatEngine().getTimeMult().unmodify(ID);

        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }
}
