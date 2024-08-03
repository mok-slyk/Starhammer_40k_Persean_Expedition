package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class DrakeWingEffect implements EveryFrameWeaponEffectPlugin {
    float elapsed = 0f;
    float flamesElapsed = 0f;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        AnimationAPI anim = weapon.getAnimation();
        ShipAPI ship = weapon.getShip();
        elapsed += amount;
        flamesElapsed += elapsed;
        float invRate = Math.max(0.25f/(ship.getVelocity().length()/ship.getMaxSpeed()), 2);
        if (elapsed >= invRate) {
            anim.setFrame(anim.getFrame() == 1 ? 0 : 1);
            elapsed = 0;
        }
        /*
        if (flamesElapsed > 0.1) {
            Vector2f point = MathUtils.getRandomPointInCircle(weapon.getLocation(), 20f);
            Vector2f point2 = MathUtils.getRandomPointInCircle(weapon.getLocation(), 20f);
            Vector2f vel = new Vector2f();
            float size = 20f;
            //size = Misc.getHitGlowSize(size, projectile.getDamage().getBaseDamage(), damageResult);
            float sizeMult = (float)Math.random()+0.5f;
            float dur = 0.5f;
            float rampUp = 0f;
            Color c = new Color(255, 150, 90, 50);
            Color inv = new Color(100, 150, 250, 20);
            engine.addNebulaParticle(point, vel, size, 1f + sizeMult,
                    rampUp, 0f, dur, c);
            engine.addNegativeNebulaParticle(point, vel, size, 2f,
										rampUp, 0f, dur, inv);
        }
         */
    }
}
