package mok_slyk.shpe.scripts.skills;

import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;

public class ChosenOfNurgle {

    static final String ID = "shpe_nurgle_chosen";
    public static final float REGENERATION_AT_0 = 2;
    public static class Level1 implements AfterShipCreationSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }

        @Override
        public String getEffectDescription(float level) {
            return "Slowly regains hull, the more damaged the hull, the faster the regeneration.";
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
            ship.addListener(new ChosenOfNurgle.NurgleChosenListener((ship)));
        }

        @Override
        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {
            ship.removeListenerOfClass(ChosenOfNurgle.NurgleChosenListener.class);
        }
    }

    public static class NurgleChosenListener implements AdvanceableListener {

        protected ShipAPI ship;
        public NurgleChosenListener(ShipAPI ship) {
            this.ship = ship;
        }
        @Override
        public void advance(float amount) {
            ship.setHitpoints((ship.getHullLevel() + (amount * (1-ship.getHullLevel()) * REGENERATION_AT_0 * 0.01f)));
        }
    }
}

