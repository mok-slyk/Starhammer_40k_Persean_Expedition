package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import mok_slyk.shpe.SHPEModPlugin;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lwjgl.util.vector.Vector2f;

public class SonicChargeOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        float range =200;
        if (SHPEModPlugin.hasGraphicsLib) {
            float time = range/250f;
            float rippleRange = range*0.9f;
            RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
            DistortionShader.addDistortion(ripple);
            ripple.setSize(rippleRange);
            ripple.setIntensity(rippleRange*0.5f);
            ripple.setFrameRate(120f/time);
            ripple.fadeInSize(time*1.2f);
            ripple.fadeOutIntensity(time);
            ripple.setSize(rippleRange*0.01f);
        }
    }
}
