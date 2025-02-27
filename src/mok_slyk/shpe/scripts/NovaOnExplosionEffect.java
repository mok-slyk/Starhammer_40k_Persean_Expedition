package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ProximityExplosionEffect;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class NovaOnExplosionEffect implements ProximityExplosionEffect {
    @Override
    public void onExplosion(DamagingProjectileAPI explosion, DamagingProjectileAPI originalProjectile) {
        drawBurst(explosion, 600, new Color(255,255,255,150));
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
