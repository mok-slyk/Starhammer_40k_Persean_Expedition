package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.FleetMemberDeploymentListener;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import mok_slyk.shpe.scripts.hullmods.GraviticInterlock;

import java.util.*;

public class GraviticHookCombatPlugin implements EveryFrameCombatPlugin {
    public static Set<ShipAPI> hookQueue = new HashSet<>();
    private static Set<ShipAPI> hookDequeue = new HashSet<>();
    public static Set<ShipAPI> interlockQueue = new HashSet<>();
    private static Set<ShipAPI> interlockDequeue = new HashSet<>();
    public static Set<ShipAPI> handled = new HashSet<>();
    public static Map<ShipAPI, List<ShipAPI>> inCombatMatches = new HashMap<>(); //hook to interlocks
    public static Map<String, String> fleetPairs = new HashMap<>(); //interlock to hook

    public static void ensureExists() {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (!engine.hasPluginOfClass(GraviticHookCombatPlugin.class)) {
            engine.addPlugin(new GraviticHookCombatPlugin());
        }
    }
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        // handle queue
        for (ShipAPI interlock : interlockQueue) {
            if (fleetPairs.containsKey(interlock.getFleetMember().getId())) {
                String carrier = fleetPairs.get(interlock.getFleetMember().getId());
                for (ShipAPI hook: hookQueue) {
                    if (Objects.equals(hook.getFleetMember().getId(), carrier)) {
                        inCombatMatches.computeIfAbsent(hook, k -> new ArrayList<>());
                        inCombatMatches.get(hook).add(interlock);
                    }
                }
            }
            handled.add(interlock);
            interlockDequeue.add(interlock);
        }
        for (ShipAPI hook : hookQueue) {
            handled.add(hook);
            hookDequeue.add(hook);
        }
        hookQueue.removeAll(hookDequeue);
        hookDequeue.clear();
        interlockQueue.removeAll(interlockDequeue);
        interlockDequeue.clear();

        //handle matches
        for (Map.Entry<ShipAPI, List<ShipAPI>> entry : inCombatMatches.entrySet()) {
            ShipAPI carrier = entry.getKey();
            if (carrier.getTravelDrive().getState() == ShipSystemAPI.SystemState.OUT) {
                for(ShipAPI ship : entry.getValue()) {
                    ship.setCollisionClass(CollisionClass.SHIP);
                    ship.setAlphaMult(1);
                }
            } else if (carrier.getTravelDrive().getState() == ShipSystemAPI.SystemState.IN || carrier.getTravelDrive().getState() == ShipSystemAPI.SystemState.ACTIVE) {
                for(ShipAPI ship : entry.getValue()) {
                    ship.setCollisionClass(CollisionClass.NONE);
                    ship.setAlphaMult(0);
                    ship.getLocation().set(carrier.getLocation());
                    ship.getVelocity().set(carrier.getVelocity());
                }
            }
        }
    }
    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {}
    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {}

    @Override
    public void renderInUICoords(ViewportAPI viewport) {}

    @Override @SuppressWarnings("unchecked")
    public void init(CombatEngineAPI engine) {
        fleetPairs = (Map<String, String>) Global.getSector().getPlayerMemoryWithoutUpdate().get(GraviticInterlock.KEY);
        GraviticHookListener.ensureExists();
    }

    public static class GraviticHookListener implements FleetMemberDeploymentListener {
        @Override
        public void reportFleetMemberDeployed(DeployedFleetMemberAPI member) {
            if (member.getShip().getVariant().hasHullMod("shpe_warden_interlock")) {
                // do warden stuff
                GraviticHookCombatPlugin.ensureExists();
                ShipAPI ship = member.getShip();
                if (!GraviticHookCombatPlugin.handled.contains(ship)) {
                    GraviticHookCombatPlugin.interlockQueue.add(ship);
                }
            } else if (member.getShip().getVariant().hasHullMod("shpe_enclosed_hook")) {
                // do custodian stuff
                GraviticHookCombatPlugin.ensureExists();
                ShipAPI ship = member.getShip();
                if (!GraviticHookCombatPlugin.handled.contains(ship)) {
                    GraviticHookCombatPlugin.hookQueue.add(ship);
                }
            }
        }

        public static void ensureExists() {
            CombatEngineAPI engine = Global.getCombatEngine();
            if (!engine.getListenerManager().hasListenerOfClass(GraviticHookListener.class)) {
                engine.getListenerManager().addListener(new GraviticHookListener());
            }
        }
    }
}
