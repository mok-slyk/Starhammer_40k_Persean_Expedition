package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import mok_slyk.shpe.scripts.skills.ChampionOfSlaanesh;
import org.lazywizard.lazylib.combat.AIUtils;

import java.util.List;

public class SirensSummon extends BaseHullMod {
    static final String ID = "shpe_sirens_summon";
    static final float MANEUVER_DEBUFF = 30;
    static final float ACCURACY_DEBUFF = 30;
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        List<ShipAPI> enemies = AIUtils.getNearbyAllies(ship, ChampionOfSlaanesh.CLOSE_RANGE);
        for (ShipAPI enemy: enemies) {
            enemy.getMutableStats().getMaxTurnRate().modifyMult(ID, 1-0.01f*MANEUVER_DEBUFF);
            enemy.getMutableStats().getTurnAcceleration().modifyMult(ID, 1-0.01f*MANEUVER_DEBUFF);
            enemy.getMutableStats().getMaxRecoilMult().modifyMult(ID, 1+0.01f*ACCURACY_DEBUFF);
            enemy.getMutableStats().getRecoilPerShotMult().modifyMult(ID, 1+0.01f*ACCURACY_DEBUFF);
            enemy.getMutableStats().getAutofireAimAccuracy().modifyMult(ID, 1-0.01f*ACCURACY_DEBUFF);
        }
        List<ShipAPI> otherEnemies = AIUtils.getEnemiesOnMap(ship);
        otherEnemies.removeAll(enemies);
        for (ShipAPI enemy: otherEnemies) {

        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("shpe_hellforged") && ship.getVariant().hasHullMod("shpe_slaanesh_mark");
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_slaanesh_mark")) {
            return "Requires Mark of Slaanesh";
        }
        if (!ship.getVariant().hasHullMod("shpe_hellforged")) {
            return "Requires Hellforged";
        }
        return "";
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) {
            return ((int) MANEUVER_DEBUFF) + "%";
        }
        if (index == 1) {
            return ((int) ACCURACY_DEBUFF) + "%";
        }
        if (index == 2) {
            return ((int) ACCURACY_DEBUFF) + "%";
        }
        return null;
    }
}
