package mok_slyk.shpe.scripts.utils;

import com.fs.graphics.anim.SpriteAnimation;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.LazyLib;
import org.lazywizard.lazylib.ModUtils;
import org.lazywizard.lazylib.opengl.ColorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.plugins.MagicFakeBeamPlugin;
import org.magiclib.util.MagicAnim;
import org.magiclib.util.MagicMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Deprecated
public class LanceBeamPlugin extends BaseEveryFrameCombatPlugin {
    public static List<lanceBeamData> activeBeams = new ArrayList<>();
    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null) {
            return;
        }

        if (!activeBeams.isEmpty()){
            float amount = (engine.isPaused() ? 0f : engine.getElapsedInLastFrame());

            for (Iterator<lanceBeamData> iter = activeBeams.iterator(); iter.hasNext(); ) {
                lanceBeamData data = iter.next();
                if (data.age > data.intro + data.full + data.fade) {
                    iter.remove();
                } else {

                }
            }
        }

    }

    public static class lanceBeamData {
        private float age = 0f;
        private SpriteAPI coreSprite; //sprite of the beam core
        private Color coreColorStart; //color the core is at the start
        private Color coreColorEnd; //color the core is at its end
        private Color coreColorFade = null; //color the core fades to, no color fade if null
        private float coreWidthStart; //width of the core at its start
        private float coreWidthEnd; //width of the core at its end
        private boolean doCoreIntroGrow = false; //whether the core grows from nothing during the intro
        private boolean doCoreFadeShrink = false; //whether the core shrinks away into nothing at the end
        private SpriteAPI fringeSprite; //fringe stuff analogous to core
        private Color fringeColorStart;
        private Color fringeColorEnd;
        private Color fringeColorFade = null;
        private float fringeWidthStart;
        private float fringeWidthEnd;
        private boolean doFringeIntroGrow = false; //whether the fringe grows from nothing during the intro
        private boolean doFringeFadeShrink = false; //whether the fringe shrinks away into nothing at the end
        private float intro; //time the beam winds up
        private float full; //time the beam is at full power
        private float fade; //time the beam fades out
        private Vector2f start; //spot the beam starts at
        private float angle; //angle of the beam
        private float length; //length of the beam

        public lanceBeamData(SpriteAPI coreSprite, Color coreColor, float coreWidth, SpriteAPI fringeSprite,
                             Color fringeColor, float fringeWidth, float intro, float full, float fade, Vector2f start, float angle, float length) {
            this.coreSprite = coreSprite;
            this.coreColorStart = coreColor;
            this.coreColorEnd = coreColor;
            this.coreWidthStart = coreWidth;
            this.coreWidthEnd = coreWidth;
            this.fringeSprite = fringeSprite;
            this.fringeColorStart = fringeColor;
            this.fringeColorEnd = fringeColor;
            this.fringeWidthStart = fringeWidth;
            this.fringeWidthEnd = fringeWidth;
            this.intro = intro;
            this.full = full;
            this.fade = fade;
            this.start = start;
            this.angle = angle;
            this.length = length;
        }
    }
}
