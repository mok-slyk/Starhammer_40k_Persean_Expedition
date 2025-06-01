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
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.scaleVector;

public class LasburnerOnFireEffect implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        SpriteAPI coreSprite = Global.getSettings().getSprite("fx", "base_trail_smooth");
        SpriteAPI fringeSprite = Global.getSettings().getSprite("fx", "base_trail_smoke");
        LanceBeam.spawnLanceBeam(engine, projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getRange(), scaleVector(weapon.getShip().getVelocity(), 0.8f),
                coreSprite, new Color(255, 182, 75), new Color(255, 147, 21), new Color(255, 89, 0), 3, 1.5f, 0.7f, 100, 0, 1, 1f, 0, 0.3f, 0.2f,
                fringeSprite, new Color(255, 98, 0), new Color(255, 21, 0), new Color(204, 3, 61), 4.5f, 2.5f, 1.3f, 100, -200, 1, 0.8f, 0, 0.3f, 0.3f,
                5, 80, projectile.getDamageAmount(), projectile.getDamageType(), projectile.getEmpAmount(), 50, 0, weapon.getShip(), false, true, false, null);

        float flashSize = 30f;
        engine.addHitParticle(
                projectile.getSpawnLocation(),
                scaleVector(weapon.getShip().getVelocity(), 0.8f),
                (float) Math.random() * flashSize / 2 + flashSize,
                1,
                0.3f,
                new Color(255, 230, 99)
        );
        engine.addHitParticle(
                projectile.getSpawnLocation(),
                scaleVector(weapon.getShip().getVelocity(), 0.8f),
                (float) Math.random() * flashSize / 4 + flashSize / 2,
                1,
                0.9f,
                new Color(255, 83, 83)
        );
        //Muzzle Flash Glow:
        StandardLight flash = new StandardLight(projectile.getSpawnLocation(), scaleVector(weapon.getShip().getVelocity(), 0.8f), new Vector2f(), null, 0.3f, 50);
        flash.setColor(new Color(255, 230, 99));
        flash.fadeOut(1);
        LightShader.addLight(flash);

        engine.removeEntity(projectile);
    }
}
