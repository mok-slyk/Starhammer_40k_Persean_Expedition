package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SHPEUtils {
    protected static Random rand = new Random();
    private static final float EPS = 1e-6f;

    /**
     * returns a random point somewhere in or around the given bounds, not guaranteed to produce a point actually within the bounds on concave shapes.
     * @param entity the bounds to find a point around
     * @return the random point
     */
    public static Vector2f getRandomPointInBounds(CombatEntityAPI entity){
        Vector2f point = MathUtils.getRandomPointInCircle(entity.getLocation(), entity.getCollisionRadius());

        while (!CollisionUtils.isPointWithinBounds(point, entity)) {
            point = MathUtils.getRandomPointInCircle(entity.getLocation(), entity.getCollisionRadius());
        }
        return point;
    }

    public static Vector2f getClosestPointOnCircleInCone(Vector2f point, float facing, float range, float angle, Vector2f center, float radius) {
        Vector2f closest = getClosestPointOnCircle(point, center, radius);
        if (closest != null && Math.abs((VectorUtils.getAngle(point, closest)-facing + 540) % 360 -180) < angle) return closest;
        Vector2f endL = Vector2f.add(point, VectorUtils.rotate(new Vector2f(range, 0), facing+angle), null);
        //SHPEDebug.drawLine(point, endL);
        Vector2f intersectL = MagicFakeBeam.getCollisionPointOnCircumference(point, endL, center, radius);
        if (intersectL != null) return intersectL;
        Vector2f endR = Vector2f.add(point, VectorUtils.rotate(new Vector2f(range, 0), facing-angle), null);
        //SHPEDebug.drawLine(point, endR);
        Vector2f intersectR = MagicFakeBeam.getCollisionPointOnCircumference(point, endR, center, radius);
        return intersectR;
    }

    public static Vector2f getClosestPointOnCircle(Vector2f point, Vector2f center, float radius) {
        Vector2f v = Vector2f.sub(point, center, null);
        float len = v.length();

        if (len < EPS) {
            return null;
        }

        v = (Vector2f) v.normalise().scale(radius);

        return Vector2f.add(center, v, null);
    }

    public static Vector2f getRandomPointInBoundsAndCircle(CombatEntityAPI entity, Vector2f center, float radius, int resolution) {
        float minX = center.x - radius;
        float maxX = center.x + radius;
        float minY = center.y - radius;
        float maxY = center.y + radius;

        for (BoundsAPI.SegmentAPI p : entity.getExactBounds().getSegments()) {
            minX = Math.min(minX, p.getP1().x);
            maxX = Math.max(maxX, p.getP1().x);
            minY = Math.min(minY, p.getP1().y);
            maxY = Math.max(maxY, p.getP1().y);
        }

        float width = maxX - minX;
        float height = maxY - minY;

        float cellW = width / resolution;
        float cellH = height / resolution;

        List<SamplingCell> validCells = new ArrayList<>();
        for (int gx = 0; gx < resolution; gx++) {
            for (int gy = 0; gy < resolution; gy++) {
                float cx = minX + (gx + 0.5f) * cellW;
                float cy = minY + (gy + 0.5f) * cellH;
                Vector2f cellCenter = new Vector2f(cx, cy);

                if (MathUtils.isPointWithinCircle(cellCenter, center, radius)
                        && CollisionUtils.isPointWithinBounds(cellCenter, entity)) {
                    validCells.add(new SamplingCell(gx, gy, cx, cy));
                }
            }
        }

        if (validCells.isEmpty()) return null;

        SamplingCell chosen = validCells.get(rand.nextInt(validCells.size()));

        float rx = (float) (rand.nextDouble() * cellW);
        float ry = (float) (rand.nextDouble() * cellH);
        return new Vector2f(
                minX + chosen.gx * cellW + rx,
                minY + chosen.gy * cellH + ry
        );
    }
    private static class SamplingCell {
        int gx, gy;
        float cx, cy;
        SamplingCell(int gx, int gy, float cx, float cy) {
            this.gx = gx; this.gy = gy; this.cx = cx; this.cy = cy;
        }
    }

    public static BoundsAPI.SegmentAPI getRandomBoundsSegment(BoundsAPI bounds) {
        List<BoundsAPI.SegmentAPI> segments = bounds.getSegments();
        return segments.get(rand.nextInt(segments.size()));
    }

    /**
     * returns a scaled version of a vector without modifying the original.
     * @param vec the vector to scale; will not be modified
     * @param fac the factor to scale the vector by
     * @return a scaled version of the vector
     */
    public static Vector2f scaleVector(Vector2f vec, float fac) {
        return new Vector2f(vec.x*fac, vec.y*fac);
    }

    public static Vector2f collinearVectorOfScale(Vector2f vec, float scale) {
        Vector2f vec2 = new Vector2f(vec);
        if (vec2.length() == 0) return new Vector2f(0, 0);
        return (Vector2f) vec2.normalise().scale(scale);
    }

    public static Vector2f getLineEntityCollisionPoint(CombatEntityAPI entity, Vector2f start, Vector2f end) {
        BoundsAPI bounds = entity.getExactBounds();
        if (bounds == null) {
            return MagicFakeBeam.getCollisionPointOnCircumference(start, end, entity.getLocation(), entity.getCollisionRadius());
        }
        bounds.update(entity.getLocation(), entity.getFacing());

        if (CollisionUtils.isPointWithinBounds(start, entity))
        {
            return new Vector2f(start);
        }
        /*
        if (CollisionUtils.isPointWithinBounds(end, entity))
        {
            return new Vector2f(end);
        }
         */

        Vector2f closestPoint = null;
        float closestDistanceSquared = Float.MAX_VALUE;

        for (BoundsAPI.SegmentAPI tmp : bounds.getSegments()) {
            Vector2f intersect = CollisionUtils.getCollisionPoint(start, end, tmp.getP1(), tmp.getP2());
            if (intersect != null) {
                float distanceSquared = MathUtils.getDistanceSquared(start, intersect);
                if (distanceSquared < closestDistanceSquared) {
                    closestPoint = new Vector2f(intersect);
                    closestDistanceSquared = distanceSquared;
                }
            }
        }
        return closestPoint;
    }

    public static Color averageColor(Color... colors) {
        int r = 0, g = 0, b = 0, a = 0;
        for (Color color : colors) {
            r += color.getRed();
            g += color.getGreen();
            b += color.getBlue();
            a += color.getAlpha();
        }
        int n = colors.length;
        return new Color(r / n, g / n, b / n, a / n);
    }

    public static Color lerpColor(Color color1, Color color2, float fac) {
        MathUtils.clamp(fac, 0, 1);
        float r = 0, g = 0, b = 0, a = 0;
        float fac1 = 1 - fac;
        r = fac1 * color1.getRed() + fac * color2.getRed();
        g = fac1 * color1.getGreen() + fac * color2.getGreen();
        b = fac1 * color1.getBlue() + fac * color2.getBlue();
        a = fac1 * color1.getAlpha() + fac * color2.getAlpha();

        return new Color(r, g, b, a);
    }
}
