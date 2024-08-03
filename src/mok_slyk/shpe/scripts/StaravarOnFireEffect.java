package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.WeaponSpecAPI;

public class StaravarOnFireEffect implements BeamEffectPluginWithReset {

    public final WeaponSpecAPI muzzleSpec = Global.getSettings().getWeaponSpec("shpe_staravar_flash");
    boolean spawnedFlash = false;
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        ShipAPI ship = beam.getSource();
        if (!spawnedFlash && beam.getBrightness() >= 0.99f) {
            engine.spawnMuzzleFlashOrSmoke(beam.getSource(), beam.getWeapon().getSlot(), muzzleSpec, 0, beam.getWeapon().getCurrAngle());
            spawnedFlash = true;
        }
    }

    @Override
    public void reset() {
        spawnedFlash = false;
    }
}
