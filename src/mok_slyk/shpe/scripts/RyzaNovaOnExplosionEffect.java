package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ProximityExplosionEffect;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class RyzaNovaOnExplosionEffect implements ProximityExplosionEffect {
    @Override
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        //drawBurst(explosion, 600, new Color(255,255,255,60));
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","plasma_nova_hit_"+ MathUtils.getRandomNumberInRange(1, 2)),
                explosion.getLocation(),
                new Vector2f(),
                new Vector2f(90,90),
                new Vector2f(2740,2740),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(160,200,255,165),
                true,
                0,
                0.2f,
                0.4f
        );
        MagicRender.battlespace(
                Global.getSettings().getSprite("fx","plasma_nova_hit_"+ MathUtils.getRandomNumberInRange(1, 2)),
                explosion.getLocation(),
                new Vector2f(),
                new Vector2f(320,320),
                new Vector2f(1480,1480),
                //angle,
                360*(float)Math.random(),
                0,
                new Color(195,225,255,135),
                true,
                0.2f,
                0.2f,
                0.3f
        );
    }

    public static void drawBurst(DamagingProjectileAPI explosion, float size, Color color) {
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "nova_burst");
        MagicRender.battlespace(sprite, explosion.getLocation(), explosion.getVelocity(), new Vector2f(size, size), new Vector2f(3*size, 3*size), (float) (Math.random()*360f), 0f,
                color,
                true,
                0f,
                0.1f,
                0.8f
        );
    }
}
