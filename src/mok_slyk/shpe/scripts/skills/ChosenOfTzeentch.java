package mok_slyk.shpe.scripts.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class ChosenOfTzeentch {
    public static final String ID = "shpe_tzeentch_chosen";
    static final float FIGHTER_RANGE_BUFF = 20f;
    static final float WEAPON_RANGE_BUFF = 10f;

    //runcode com.fs.starfarer.api.Global.getSector().getPlayerStats().setSkillLevel("shpe_tzeentch_chosen", 1);

    public static class Level1 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(FIGHTER_RANGE_BUFF) + "% fighter engagement range";
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
            stats.getFighterWingRange().modifyMult(ID, 1 + FIGHTER_RANGE_BUFF * 0.01f);
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getFighterWingRange().unmodify(ID);
        }
    }

    public static class Level2 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(WEAPON_RANGE_BUFF) + "% weapon range";
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
            stats.getEnergyWeaponRangeBonus().modifyMult(ID, 1 + WEAPON_RANGE_BUFF * 0.01f);
            stats.getBallisticWeaponRangeBonus().modifyMult(ID, 1 + WEAPON_RANGE_BUFF * 0.01f);
            stats.getMissileWeaponRangeBonus().modifyMult(ID, 1 + WEAPON_RANGE_BUFF * 0.01f);
        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getEnergyWeaponRangeBonus().unmodify(ID);
            stats.getBallisticWeaponRangeBonus().unmodify(ID);
            stats.getMissileWeaponRangeBonus().unmodify(ID);
        }
    }

    public static class Level3 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(FIGHTER_RANGE_BUFF) + "% fighter engagement range";
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
            return "+" + (int)(WEAPON_RANGE_BUFF) + "% weapon range";
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
