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
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ChosenOfKhorne {
    static final String ID = "shpe_khorne_chosen";
    public static class Level1 implements AfterShipCreationSkillEffect {
        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }

        @Override
        public String getEffectDescription(float level) {
            return "Piloted ship deals increased damage based on the amount of damage it has already dealt this combat.";
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
            ship.addListener(new KhorneChosenListener((ship)));
        }

        @Override
        public void unapplyEffectsAfterShipCreation(ShipAPI ship, String id) {
            ship.removeListenerOfClass(KhorneChosenListener.class);
        }
    }

    public static class KhorneChosenListener implements AdvanceableListener, DamageDealtModifier {
        float maxDamage = 10000;
        float totalDamage = 0;
        protected ShipAPI ship;
        public KhorneChosenListener(ShipAPI ship) {
            this.ship = ship;
        }
        @Override
        public void advance(float amount) {

        }

        @Override
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            totalDamage += damage.getDamage();
            float ratio = MathUtils.clamp(totalDamage/maxDamage, 0, 1);
            ship.getMutableStats().getEnergyWeaponDamageMult().modifyFlat(ID, ratio);
            ship.getMutableStats().getBallisticWeaponDamageMult().modifyFlat(ID, ratio);
            ship.getMutableStats().getMissileWeaponDamageMult().modifyFlat(ID, ratio*0.5f);

            //Global.getCombatEngine().addFloatingText(ship.getLocation(), String.valueOf(ratio), 100, new Color(1f, 1f ,1f),ship, 0,0 );
            return null;
        }
    }
}
