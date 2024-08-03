package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.MathUtils;

import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class BurstOnFireEffect implements OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    public static final float FLASH_DECAY = 1;
    float opacity;
    public Color COLOR = new Color(135, 231, 255, 153);
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        int id = MathUtils.getRandomNumberInRange(1,3);
        float size = MathUtils.getRandomNumberInRange(0f,3f)+9f;
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "pulse_"+id);
        float offset = 1f;
        Vector2f offsetVector = VectorUtils.rotate(new Vector2f(offset, 0), weapon.getCurrAngle());
        Vector2f flashLoc = new Vector2f(projectile.getLocation().x+offsetVector.x, projectile.getLocation().y+offsetVector.y);
        MagicRender.battlespace(sprite, flashLoc, new Vector2f(), new Vector2f(size,size), new Vector2f(1f, 1f), weapon.getCurrAngle(), 0f, COLOR, true, 0f, 0f, 0.1f);
    }
}
