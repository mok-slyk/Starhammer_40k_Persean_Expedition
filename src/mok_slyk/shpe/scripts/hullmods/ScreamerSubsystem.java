package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.magiclib.subsystems.drones.DroneFormation;
import org.magiclib.subsystems.drones.HoveringFormation;
import org.magiclib.subsystems.drones.MagicDroneSubsystem;
import org.magiclib.subsystems.drones.PIDController;

import java.util.List;
import java.util.Objects;

/**
 * Spawns a PD drone. Has no usable key and doesn't take a key index.
 */
public class ScreamerSubsystem extends MagicDroneSubsystem {
    protected int numScreamers;
    private static final Logger LOG = Global.getLogger(ScreamerSubsystem.class);
    DroneFormation formation = new ScreamerFormation();
    public ScreamerSubsystem(ShipAPI ship, int numScreamers) {
        super(ship);
        setFormation(formation);
        this.numScreamers = numScreamers;
    }

    /*
    @NotNull
    @Override
    public ShipAPI spawnDrone() {


        FighterLaunchBayAPI screamerBay = null;
        WeaponSlotAPI screamerSlot = ship.getVariant().getSlot("fakeScreamerBay");

        LOG.info("test111");
        LOG.warn("test222");
        List<FighterLaunchBayAPI> bays = ship.getLaunchBaysCopy();
        LOG.warn(bays.size());
        LOG.warn(ship.hasLaunchBays());
        for (FighterLaunchBayAPI bay: bays) {
            LOG.info(bay.getWeaponSlot().getId());
            if (bay.getWeaponSlot().getId().equals("fakeScreamerBay")) {
                screamerBay = bay;
                break;
            }
        }
        if (screamerBay == null) throw new RuntimeException();




        Global.getCombatEngine().getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(true);
        FleetSide fleetSide = FleetSide.values()[ship.getOwner()];
        ShipAPI fighter = CombatUtils.spawnShipOrWingDirectly(
                getDroneVariant(),
                FleetMemberType.FIGHTER_WING,
                fleetSide,
                0.7f,
                ship.getLocation(),
                ship.getFacing()
        );
        getActiveWings().put(fighter, getPIDController());
        FighterWingSpec spec = (FighterWingSpec) Global.getSettings().getFighterWingSpec(getDroneVariant());
        screamerBay = new C(spec, (Y) screamerSlot, (Ship) ship);
        L wing = new L(spec, ship.getOwner(), 90);
        //wing.setOrder(o);

        wing.setSourceBay(screamerBay);
        FighterAI ai = new FighterAI((Ship) fighter, wing);
        fighter.setShipAI(ai);
        fighter.giveCommand(ShipCommand.SELECT_GROUP, null, 99);
        Global.getCombatEngine().getFleetManager(ship.getOwner()).setSuppressDeploymentMessages(false);
        return fighter;
    }
    */

    @Override
    public boolean canAssignKey() {
        return false;
    }

    @Override
    public float getBaseActiveDuration() {
        return 0;
    }

    @Override
    public float getBaseCooldownDuration() {
        return 0;
    }

    @Override
    public boolean shouldActivateAI(float amount) {
        return canActivate();
    }

    @Override
    public float getBaseChargeRechargeDuration() {
        return 10f;
    }

    @Override
    public boolean canActivate() {
        return false;
    }

    @Override
    public String getDisplayText() {
        return "Screamers of Tzeentch";
    }

    @Override
    public String getStateText() {
        return "";
    }

    @Override
    public float getBarFill() {
        float fill = 0f;
        if (charges < calcMaxCharges()) {
            fill = chargeInterval.getElapsed() / chargeInterval.getIntervalDuration();
        }

        return fill;
    }

    @Override
    public int getMaxCharges() {
        return numScreamers;
    }

    @Override
    public int getMaxDeployedDrones() {
        //return ship.hasLaunchBays() ? numScreamers : 0;
        return numScreamers;
    }

    @Override
    public boolean usesChargesOnActivate() {
        return false;
    }

    @NotNull
    @Override
    public DroneFormation getDroneFormation() {
        return getFormation();
    }

    @Override
    public @NotNull String getDroneVariant() {
        return "shpe_screamer_Daemon_wing";
    }

    @NotNull
    @Override
    public PIDController getPIDController() {
            return new PIDController(20f, 4f, 12f, 0.5f);
    }
}
