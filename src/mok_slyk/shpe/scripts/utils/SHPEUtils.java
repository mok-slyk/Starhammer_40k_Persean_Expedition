package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.combat.BoundsAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;
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
}
