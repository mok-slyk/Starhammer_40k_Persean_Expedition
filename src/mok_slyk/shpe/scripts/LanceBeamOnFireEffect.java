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
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;

import java.awt.*;

public class LanceBeamOnFireEffect implements OnFireEffectPlugin {
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
        SpriteAPI fringeSprite = Global.getSettings().getSprite("fx", "base_trail_smooth");
        LanceBeam.spawnLanceBeam(engine, projectile.getSpawnLocation(), weapon.getCurrAngle(), weapon.getRange(), (Vector2f) weapon.getShip().getVelocity().scale(0.8f),
                coreSprite, new Color(230, 230, 255), new Color(255, 255, 200), new Color(255, 200, 200), 5, 3, 1.2f, 100, 0, 1, 0.8f, 0, 0.2f, 0.2f,
                fringeSprite, new Color(66, 104, 255), new Color(255, 221, 0), new Color(215, 63, 43), 15, 10, 2, 100, 0, 1, 0.8f, 0.05f, 0.3f, 0.3f,
                5, 80, projectile.getDamageAmount(), projectile.getDamageType(), projectile.getEmpAmount(), 100, 0, weapon.getShip(), false, false);

        float flashSize = 30f;
        engine.addHitParticle(
                projectile.getSpawnLocation(),
                (Vector2f) weapon.getShip().getVelocity().scale(0.8f),
                (float) Math.random() * flashSize / 2 + flashSize,
                1,
                0.3f,
                new Color(255, 255, 140)
        );
        engine.addHitParticle(
                projectile.getSpawnLocation(),
                (Vector2f) weapon.getShip().getVelocity().scale(0.8f),
                (float) Math.random() * flashSize / 4 + flashSize / 2,
                1,
                0.9f,
                new Color(255, 190, 190)
        );
        //Muzzle Flash Glow:
        StandardLight flash = new StandardLight(projectile.getSpawnLocation(), (Vector2f) weapon.getShip().getVelocity().scale(0.8f), new Vector2f(), null, 0.4f, 50);
        flash.setColor(new Color(251, 255, 219));
        flash.fadeOut(1);
        LightShader.addLight(flash);

         //*/

        engine.removeEntity(projectile);
    }
}
