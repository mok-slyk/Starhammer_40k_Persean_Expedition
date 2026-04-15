package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.FleetMemberDeploymentListener;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import mok_slyk.shpe.scripts.hullmods.GraviticInterlock;
import static mok_slyk.shpe.scripts.utils.SHPEUtils.*;
import mok_slyk.shpe.scripts.utils.Witchcraft;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.*;

public class GraviticHookCombatPlugin implements EveryFrameCombatPlugin {
    private static Logger log = Global.getLogger(GraviticHookCombatPlugin.class);
    public static Set<ShipAPI> hookQueue = new HashSet<>();
    private static Set<ShipAPI> hookDequeue = new HashSet<>();
    public static Set<ShipAPI> interlockQueue = new HashSet<>();
    private static Set<ShipAPI> interlockDequeue = new HashSet<>();
    public static Set<ShipAPI> handled = new HashSet<>();
    public static Map<ShipAPI, List<ShipAPI>> inCombatMatches = new HashMap<>(); //hook to interlocks
    public static Set<Map.Entry<ShipAPI, List<ShipAPI>>> inCombatMatchesDelete = new HashSet<>();
    public static Map<String, String> fleetPairs = new HashMap<>(); //interlock to hook

    private boolean isInitialized = false;

    public static void ensureExists() {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (!engine.hasPluginOfClass(GraviticHookCombatPlugin.class)) {
            engine.addPlugin(new GraviticHookCombatPlugin());
        }
        GraviticHookListener.ensureExists();
    }
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if(!isInitialized) initialize();

        // handle queue
        for (ShipAPI interlock : interlockQueue) {
            if (fleetPairs.containsKey(interlock.getFleetMember().getId())) {
                String carrier = fleetPairs.get(interlock.getFleetMember().getId());
                for (ShipAPI hook: hookQueue) {
                    if (Objects.equals(hook.getFleetMember().getId(), carrier)) {
                        inCombatMatches.computeIfAbsent(hook, k -> new ArrayList<>());
                        inCombatMatches.get(hook).add(interlock);
                        handled.add(hook);
                        //hookDequeue.add(hook);
                    }
                }
            }
            handled.add(interlock);
            interlockDequeue.add(interlock);
        }
        /*
        for (ShipAPI hook : hookQueue) {
            handled.add(hook);
            hookDequeue.add(hook);
        }
         */
        hookQueue.removeAll(hookDequeue);
        hookDequeue.clear();
        interlockQueue.removeAll(interlockDequeue);
        interlockDequeue.clear();

        /*
        log.info("fleetpairs: "+fleetPairs);
        log.info("hookqueue: "+hookQueue);
        log.info("interlockqueue: "+interlockQueue);
        log.info(inCombatMatches);

         */

