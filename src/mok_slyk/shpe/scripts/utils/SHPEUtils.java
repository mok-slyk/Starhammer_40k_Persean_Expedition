package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicFakeBeam;
import org.magiclib.util.MagicMisc;

import java.util.List;
import java.util.Random;

public class SHPEUtils {
    protected static Random rand = new Random();

    /**
     * returns a random point somewhere in or around the given bounds, not guaranteed to produce a point actually within the bounds on concave shapes.
     * @param entity the bounds to find a point around
     * @return the random point
     */
    public static Vector2f getRandomPointInBounds(CombatEntityAPI entity){
        BoundsAPI.SegmentAPI seg1 = null;
        BoundsAPI.SegmentAPI seg2 = null;
        Vector2f point = MathUtils.getRandomPointInCircle(entity.getLocation(), entity.getCollisionRadius());

        while (!CollisionUtils.isPointWithinBounds(point, entity)) {
            point = MathUtils.getRandomPointInCircle(entity.getLocation(), entity.getCollisionRadius());
        }
        return point;
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
        if (CollisionUtils.isPointWithinBounds(end, entity))
        {
            return new Vector2f(end);
        }

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
}
