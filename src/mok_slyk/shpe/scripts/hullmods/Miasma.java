package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.getRandomPointInBounds;

public class Miasma extends BaseHullMod {
    public static float PROFILE_MULT = 0.7f;
    public static final float DMG_REDUCTION_PERCENT = 5;

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float elapsed = amount*50;
        //spawn fog
        while (elapsed >= 0.1) {
            Vector2f vel = new Vector2f();
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f point = getRandomPointInBounds(ship);
            Vector2f point2 = getRandomPointInBounds(ship);
            float size = 60f;
            //size = Misc.getHitGlowSize(size, projectile.getDamage().getBaseDamage(), damageResult);
            float sizeMult = (float)Math.random()+0.5f;
            float durMult = (float)Math.random()+0.5f;
            float dur = 0.5f;
            float rampUp = 0f;
            Color c = new Color(80, 100, 80, 7);
            Color inv = new Color(150, 100, 250, 20);
            //engine.addNebulaParticle(point, vel, size, 5f*sizeMult, rampUp, 0f, dur, c);
            //engine.addSmokeParticle(point, vel, size*sizeMult, 0.1f, 7*sizeMult, c);
            engine.addNebulaSmokeParticle(point, vel, size*sizeMult, 2, 0.3f, 0, 2*durMult, c);
            //engine.addNegativeNebulaParticle(point2, vel, size, 2f,rampUp, 0f, dur, inv);
            elapsed -= 0.1;
            if (elapsed<0) elapsed = 0;
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getSensorProfile().modifyMult(id, PROFILE_MULT);
        stats.getHullDamageTakenMult().modifyMult(id, 1-0.01f*DMG_REDUCTION_PERCENT);
        stats.getArmorDamageTakenMult().modifyMult(id, 1-0.01f*DMG_REDUCTION_PERCENT);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().hasHullMod("shpe_hellforged") && ship.getVariant().hasHullMod("shpe_nurgle_mark");
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (!ship.getVariant().hasHullMod("shpe_nurgle_mark")) {
            return "Requires Mark of Nurgle";
        }
        if (!ship.getVariant().hasHullMod("shpe_hellforged")) {
            return "Requires Hellforged";
        }
        return "";
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize, ShipAPI ship) {
        if (index == 0) {
            return (int)((1-PROFILE_MULT)*100) + "%";
        }
        if (index == 1) {
            return ((int) DMG_REDUCTION_PERCENT) + "%";
        }
        return null;
    }
}
