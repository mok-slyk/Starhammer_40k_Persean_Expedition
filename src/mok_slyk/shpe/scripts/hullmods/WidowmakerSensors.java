package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.magiclib.util.MagicIncompatibleHullmods;

public class WidowmakerSensors extends BaseHullMod {
    public static final float SIGHT_RANGE_BOOST = 2000f;
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getSightRadiusMod().modifyFlat(id, SIGHT_RANGE_BOOST);
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        return (int) SIGHT_RANGE_BOOST +"";
    }
}
