package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ProximityExplosionEffect;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import mok_slyk.shpe.SHPEModPlugin;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Vector;

import static com.fs.starfarer.api.impl.combat.RiftCascadeEffect.STANDARD_RIFT_COLOR;
import static mok_slyk.shpe.scripts.VortexTorpedoOnHitEffect.createVortexRiftParams;

public class LatheNovaOnExplosionEffect implements ProximityExplosionEffect {
    @Override
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        /*
        Color color = originalProjectile.getProjectileSpec().getGlowColor();
        NegativeExplosionVisual.NEParams p = createVortexRiftParams(color, 90f);
        p.fadeOut = 2f;
        p.hitGlowSizeMult = 1f;
        p.invertForDarkening = Color.BLACK;
        p.radius *= 0.3f;
        RiftCascadeMineExplosion.spawnStandardRift(explosion, p);
         */

        Vector2f vel = new Vector2f();
        Global.getSoundPlayer().playSound("rifttorpedo_explosion", 1f, 1f, explosion.getLocation(), vel);

        float range =500f;
        if (SHPEModPlugin.hasGraphicsLib) {
            float time = range/250f;
            float rippleRange = range*0.9f;
            RippleDistortion ripple = new RippleDistortion(explosion.getLocation(), new Vector2f());
            DistortionShader.addDistortion(ripple);
            ripple.setSize(rippleRange);
            ripple.setIntensity(rippleRange*0.3f);
            ripple.setFrameRate(60f/time);
            ripple.fadeOutSize(time*1.2f);
            ripple.fadeOutIntensity(time);
        }



        for (CombatEntityAPI entity: CombatUtils.getEntitiesWithinRange(explosion.getLocation(), range*0.5f)) {
            CombatUtils.applyForce(entity, Vector2f.sub(explosion.getLocation(), entity.getLocation(), null), 7000f);
        }
    }
}
