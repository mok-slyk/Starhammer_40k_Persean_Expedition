package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicIncompatibleHullmods;

import java.awt.*;

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

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new WeaponsArrayRangeModifier());
    }

    public static class WeaponsArrayRangeModifier implements WeaponBaseRangeModifier {
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
            if (weapon.getSpec().getSize() == WeaponAPI.WeaponSize.SMALL || weapon.getSpec().getSize() == WeaponAPI.WeaponSize.MEDIUM) {
                if (weapon.getSpec().getMaxRange()*1.5 >= 1200) {
                    return Math.max(0, 1200-weapon.getSpec().getMaxRange());
                }
                return weapon.getSpec().getMaxRange()*0.5f;
            }
            return 0;
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        //if (index == 0) return "" + (int)RANGE_PENALTY_PERCENT + "%";
        return null;
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color t = Misc.getTextColor();
        Color g = Misc.getGrayColor();

        tooltip.addPara("Increases the base range of small and medium weapons by 50%% up to a maximum of 1200.",
                opad, h, "50%", "1200");

    }
}
