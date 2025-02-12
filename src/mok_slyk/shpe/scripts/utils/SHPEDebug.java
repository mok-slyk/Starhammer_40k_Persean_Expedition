package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class SHPEDebug {
    public static Color DEFAULT = new Color(220, 130, 50);
    public static void drawDot(Vector2f point){
        drawDot(Global.getCombatEngine(), point, DEFAULT);
    }
    public static void drawDot(CombatEngineAPI engine, Vector2f point, Color color){
        SpriteAPI sprite = Global.getSettings().getSprite("debug", "dot");
        MagicRender.singleframe(sprite, point, new Vector2f(20, 20), 0, color, false);
    }

    public static void drawLine(Vector2f start, Vector2f end){
        drawLine(Global.getCombatEngine(), start, end, DEFAULT);
    }
    public static void drawLine(CombatEngineAPI engine, Vector2f start, Vector2f end, Color color){
        SpriteAPI sprite = Global.getSettings().getSprite("debug", "line");
        Vector2f dist = Vector2f.sub(end, start, null);
        float length = dist.length();
        Vector2f scale = new Vector2f(10, length);
        MagicRender.singleframe(sprite, Vector2f.add(start, (Vector2f) dist.scale(0.5f), null), scale, VectorUtils.getFacing(dist)-90, color, false);
    }

    public static void drawVector(Vector2f start, Vector2f end){
        drawVector(Global.getCombatEngine(), start, end, DEFAULT);
    }
    public static void drawVector(CombatEngineAPI engine, Vector2f start, Vector2f end, Color color){
        SpriteAPI sprite = Global.getSettings().getSprite("debug", "vector");
        Vector2f dist = Vector2f.sub(end, start, null);
        float length = dist.length();
        Vector2f scale = new Vector2f(10, length);
        MagicRender.singleframe(sprite, Vector2f.add(start, (Vector2f) dist.scale(0.5f), null), scale, VectorUtils.getFacing(dist)-90, color, false);
    }
}
