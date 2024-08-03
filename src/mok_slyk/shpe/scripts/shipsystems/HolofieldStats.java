package mok_slyk.shpe.scripts.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class HolofieldStats extends BaseShipSystemScript {
    public static float PLAYER_ALPHA_MULT = 0.75f;
    public static Color HOLO_COLOR = new Color(155, 135, 255, 200);
    public static float HOLO_RANGE = 30f;
    public static float HOLO_DRIFT = 10f;
    public static float INVULN_PERCENTAGE = 0.6f;

    private Vector2f[] imageOffsets = null;
    private IntervalUtil invulnInterval = new IntervalUtil(0.3f, 1f);
    private float lastTimeElapsed = 0;
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

        doVisualEffect(ship, state, effectLevel, amount);

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, id);
            return;
        }

        if (state == State.IN || state == State.ACTIVE) {
            //ship.setPhased(true);
            invulnInterval.advance(amount);
            if (invulnInterval.intervalElapsed()) {
                if (Math.random() < INVULN_PERCENTAGE*effectLevel) {
                    ship.setCollisionClass(CollisionClass.NONE);
                } else {
                    if (ship.isFighter() || ship.isDrone()) {
                        ship.setCollisionClass(CollisionClass.FIGHTER);
                    } else {
                        ship.setCollisionClass(CollisionClass.SHIP);
                    }
                }
            }
        }


    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        //boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            //player = ship == Global.getCombatEngine().getPlayerShip();
            //id = id + "_" + ship.getId();
        } else {
            return;
        }
        //ship.setPhased(false);
        if (ship.isFighter() || ship.isDrone()) {
            ship.setCollisionClass(CollisionClass.FIGHTER);
        } else {
            ship.setCollisionClass(CollisionClass.SHIP);
        }
        undoVisualEffect(ship);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    protected void doVisualEffect(ShipAPI ship, State state, float effectLevel, float amount) {
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            ship.setExtraAlphaMult(1f - (1f - PLAYER_ALPHA_MULT) * effectLevel);
        } else {
            ship.setExtraAlphaMult(1f - effectLevel);
        }
        ship.setApplyExtraAlphaToEngines(true);

        float spriteOffset = 0f;
        if(ship.getHullSpec().getBaseHullId().equals("shpe_eclipse")) {
            spriteOffset = -40f;
        }
        Vector2f offsetVector = new Vector2f(0f, 1f);
        offsetVector = VectorUtils.rotate(offsetVector, ship.getFacing()-90f);
        offsetVector = VectorUtils.resize(offsetVector, spriteOffset);

        //ship.setJitter(ship, HOLO_COLOR, 1f, 3, 0f, 5f );
        SpriteAPI shipSprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
        if (imageOffsets == null) {
            imageOffsets = new Vector2f[]{new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};
            for (Vector2f pos: imageOffsets) {
                pos = MathUtils.getRandomPointInCircle(pos, HOLO_RANGE);
            }
        }

        for (Vector2f pos: imageOffsets) {
            pos.x = effectLevel*MathUtils.clamp(pos.x + MathUtils.getRandomNumberInRange(-HOLO_DRIFT, HOLO_DRIFT),-HOLO_RANGE, HOLO_RANGE);
            pos.y = effectLevel*MathUtils.clamp(pos.y + MathUtils.getRandomNumberInRange(-HOLO_DRIFT, HOLO_DRIFT),-HOLO_RANGE, HOLO_RANGE);
            float xLocation = ship.getLocation().x+pos.x+offsetVector.x;
            float yLocation = ship.getLocation().y+pos.y+offsetVector.y;
            MagicRender.singleframe(shipSprite, new Vector2f(xLocation,yLocation), new Vector2f(shipSprite.getWidth(), shipSprite.getHeight()), ship.getFacing()-90f, HOLO_COLOR, true);
        }
    }

    protected void undoVisualEffect(ShipAPI ship) {
        ship.setExtraAlphaMult(1f);
        imageOffsets = null;
    }
}
