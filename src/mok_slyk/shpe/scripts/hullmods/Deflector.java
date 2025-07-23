package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class Deflector extends BaseHullMod {

    private final String INNERLARGE = "graphics/fx/shpe_shield_tau.png";
    private final String OUTERLARGE = "graphics/fx/shields256ringd.png";

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getShield() != null) {
            ship.getShield().setRadius(ship.getShieldRadiusEvenIfNoShield(), INNERLARGE, OUTERLARGE);
        }
    }
}
