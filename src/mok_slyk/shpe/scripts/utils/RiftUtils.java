package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual;
import com.fs.starfarer.api.impl.combat.NegativeExplosionVisual.NEParams;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class RiftUtils {
    public static void createWarpRift(CombatEngineAPI engine, Vector2f point, float radius, Color glow, Color underGlow, float fadeOut, float thickness) {
        CombatEntityAPI prev = null;
        for (int i = 0; i < 2; i++) {
            NEParams p = createWarpRiftParams(radius, glow, underGlow, fadeOut, thickness);
            p.radius *= 0.35f + 0.25f * (float) Math.random();

            p.withHitGlow = prev == null;
            Vector2f loc = new Vector2f(point);
            loc = Misc.getPointAtRadius(loc, p.radius * 0.4f);

            CombatEntityAPI e = engine.addLayeredRenderingPlugin(new NegativeExplosionVisual(p));
            e.getLocation().set(loc);

            if (prev != null) {
                float dist = Misc.getDistance(prev.getLocation(), loc);
                Vector2f vel = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(loc, prev.getLocation()));
                vel.scale(dist / (p.fadeIn + p.fadeOut) * 0.7f);
                e.getVelocity().set(vel);
            }

            prev = e;
        }
    }

    public static void createStandardWarpRift(Vector2f point, CombatEngineAPI engine){
        createWarpRift(engine, point, 20, new Color(255, 0, 255), new Color(140, 40, 120), 1.5f, 25);
    }

    public static NEParams createWarpRiftParams(float radius, Color glow, Color underGlow, float fadeOut, float thickness) {
        NEParams param = new NEParams(radius, thickness, glow);
        param.underglow = underGlow;
        param.fadeOut = fadeOut;
        return param;
    }
}
