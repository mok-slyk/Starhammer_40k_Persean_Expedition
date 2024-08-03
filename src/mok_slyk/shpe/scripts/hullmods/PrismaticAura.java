package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class PrismaticAura extends BaseHullMod {
    float flamesElapsed = 0f;
    float timer = 0f;
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        flamesElapsed += amount;
        timer += amount*1;
        if (timer >= 2*MathUtils.FPI) timer -= 2*MathUtils.FPI;
        if (flamesElapsed > 0.03) {
            Vector2f point = MathUtils.getRandomPointInCircle(ship.getLocation(), 10f);
            Vector2f point2 = MathUtils.getRandomPointInCircle(ship.getLocation(), 10f);
            Vector2f vel = new Vector2f();
            CombatEngineAPI engine = Global.getCombatEngine();
            float size = 15f;
            //size = Misc.getHitGlowSize(size, projectile.getDamage().getBaseDamage(), damageResult);
            float sizeMult = (float)Math.random()+0.5f;
            float dur = 0.5f;
            float rampUp = 0f;
            Color c = new Color(Math.min(((float)FastTrig.sin(timer)+1)*0.5f,1),
                    Math.min(((float)FastTrig.sin(timer+0.67*MathUtils.FPI)+1)*0.5f,1),
                    Math.min(((float)FastTrig.sin(timer+1.33*MathUtils.FPI)+1)*0.5f,1),
                    0.2f);
            Color inv = new Color(100, 150, 250, 20);
            engine.addNebulaParticle(point, vel, size, 1f + sizeMult,
                    rampUp, 0f, dur, c);
            //engine.addNegativeNebulaParticle(point2, vel, size, 2f,
            //        rampUp, 0f, dur, inv);
        }
        super.advanceInCombat(ship, amount);
    }
}
