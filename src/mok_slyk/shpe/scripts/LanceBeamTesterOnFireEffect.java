package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import mok_slyk.shpe.scripts.utils.LanceBeam;
import mok_slyk.shpe.scripts.utils.LanceBeamObject;
import mok_slyk.shpe.scripts.utils.LanceBeamSpawner;
import mok_slyk.shpe.scripts.utils.SHPEUtils;
import org.dark.shaders.light.LightShader;
import org.dark.shaders.light.StandardLight;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEUtils.scaleVector;

public class LanceBeamTesterOnFireEffect implements OnFireEffectPlugin {
    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        SpriteAPI coreSprite = Global.getSettings().getSprite("fx", "trail_tester");
        SpriteAPI fringeSprite = Global.getSettings().getSprite("fx", "base_trail_smoke");

        LanceBeamObject beam = LanceBeamSpawner.spawnSimpleLanceBeam(engine, projectile.getSpawnLocation(), projectile.getFacing(), weapon.getRange(), 2f,2f, 2f, coreSprite, new Color(255, 145, 0), new Color(255, 60, 0), new Color(1, 43, 190), 5, 15, 5, projectile.getDamageAmount(), projectile.getDamageType(), projectile.getEmpAmount(), weapon, SHPEUtils.findClosestBarrel(weapon, projectile.getLocation()), 0.5f );

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
