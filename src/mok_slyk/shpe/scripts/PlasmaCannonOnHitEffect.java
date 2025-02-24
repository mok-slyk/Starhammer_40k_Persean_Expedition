package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.Random;

public class PlasmaCannonOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(target instanceof MissileAPI || target instanceof AsteroidAPI) return;

        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","plasma_hit_"+ MathUtils.getRandomNumberInRange(1, 3)),
                point,
                new Vector2f(),
                new Vector2f(50,50),
                new Vector2f(200,200),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(180,200,255,255),
                true,
                0,
                0.1f,
                0.3f
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","plasma_hit_"+ MathUtils.getRandomNumberInRange(1, 3)),
                point,
                new Vector2f(),
                new Vector2f(60,60),
                new Vector2f(100,100),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(205,225,255,225),
                true,
                0.2f,
                0.0f,
                0.5f
        );
    }
}
