package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.subsystems.drones.DroneFormation;
import org.magiclib.subsystems.drones.PIDController;

import java.util.*;
import java.util.List;

public class ScreamerFormation extends DroneFormation {
    public float ENGAGEMENT_RANGE = 600f;
    protected float OFFSET_SCALE = 80f;

    protected float ROTATION_SPEED = 100f;
    protected float rotation = MathUtils.getRandomNumberInRange(30f, 90f);
    List<ShipAPI> currentTargets;
    List<Boolean> doAttackRuns = new ArrayList<>();
    Map<ShipAPI, Boolean> runMap = new HashMap<>();

    @Override
    public void advance(@NotNull ShipAPI shipAPI, @NotNull Map<ShipAPI, ? extends PIDController> map, float v) {
        //Map.Entry<ShipAPI, ? extends PIDController>[] droneArray = (Map.Entry<ShipAPI, ? extends PIDController>[]) map.entrySet().toArray();
        Iterator<? extends Map.Entry<ShipAPI, ? extends PIDController>> iter = map.entrySet().iterator();
        rotation+= ROTATION_SPEED*v;
        if (rotation > 360f) rotation -= 360;
        for (int i = 0; i < map.size(); i++) {
            if (!iter.hasNext()) break;
            Map.Entry<ShipAPI, ? extends PIDController> entry = iter.next();
            ShipAPI ship = entry.getKey();
            PIDController pid = entry.getValue();
            //pid.KdR = 1f;
            ShipAPI target = selectTarget(ship, shipAPI);
            float angleIncrease = 360f / map.size();
            float angle = angleIncrease * (i - 1);
            angle = angle + rotation;
            Vector2f homePoint = MathUtils.getPointOnCircumference(shipAPI.getLocation(), shipAPI.getCollisionRadius() * 0.9f, angle);
            //Vector2f homePoint = SHPEUtils.getRandomPointInBounds(shipAPI);
            //Vector2f homePoint = shipAPI.getLocation();
            Vector2f offset = new Vector2f((float) Math.random()*OFFSET_SCALE-0.5f*OFFSET_SCALE, (float) Math.random()*OFFSET_SCALE-0.5f*OFFSET_SCALE);
            Vector2f.add(homePoint,offset,homePoint);
            //Global.getCombatEngine().addNebulaParticle(homePoint, new Vector2f(), 20f, 1, 0, 1, 0.1f, new Color(255,255,255));
            if (target == null) {
                pid.move(homePoint, ship);
            } else {
                if (!runMap.containsKey(ship)) runMap.put(ship, true);
                if (runMap.get(ship)) {
                    pid.move(target.getLocation(), ship);
                } else {
                    Vector2f point = Vector2f.add(ship.getLocation(), (Vector2f) ship.getVelocity().normalise(null).scale(10f), null);
                    pid.move(point, ship);
                }
                if (runMap.get(ship) && Misc.getDistance(ship.getLocation(), target.getLocation())<=50f) {
                    runMap.put(ship, false);
                }
                if (!runMap.get(ship) && Misc.getDistance(ship.getLocation(), target.getLocation())>=ENGAGEMENT_RANGE) {
                    runMap.put(ship, true);
                }
            }
            pid.rotate(Misc.getAngleInDegrees(ship.getVelocity()),ship);
        }
    }

    protected ShipAPI selectTarget(ShipAPI ship, ShipAPI home) {
        List<ShipAPI> possibleEnemies = AIUtils.getNearbyEnemies(home, ENGAGEMENT_RANGE);
        ShipAPI best = null;
        for (ShipAPI enemy: possibleEnemies) {
            if (best == null || MathUtils.getDistanceSquared(ship, enemy)>MathUtils.getDistanceSquared(ship, best)) best = enemy;
        }
        return best;
    }
}
