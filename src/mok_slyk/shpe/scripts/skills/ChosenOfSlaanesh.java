package mok_slyk.shpe.scripts.skills;

import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;

public class ChosenOfSlaanesh {
    static final String ID = "shpe_slaanesh_chosen";
    static final float FIRE_RATE_BUFF = 5f;
    static final float SPEED_BUFF = 5f;

    //runcode com.fs.starfarer.api.Global.getSector().getPlayerStats().setSkillLevel("shpe_tzeentch_chosen", 1);

    public static class Level1 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(FIRE_RATE_BUFF) + "% fire rate";
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            stats.getBallisticRoFMult().modifyMult(ID, 1 + FIRE_RATE_BUFF * 0.01f);
            stats.getEnergyRoFMult().modifyMult(ID, 1 + FIRE_RATE_BUFF * 0.01f);
            stats.getMissileRoFMult().modifyMult(ID, 1 + FIRE_RATE_BUFF * 0.01f);
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getBallisticRoFMult().unmodify(ID);
            stats.getEnergyRoFMult().unmodify(ID);
            stats.getMissileRoFMult().unmodify(ID);
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_SHIPS;
        }
    }

    public static class Level2 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(SPEED_BUFF) + "% top speed and acceleration";
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            stats.getMaxSpeed().modifyMult(ID, 1 + SPEED_BUFF * 0.01f);
            stats.getAcceleration().modifyMult(ID, 1 + SPEED_BUFF * 0.01f);
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getMaxSpeed().unmodify(ID);
            stats.getAcceleration().unmodify(ID);
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_COMBAT_SHIPS;
        }
    }
    public static class Level3 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(FIRE_RATE_BUFF) + "% fire rate";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_COMBAT_SHIPS;
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }
    }
    public static class Level4 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(SPEED_BUFF) + "% top speed and acceleration";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return ScopeDescription.ALL_COMBAT_SHIPS;
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }
    }
}
