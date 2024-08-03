package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPluginWithReset;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ParticleWhipOnFireEffect implements BeamEffectPluginWithReset {

    public final WeaponSpecAPI muzzleSpec = Global.getSettings().getWeaponSpec("shpe_particle_whip_flash");
    boolean spawnedFlash = false;
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        ShipAPI ship = beam.getSource();
        Vector2f beamSource = beam.getWeapon().getFirePoint(0);
        if (!spawnedFlash && beam.getBrightness() >= 0.99f) {
            engine.spawnMuzzleFlashOrSmoke(beam.getSource(), beam.getWeapon().getSlot(), muzzleSpec, 0, beam.getWeapon().getCurrAngle());
            for (int i = 0; i < 6; i++) {
                engine.spawnEmpArcVisual(beamSource, ship, new Vector2f((float) (beamSource.x + Math.random()*100f-50f), (float) (beamSource.y + Math.random()*100f-50f))
                        , ship, 2f, new Color(150,255,150,155) ,new Color(250,255,250,105));
            }
            spawnedFlash = true;
        }
    }

    @Override
    public void reset() {
        spawnedFlash = false;
    }
}