        //handle matches
        for (Map.Entry<ShipAPI, List<ShipAPI>> entry : inCombatMatches.entrySet()) {
            ShipAPI carrier = entry.getKey();
            if (entry.getValue().isEmpty()) {
                inCombatMatchesDelete.add(entry);
                continue;
            }
            ShipAPI carried = entry.getValue().get(0);

            for (int i = 1; i < entry.getValue().size(); i++) {
                ShipAPI ship = entry.getValue().get(i);
                // put ship in stasis
                ship.getLocation().set(carrier.getLocation());
                ship.setCollisionClass(CollisionClass.NONE);
                ship.setAlphaMult(0);
                ship.setDoNotRender(true);
                ship.setBeingIgnored(true);
                ship.setTimeDeployed(0);
            }

            if (carried.getTravelDrive().getState() == ShipSystemAPI.SystemState.IN) {
                carried.setCollisionClass(CollisionClass.NONE);
                carried.setAlphaMult(0);
                carried.getLocation().set(carrier.getLocation());
                carried.getVelocity().set(carrier.getVelocity());
                carried.setDoNotRender(false);
                carried.setBeingIgnored(false);

            } else if (carried.getTravelDrive().getState() == ShipSystemAPI.SystemState.ACTIVE) {
                carried.setCollisionClass(CollisionClass.NONE);
                carried.setAlphaMult(0);
                Vector2f spawnPoint = Vector2f.add(VectorUtils.rotate(new Vector2f(100, 0), carrier.getFacing()), carrier.getLocation(), null);
                carried.getLocation().set(spawnPoint);
                carried.getVelocity().set(carrier.getVelocity());
                carried.setDoNotRender(false);
                carried.setBeingIgnored(false);

            } else if (carried.getTravelDrive().getState() == ShipSystemAPI.SystemState.OUT) {
                carried.setCollisionClass(CollisionClass.FIGHTER);
                carried.setAlphaMult(1-carried.getTravelDrive().getEffectLevel());

                Vector2f exitVelocity = Vector2f.add(collinearVectorOfScale(carrier.getVelocity(), 120f), carrier.getVelocity(), null);
                carried.getVelocity().set(exitVelocity);
                carried.setDoNotRender(false);
                carried.setBeingIgnored(false);

                if (carried.getShield() != null) carried.getShield().toggleOff();
                if (carrier.getShield() != null) carrier.getShield().toggleOff();

            } else {
                if ((MathUtils.getDistance(carrier.getLocation(), carried.getLocation()) > 40 || carried.getFullTimeDeployed() > 1.5f) && carried.getFullTimeDeployed() > 0.2f) {
                    carried.setCollisionClass(CollisionClass.SHIP);
                    carried.setAlphaMult(1);
                    carried.setDoNotRender(false);
                    carried.setBeingIgnored(false);
                    entry.getValue().remove(0);
                }
            }

            /*
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

             */
        }

        inCombatMatches.entrySet().removeAll(inCombatMatchesDelete);
        inCombatMatchesDelete.clear();
    }
    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {}
    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {}

    @Override
    public void renderInUICoords(ViewportAPI viewport) {}

    @SuppressWarnings("unchecked")
    public void initialize() {
        Map<String, String> savedFleetPairs = (Map<String, String>) Global.getSector().getPlayerMemoryWithoutUpdate().get(GraviticInterlock.KEY);
        fleetPairs = savedFleetPairs != null ? savedFleetPairs : new HashMap<>();
        GraviticHookListener.ensureExists();
        isInitialized = true;
    }

    @Override @SuppressWarnings("deprecation")
    public void init(CombatEngineAPI engine) {

    }

    public static class GraviticHookListener implements FleetMemberDeploymentListener {
        @Override
        public void reportFleetMemberDeployed(DeployedFleetMemberAPI member) {
            log.info("deployd");
            if (member.getShip().getVariant().hasHullMod("shpe_warden_interlock")) {
                log.info("warden");
                // do warden stuff
                GraviticHookCombatPlugin.ensureExists();
                ShipAPI ship = member.getShip();
                if (!GraviticHookCombatPlugin.handled.contains(ship)) {
                    GraviticHookCombatPlugin.interlockQueue.add(ship);
                }
            } else if (member.getShip().getVariant().hasHullMod("shpe_enclosed_hook")) {
                log.info("custo");
                // do custodian stuff
                GraviticHookCombatPlugin.ensureExists();
                ShipAPI ship = member.getShip();
                if (!GraviticHookCombatPlugin.handled.contains(ship)) {
                    GraviticHookCombatPlugin.hookQueue.add(ship);
                }
            }
            log.info("fleetpairs: "+fleetPairs);
            log.info("hookqueue: "+hookQueue);
            log.info("interlockqueue: "+interlockQueue);
            log.info(inCombatMatches);
        }

        public static void ensureExists() {
            //log.info("ensured listener");
            CombatEngineAPI engine = Global.getCombatEngine();
            if (!engine.getListenerManager().hasListenerOfClass(GraviticHookListener.class)) {
                engine.getListenerManager().addListener(new GraviticHookListener());
            }
        }
    }
}
