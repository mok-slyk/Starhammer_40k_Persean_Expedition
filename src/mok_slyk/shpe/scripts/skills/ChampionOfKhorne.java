package mok_slyk.shpe.scripts.skills;

import com.fs.starfarer.api.characters.AfterShipCreationSkillEffect;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.Objects;

public class ChampionOfKhorne {
    static final String ID = "shpe_khorne_champion";
    public static final int FLUX_DISSIPATION_BUFF = 20;
    public static final int BUFF_DURATION = 5;

    //runcode com.fs.starfarer.api.Global.getSector().getPlayerStats().setSkillLevel("shpe_khorne_champion", 1);
    public static class Level1 implements AfterShipCreationSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }

        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(FLUX_DISSIPATION_BUFF) + "% flux dissipation for " + (int)(BUFF_DURATION) + " seconds after damaging an enemy's hull or armor.";
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
            if (Objects.equals(ship.getCaptain().getPersonalityAPI().getId(), "reckless") ) {
                ship.addListener(new KhorneChampionListener((ship)));
            }
        }

        @Override
        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {
            ship.removeListenerOfClass(KhorneChampionListener.class);
        }
    }

    public static class Level2 implements ShipSkillEffect {
        @Override
        public String getEffectDescription(float level) {
            return "+" + (int)(FLUX_DISSIPATION_BUFF) + "% flux dissipation for " + (int)(BUFF_DURATION) + " seconds after damaging an enemy's hull or armor.";
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

    public static class KhorneChampionListener implements AdvanceableListener, DamageDealtModifier {
        float buffElapsed = 0f;
        protected ShipAPI ship;
        public KhorneChampionListener(ShipAPI ship) {
            this.ship = ship;
        }
        @Override
        public void advance(float amount) {
            if (buffElapsed > BUFF_DURATION) {
                ship.getMutableStats().getFluxDissipation().unmodify(ID);
            } else {
                buffElapsed += amount;
            }
        }

        @Override
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if (damage.getDamage() > 0.5 && !shieldHit && target.getOwner() != ship.getOwner()) {
                buffElapsed = 0;
                ship.getMutableStats().getFluxDissipation().modifyMult(ID, 1f + 0.01f*FLUX_DISSIPATION_BUFF);
            }

            //Global.getCombatEngine().addFloatingText(ship.getLocation(), String.valueOf(ratio), 100, new Color(1f, 1f ,1f),ship, 0,0 );
            return null;
        }
    }
}
