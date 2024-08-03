package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.combat.*;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DoomweaverEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {

    protected List<DamagingProjectileAPI> shots;
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (shots == null) return;
        Iterator<DamagingProjectileAPI> iter = shots.iterator();
        while (iter.hasNext()) {
            DamagingProjectileAPI shot = iter.next();
            if (shot.isExpired() || shot.isFading()) {
                iter.remove();
            } else {
                float scale = 1f;
                for (int i = 0; i < Math.round(amount); i++) {
                    scale *= 0.1f;
                }
                //shot.getVelocity().set(newVel);
                shot.getVelocity().x *= scale;
                shot.getVelocity().y *= scale;

            }
        }
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (shots == null) {
            shots = new ArrayList<DamagingProjectileAPI>();
        }
        shots.add(0, projectile);
    }
}
