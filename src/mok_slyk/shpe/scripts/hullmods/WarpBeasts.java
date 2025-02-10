package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.subsystems.MagicSubsystemsManager;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.getRandomPointInBounds;

public class WarpBeasts extends BaseHullMod {
    public final int RANGE = 600;
    public final int DAMAGE = 30;
    public final int EMP = 30;
    IntervalUtil timer = new IntervalUtil(1f, 1.5f);
    int elapsed = 0;

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        timer.advance(amount);
        if (!ship.isAlive()||ship.isHulk()) return;

        //spawn arcs
        if (timer.intervalElapsed()) {
            CombatEngineAPI engine = Global.getCombatEngine();
            CombatEntityAPI enemy = AIUtils.getNearestEnemy(ship);
            if (enemy != null) {
                if (MathUtils.getDistance(enemy, ship) <= RANGE) {
                    float thickness = 20f;
                    float coreWidthMult = 0.67f;
                    Color color = new Color(255, 100, 255, 190);
                    Vector2f spawnPoint = getRandomPointInBounds(ship);
                    //spawnPoint = VectorUtils.rotate(spawnPoint, ship.getFacing()-90);
                    //spawnPoint = Vector2f.add(spawnPoint, ship.getLocation(), null);

                    EmpArcEntityAPI arc = engine.spawnEmpArc(ship, spawnPoint, ship,
                            enemy,
                            DamageType.ENERGY,
                            DAMAGE,
                            EMP, // emp
                            RANGE * 2, // max range
                            "shock_repeater_emp_impact",
                            thickness, // thickness
                            color,
                            new Color(250, 255, 250, 255)
                    );
                    arc.setCoreWidthOverride(thickness * coreWidthMult);
                    arc.setSingleFlickerMode();
                }
            }
        }
        elapsed += amount*200;
        //spawn glow
        while (elapsed >= 0.1) {
            Vector2f vel = new Vector2f();
            CombatEngineAPI engine = Global.getCombatEngine();
            Vector2f point = getRandomPointInBounds(ship);
            Vector2f point2 = getRandomPointInBounds(ship);
            float size = 20f;
            //size = Misc.getHitGlowSize(size, projectile.getDamage().getBaseDamage(), damageResult);
            float sizeMult = (float)Math.random()+0.5f;
            float dur = 0.5f;
            float rampUp = 0f;
            Color c = new Color(255, 100, 255, 50);
            Color inv = new Color(150, 100, 250, 20);
            engine.addNebulaParticle(point, vel, size, 1f + sizeMult,
                    rampUp, 0f, dur, c);
            engine.addNegativeNebulaParticle(point2, vel, size, 2f,
                    rampUp, 0f, dur, inv);
            elapsed -= 0.1;
            if (elapsed<0) elapsed = 0;
        }
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        int num = 1;
        switch (ship.getHullSize()) {
            case FRIGATE:
                num = 1;
                break;
            case DESTROYER:
                num = 2;
                break;
            case CRUISER:
                num = 3;
                break;
            case CAPITAL_SHIP:
                num = 5;
                break;
        }
        MagicSubsystemsManager.addSubsystemToShip(ship, new ScreamerSubsystem(ship, num));

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
}
