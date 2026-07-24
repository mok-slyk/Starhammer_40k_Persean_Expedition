package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import mok_slyk.shpe.scripts.utils.Witchcraft;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InterceptorDroneWeaponEffect implements EveryFrameWeaponEffectPlugin {
    private static final Logger LOG = Global.getLogger(InterceptorDroneWeaponEffect.class);
    Map<WeaponAPI, Boolean> hadFallingEdge = new HashMap<>();
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        ShipSystemAPI system = ship.getSystem();
        DroneLauncherShipSystemAPI launcher = (DroneLauncherShipSystemAPI) ship.getSystem();
        int slot = Integer.parseInt(weapon.getSlot().getId());

        LOG.info(slot + ": " + hadFallingEdge);

        ShipAPI drone = null;

        List<ShipAPI> ships = ship.getDeployedDrones();
        for (ShipAPI potentialDrone: ships) {
            if (launcher.getIndex(potentialDrone) == slot - 1) {
                drone = potentialDrone;
                break;
            }
        }

        if (system.getAmmo() < slot) {
            weapon.getAnimation().setFrame(1);
            if (!hadFallingEdge.getOrDefault(weapon, false) && drone != null) {
                LOG.info("fired");
                hadFallingEdge.put(weapon, true);
                drone.getLocation().set(weapon.getLocation());
                drone.setFacing(ship.getFacing());
                Witchcraft.setFieldInObject(drone, "isAnimatedLaunch", false);
            }
        } else {
            weapon.getAnimation().setFrame(0);
            hadFallingEdge.put(weapon, false);
        }
    }
}
