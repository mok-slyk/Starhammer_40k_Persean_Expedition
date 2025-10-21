package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import org.magiclib.util.MagicIncompatibleHullmods;

public class WeaponsArrayTargeting extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getVariant().getHullMods().contains(HullMods.BALLISTIC_RANGEFINDER)) {
            //if someone tries to install BR, remove it
            MagicIncompatibleHullmods.removeHullmodWithWarning(
                    stats.getVariant(),
                    HullMods.BALLISTIC_RANGEFINDER,
                    "shpe_weapons_array"
            );
        }
    }

    public static class RangefinderRangeModifier implements WeaponBaseRangeModifier {
        @Override
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }

        @Override
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1;
        }

        @Override
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getSpec().getSize() == WeaponAPI.WeaponSize.SMALL) {
                return 300f;
            }
            return 0;
        }
    }
}
