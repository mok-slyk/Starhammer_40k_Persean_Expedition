package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import mok_slyk.shpe.scripts.utils.LanceBeam;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.scaleVector;

public class LanceBeamOnFireEffect implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

        SpriteAPI coreSprite = Global.getSettings().getSprite("fx", "base_trail_smooth");
        SpriteAPI fringeSprite = Global.getSettings().getSprite("fx", "base_trail_smooth");
        LanceBeam.spawnLanceBeam(engine, projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getRange(), scaleVector(weapon.getShip().getVelocity(), 0.8f),
                coreSprite, new Color(230, 230, 255), new Color(255, 255, 200), new Color(255, 200, 200), 9, 7, 0.3f, 100, 0, 1, 0.9f, 0, 0.4f, 0.2f,
                fringeSprite, new Color(68, 134, 255), new Color(255, 221, 0), new Color(215, 63, 43), 15, 10, 2, 100, 0, 1, 0.8f, 0.1f, 0.4f, 0.3f,
                5, 80, projectile.getDamageAmount(), projectile.getDamageType(), projectile.getEmpAmount(), 100, 0, weapon.getShip(), false, true, true, null);

        float flashSize = 30f;
        engine.addHitParticle(
                projectile.getSpawnLocation(),
                scaleVector(weapon.getShip().getVelocity(), 0.8f),
                (float) Math.random() * flashSize / 2 + flashSize,
                1,
                0.3f,
                new Color(255, 255, 140)
        );
        engine.addHitParticle(
                projectile.getSpawnLocation(),
                scaleVector(weapon.getShip().getVelocity(), 0.8f),
                (float) Math.random() * flashSize / 4 + flashSize / 2,
                1,
                0.9f,
                new Color(255, 190, 190)
        );
        //Muzzle Flash Glow:
        StandardLight flash = new StandardLight(projectile.getSpawnLocation(), scaleVector(weapon.getShip().getVelocity(), 0.8f), new Vector2f(), null, 0.4f, 50);
        flash.setColor(new Color(251, 255, 219));
        flash.fadeOut(1);
        LightShader.addLight(flash);

        engine.removeEntity(projectile);
    }
}
