package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class LightPlasmaCannonOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(target instanceof MissileAPI || target instanceof AsteroidAPI) return;

        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","plasma_hit_"+ MathUtils.getRandomNumberInRange(1, 3)),
                point,
                new Vector2f(),
                new Vector2f(10,10),
                new Vector2f(100,100),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(160,200,255,205),
                true,
                0,
                0.1f,
                0.5f
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","plasma_hit_"+ MathUtils.getRandomNumberInRange(1, 3)),
                point,
                new Vector2f(),
                new Vector2f(30,30),
                new Vector2f(50,50),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(195,225,255,185),
                true,
                0.2f,
                0.0f,
                0.5f
        );
    }
}
