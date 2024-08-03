package mok_slyk.shpe.scripts;

import com.fs.graphics.Sprite;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.opengl.DrawUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SonicChargeOnFireEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    private List<DamagingProjectileAPI> projectiles = new ArrayList<>();
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        /*
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "sonic_charge");
        for (DamagingProjectileAPI proj: projectiles) {
            float intensity = (float) Math.abs(Math.sin(proj.getElapsed()));
            Color color = new Color(255, 255, 255, (int)(intensity*255));
            MagicRender.singleframe(sprite, proj.getLocation(), new Vector2f(sprite.getWidth(), sprite.getHeight()), 0, color, true);
        }

         */
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        projectiles.add(projectile);
    }
}
