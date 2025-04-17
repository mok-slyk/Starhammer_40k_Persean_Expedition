package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaUtil.campaign.SectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class HyperspaceGodFleetManager extends PlayerVisibleFleetManager {

    public static final float MAX_RANGE_FROM_PLAYER_LY = RouteManager.SPAWN_DIST_LY;
    public static final float DESPAWN_RANGE_LY = MAX_RANGE_FROM_PLAYER_LY + 1.4f;

    protected StarSystemAPI currentSystem;

    @Override
    protected boolean isOkToDespawnAssumingNotPlayerVisible(CampaignFleetAPI fleet) {
        if (currentSystem == null) return true;
        StarSystemAPI system = fleet.getStarSystem();
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        float playerFP = player.getFleetPoints();

        if (system == null || !system.getName().equals(currentSystem.getName())) return true;

        return false;
    }

    @Override
    protected float getSpawnRateMult() {
        return 2;
    }

    @Override
    protected int getMaxFleets() {
        return (int) Math.min(2, Math.floor(Math.max(PirateBaseManager.getInstance().getDaysSinceStart()-180, 0)/90f));
    }

    protected StarSystemAPI pickSystem() {
        if (Global.getSector().isInNewGameAdvance()) return null;
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) return null;

        StarSystemAPI playerSystem = player.getStarSystem();
        if (playerSystem != null) return playerSystem;

        StarSystemAPI nearest = null;
        float minDist = Float.MAX_VALUE;

        for (StarSystemAPI system: Global.getSector().getStarSystems()) {
            if (system.hasTag(Tags.SYSTEM_ABYSSAL)) continue;
            float distToPlayerLY = Misc.getDistanceLY(player.getLocationInHyperspace(), system.getLocation());

            if (distToPlayerLY < minDist) {
                if (system.getStar() != null) {
                    if (system.getStar().getSpec().isPulsar()) continue;
                }

                nearest = system;
                minDist = distToPlayerLY;
            }
        }

        if (nearest == null && currentSystem != null) {
            float distToPlayerLY = Misc.getDistanceLY(player.getLocationInHyperspace(), currentSystem.getLocation());
            if (distToPlayerLY <= DESPAWN_RANGE_LY) {
                nearest = currentSystem;
            }
        }

        currentSystem = nearest;
        return nearest;
    }

    protected String pickFaction() {
        float ran = (float) Math.random();
        if (ran <= 0.25f) return "shpe_khorne";
        if (ran > 0.25f && ran <= 0.5f) return "shpe_nurgle";
        if (ran > 0.5f && ran <= 0.75f) return "shpe_tzeentch";
        return "shpe_slaanesh";
    }

    @Override
    protected CampaignFleetAPI spawnFleet() {
        StarSystemAPI system = pickSystem();
        if (system == null) return null;

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) return null;

        String fleetType = FleetTypes.PATROL_MEDIUM;

        float combat = 1;
        for (int i = 0; i < 6; i++) {
            if ((float) Math.random() < 0.3f) {
                break;
            }
            combat+=2;
        }
        combat+= Math.random();

        combat *= 15f;

        FleetParamsV3 params = new FleetParamsV3(
                null, // source market
                system.getLocation(),
                pickFaction(),
                null,
                fleetType,
                combat, // combatPts
                0, // freighterPts
                0, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                0f // qualityMod
        );
        params.ignoreMarketFleetSizeMult = true;

        //params.random = random;
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        BaseLimitedFleetManager.log.info("tried to spawn");
        if (fleet == null || fleet.isEmpty()) return null;
        BaseLimitedFleetManager.log.info("spawned?");

        // setting the below means: transponder off and more "go dark" use when traveling
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);

        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);

        Vector2f loc = Misc.pickHyperLocationNotNearPlayer(system.getLocation(), Global.getSettings().getMaxSensorRangeHyper() - 100f);
        Global.getSector().getHyperspace().addEntity(fleet);
        fleet.setLocation(loc.x, loc.y);
        fleet.addAssignment(FleetAssignment.RAID_SYSTEM, system.getHyperspaceAnchor(), 100000, "Haunting the Warp");

        return fleet;
    }
}
