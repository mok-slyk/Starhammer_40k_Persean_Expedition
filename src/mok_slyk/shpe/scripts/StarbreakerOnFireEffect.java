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
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.scaleVector;

public class StarbreakerOnFireEffect implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        //plan:
        //eigene onfireeffects für verschiedene lanzen
        //magicfakebeam equivalent in sepparater klasse
        /*
        MagicFakeBeam.spawnAdvancedFakeBeam(engine, projectile.getSpawnLocation(), weapon.getRange(), weapon.getCurrAngle(), 10, 8, 0, "trail_clean", "trail_fog",
                10, 3, 10, 30, 0.2f, 0.2f, 50, new Color(255, 255, 255, 250), new Color(255, 255, 160, 190), projectile.getDamageAmount(),
                projectile.getDamageType(), projectile.getEmpAmount(), weapon.getShip());
         */
        ///*
        SpriteAPI coreSprite = Global.getSettings().getSprite("fx", "base_trail_smooth");
        SpriteAPI fringeSprite = Global.getSettings().getSprite("fx", "base_trail_smoke");
        LanceBeam.spawnLanceBeam(engine, projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getRange(), scaleVector(weapon.getShip().getVelocity(), 0.8f),
                coreSprite, new Color(255, 214, 106), new Color(255, 196, 53), new Color(255, 151, 0), 4, 2, 0.7f, 100, 0, 1, 1f, 0, 0.3f, 0.2f,
                fringeSprite, new Color(255, 145, 0), new Color(255, 60, 0), new Color(215, 43, 95), 8, 5, 1.3f, 100, -200, 1, 0.8f, 0, 0.3f, 0.3f,
                5, 80, projectile.getDamageAmount(), projectile.getDamageType(), projectile.getEmpAmount(), 100, 0, weapon.getShip(), false, false);

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

         //*/

        engine.removeEntity(projectile);
    }
}
