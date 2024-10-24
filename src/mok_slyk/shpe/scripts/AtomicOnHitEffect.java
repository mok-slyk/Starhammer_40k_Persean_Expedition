package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class AtomicOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        DamagingExplosionSpec spec = new DamagingExplosionSpec(3, 40, 20, 500, 200, CollisionClass.PROJECTILE_FF, CollisionClass.PROJECTILE_FIGHTER,
                5, 50, 1.5f, 20, new Color(255, 200, 150, 150), new Color(235, 255, 180, 220));
        spec.setUseDetailedExplosion(true);
        spec.setDetailedExplosionRadius(50);
        spec.setDetailedExplosionFlashRadius(50);
        spec.setDetailedExplosionFlashDuration(2);
        spec.setDetailedExplosionFlashColorFringe(new Color(255, 235, 120, 220));
        spec.setDetailedExplosionFlashColorCore(new Color(235, 255, 190, 220));

        engine.spawnDamagingExplosion(spec, projectile.getSource(), point, true);

    }
}
