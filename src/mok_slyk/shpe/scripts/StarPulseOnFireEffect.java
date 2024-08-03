package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.sun.prism.Graphics;
import mok_slyk.shpe.SHPEModPlugin;
import org.dark.shaders.ShaderModPlugin;
import org.dark.shaders.distortion.DistortionAPI;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.util.ShaderLib;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.LazyLib;
import org.lazywizard.lazylib.StringUtils;
import org.lazywizard.lazylib.ui.LazyFont;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.Iterator;

public class StarPulseOnFireEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    private boolean isFiring;
    private float waveRadius;
    public static float WAVE_GROWTH = 500f;

    public static float DAMAGE_RANGE = 0.75f;
    public static float RANGE_CONVERSION = 0.44f;

    public static float TICK_RATE = 10;
    public static Color COLOR = new Color(95, 255, 90, 155);
    private static Color currentColor;

    private Vector2f location;

    private final IntervalUtil damageTick = new IntervalUtil(1/TICK_RATE, 1/TICK_RATE);
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        location = projectile.getLocation();
        Global.getCombatEngine().removeEntity(projectile);
        isFiring = true;
        waveRadius = 0f;
        currentColor = COLOR;
        if (SHPEModPlugin.hasGraphicsLib) {
            float time = weapon.getRange()/250f;
            float rippleRange = weapon.getRange()*0.9f;
            RippleDistortion ripple = new RippleDistortion(location, new Vector2f());
            DistortionShader.addDistortion(ripple);
            ripple.setSize(rippleRange);
            ripple.setIntensity(rippleRange*0.1f);
            ripple.setFrameRate(60f/time);
            ripple.fadeInSize(time*1.2f);
            ripple.fadeOutIntensity(time);
            ripple.setSize(rippleRange*0.01f);
        }
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        float range = weapon.getRange();
        float damage = weapon.getDamage().getDamage()/TICK_RATE;
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "shockwave");

        int owner = weapon.getShip().getOwner();
        if (isFiring) {
            damageTick.advance(amount);
            waveRadius += WAVE_GROWTH*amount;
            float damageRadius = waveRadius * RANGE_CONVERSION;
            MagicRender.singleframe(sprite, location, new Vector2f(waveRadius,waveRadius), 0f, COLOR, true);
            if (damageTick.intervalElapsed()) {
                //currentColor = new Color((int)Math.max(0, currentColor.getRed()-500f*amount), currentColor.getGreen(), currentColor.getBlue(), currentColor.getAlpha());
                Iterator<Object> iter = Global.getCombatEngine().getAllObjectGrid().getCheckIterator(location, damageRadius* 2f, damageRadius* 2f);
                while(iter.hasNext()){
                    Object o = iter.next();
                    if (!(o instanceof CombatEntityAPI)) continue;
                    CombatEntityAPI other = (CombatEntityAPI) o;
                    if (other.getOwner() == owner) continue;
                    //if (other.getCollisionClass() == CollisionClass.NONE) continue;

                    if (o instanceof MissileAPI) {
                        MissileAPI otherMissile = (MissileAPI) o;
                        otherMissile.flameOut();
                        //otherMissile.setHitpoints(otherMissile.getHitpoints() - damage);
                        continue;
                    }
                    if (!(o instanceof ShipAPI)) continue;
                    ShipAPI otherShip = (ShipAPI) other;
                    if (otherShip.isPhased()) continue;
                    float radius = Misc.getTargetingRadius(weapon.getShip().getLocation(), other, false);
                    float dist = Misc.getDistance(location, other.getLocation())-radius;
                    if (dist < damageRadius && dist > damageRadius*DAMAGE_RANGE) {
                        Global.getCombatEngine().applyDamage(otherShip, otherShip.getLocation(), damage, DamageType.ENERGY, 0f, true, false, weapon.getShip());
                    }
                }
            }

            if (damageRadius > range) {
                isFiring = false;
                MagicRender.battlespace(sprite, location, new Vector2f(), new Vector2f(waveRadius,waveRadius), new Vector2f(20f, 20f), 0f, 0f, COLOR, true, 0f, 0f, 0.8f);
                waveRadius = 0f;
            }

        }
    }
}
