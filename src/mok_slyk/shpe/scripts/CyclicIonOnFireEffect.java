package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPluginWithReset;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class CyclicIonOnFireEffect implements BeamEffectPluginWithReset {

    boolean spawnedFlash = false;
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        ShipAPI ship = beam.getSource();
        if (beam.getBrightness() >= 0.01f) {
            SpriteAPI glow = Global.getSettings().getSprite("fx", "soft_glow");
            Color coreColor = beam.getCoreColor();
            Color fringeColor = beam.getFringeColor();
            coreColor = new Color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), (int)(coreColor.getAlpha()*beam.getBrightness()));
            fringeColor = new Color(fringeColor.getRed(), fringeColor.getGreen(), fringeColor.getBlue(), (int)(fringeColor.getAlpha()*beam.getBrightness()));
            float coreSize = 10+10*beam.getBrightness();
            float fringeSize = 15+20*beam.getBrightness();
            MagicRender.singleframe(glow, beam.getFrom(), new Vector2f(fringeSize, fringeSize), 0f, fringeColor, true);
            MagicRender.singleframe(glow, beam.getFrom(), new Vector2f(fringeSize, fringeSize), 0f, coreColor, true);
        }
    }

    @Override
    public void reset() {
        spawnedFlash = false;
    }
}
