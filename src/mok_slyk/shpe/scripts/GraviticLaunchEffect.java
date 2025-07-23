package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import mok_slyk.shpe.SHPEModPlugin;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lwjgl.util.vector.Vector2f;

public class GraviticLaunchEffect implements EveryFrameWeaponEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (weapon.isFiring() && weapon.getCooldownRemaining() <= 0) {
            float range =200f;
            if (SHPEModPlugin.hasGraphicsLib) {
                float time = 3f;
                float rippleRange = range*0.9f;
                RippleDistortion ripple = new RippleDistortion(weapon.getShip().getLocation(), new Vector2f());
                DistortionShader.addDistortion(ripple);
                ripple.setSize(5);
                ripple.setIntensity(20);
                ripple.setFrameRate(30);
                //ripple.fadeOutSize(-0.5f);
                ripple.setDeltaSize(850);
                ripple.setMaxSize(2000);
                ripple.fadeOutIntensity(0.7f);
                float rotation = weapon.getShip().getFacing();
                ripple.setArc(90 + rotation, -90 + rotation);
                ripple.setArcAttenuationWidth(50);
            }
        }
    }
}
