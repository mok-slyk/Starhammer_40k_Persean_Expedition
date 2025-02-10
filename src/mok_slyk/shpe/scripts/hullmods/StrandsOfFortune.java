package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class StrandsOfFortune extends BaseHullMod {
    static final String ID = "shpe_strands_of_fortune";
    public static final float ACCURACY_BUFF = 50;
    public static final float PROJECTIVE_SPEED_BUFF = 30;
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getProjectileSpeedMult().modifyPercent(ID, PROJECTIVE_SPEED_BUFF);
        stats.getAutofireAimAccuracy().modifyPercent(ID, ACCURACY_BUFF);
        stats.getMaxRecoilMult().modifyPercent(ID, -ACCURACY_BUFF);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("shpe_hellforged") && ship.getVariant().hasHullMod("shpe_tzeentch_mark");
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_tzeentch_mark")) {
            return "Requires Mark of Tzeentch";
        }
        if (!ship.getVariant().hasHullMod("shpe_hellforged")) {
            return "Requires Hellforged";
        }
        return "";
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) {
            return ((int) ACCURACY_BUFF) + "%";
        }
        if (index == 1) {
            return ((int) PROJECTIVE_SPEED_BUFF) + "%";
        }
        return null;
    }
}
