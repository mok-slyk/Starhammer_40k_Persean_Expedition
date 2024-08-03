package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ProximityExplosionEffect;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.RiftCascadeEffect;
import com.fs.starfarer.api.impl.combat.RiftCascadeMineExplosion;
import com.fs.starfarer.api.loading.MissileSpecAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.impl.combat.RiftCascadeEffect.STANDARD_RIFT_COLOR;
import static mok_slyk.shpe.scripts.VortexTorpedoOnHitEffect.createVortexRiftParams;

public class JovianNovaOnExplosionEffect implements ProximityExplosionEffect {
    @Override
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        Color color = originalProjectile.getProjectileSpec().getGlowColor();
        NegativeExplosionVisual.NEParams p = createVortexRiftParams(color, 90f);
        p.fadeOut = 2f;
        p.hitGlowSizeMult = 1f;
        p.invertForDarkening = STANDARD_RIFT_COLOR;
        p.radius *= 0.5f;
        RiftCascadeMineExplosion.spawnStandardRift(explosion, p);

        Vector2f vel = new Vector2f();
        Global.getSoundPlayer().playSound("rifttorpedo_explosion", 1f, 1f, explosion.getLocation(), vel);
    }
}
