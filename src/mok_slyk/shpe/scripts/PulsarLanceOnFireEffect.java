package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPluginWithReset;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicLensFlare;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class PulsarLanceOnFireEffect implements BeamEffectPluginWithReset {

    boolean spawnedFlash = false;
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        ShipAPI ship = beam.getSource();
        if (!spawnedFlash && beam.getBrightness() >= 0.99f) {
            spawnedFlash = true;
        }
        if (spawnedFlash) {
            SpriteAPI core = Global.getSettings().getSprite("fx", "starflare_core");
            SpriteAPI fringe = Global.getSettings().getSprite("fx", "starflare_fringe");
            Color coreColor = beam.getCoreColor();
            Color fringeColor = beam.getFringeColor();
            coreColor = new Color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), (int)(coreColor.getAlpha()*beam.getBrightness()));
            fringeColor = new Color(fringeColor.getRed(), fringeColor.getGreen(), fringeColor.getBlue(), (int)(fringeColor.getAlpha()*beam.getBrightness()));
            MagicRender.singleframe(fringe, beam.getFrom(), new Vector2f(30f, 30f), 0f, fringeColor, true);
            MagicRender.singleframe(core, beam.getFrom(), new Vector2f(30f, 30f), 0f, coreColor, true);
        }
    }

    @Override
    public void reset() {
        spawnedFlash = false;
    }
}
