package mok_slyk.shpe.scripts.skills;

import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;

public class ChampionOfSlaanesh {
    static final String ID = "shpe_slaanesh_champion";
    static final float MOVEMENT_BUFF = 10f;
    public static final float CLOSE_RANGE = 400;

    //runcode com.fs.starfarer.api.Global.getSector().getPlayerStats().setSkillLevel("shpe_slaanesh_champion", 1);

    public static class Level1 implements AfterShipCreationSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(MOVEMENT_BUFF) + "% speed and maneuverability while close to other ships";
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }

        @Override
        public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
            ship.addListener(new SlaaneshChampionListener((ship)));
        }

        @Override
        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {
            ship.removeListenerOfClass(SlaaneshChampionListener.class);
        }
    }

    public static class SlaaneshChampionListener implements AdvanceableListener {
        protected ShipAPI ship;
        public SlaaneshChampionListener(ShipAPI ship) {
            this.ship = ship;
        }
        @Override
        public void advance(float amount) {
            List<ShipAPI> allies = AIUtils.getNearbyAllies(ship, CLOSE_RANGE);
            if (!allies.isEmpty() || !AIUtils.getNearbyEnemies(ship, CLOSE_RANGE).isEmpty()) {
                ship.getMutableStats().getMaxSpeed().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                ship.getMutableStats().getAcceleration().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                ship.getMutableStats().getTurnAcceleration().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                ship.getMutableStats().getDeceleration().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                ship.getMutableStats().getMaxTurnRate().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);

                for (ShipAPI ally: allies) {
                    ally.getMutableStats().getMaxSpeed().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                    ally.getMutableStats().getAcceleration().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                    ally.getMutableStats().getTurnAcceleration().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                    ally.getMutableStats().getDeceleration().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                    ally.getMutableStats().getMaxTurnRate().modifyMult(ID, 1f + 0.01f*MOVEMENT_BUFF);
                }
            } else {
                ship.getMutableStats().getMaxSpeed().unmodify(ID);
                ship.getMutableStats().getAcceleration().unmodify(ID);
                ship.getMutableStats().getTurnAcceleration().unmodify(ID);
                ship.getMutableStats().getDeceleration().unmodify(ID);
                ship.getMutableStats().getMaxTurnRate().unmodify(ID);
            }
            List<ShipAPI> otherAllies = AIUtils.getAlliesOnMap(ship);
            otherAllies.removeAll(allies);
            for (ShipAPI ally: otherAllies) {
                ally.getMutableStats().getMaxSpeed().unmodify(ID);
                ally.getMutableStats().getAcceleration().unmodify(ID);
                ally.getMutableStats().getTurnAcceleration().unmodify(ID);
                ally.getMutableStats().getDeceleration().unmodify(ID);
                ally.getMutableStats().getMaxTurnRate().unmodify(ID);
            }
        }
    }

    public static class Level2 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(MOVEMENT_BUFF) + "% speed and maneuverability while close to the flagship";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_SHIPS;
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }
    }

}
