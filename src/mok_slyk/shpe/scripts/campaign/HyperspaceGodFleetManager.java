package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.util.Misc;

public class HyperspaceGodFleetManager extends PlayerVisibleFleetManager {

    @Override
    protected boolean isOkToDespawnAssumingNotPlayerVisible(CampaignFleetAPI fleet) {
        return false;
    }

    @Override
    protected int getMaxFleets() {
        return 5;
    }

    @Override
    protected CampaignFleetAPI spawnFleet() {
        StarSystemAPI system = currSpawnLoc;
        if (system == null) return null;

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null) return null;

        int num = Misc.getMarketsInLocation(system).size();
        if (Misc.getMarketsInLocation(system, Factions.PLAYER).size() == num && num > 0) {
            return null; // handled by HostileActivityIntel, DisposableHostileActivityFleetManager, etc
        }


        String fleetType = FleetTypes.PATROL_SMALL;


        float combat = 1;
        for (int i = 0; i < 3; i++) {
            if ((float) Math.random() > 0.5f) {
                combat++;
            }
        }

        float desired = getDesiredNumFleetsForSpawnLocation();
        if (desired > 2) {
            float timeFactor = (PirateBaseManager.getInstance().getDaysSinceStart() - 180f) / (365f * 2f);
            if (timeFactor < 0) timeFactor = 0;
            if (timeFactor > 1) timeFactor = 1;

            combat += ((desired - 2) * (0.5f + (float) Math.random() * 0.5f)) * 1f * timeFactor;
            //combat += (desired - 2) * (0.5f + (float) Math.random() * 0.5f);
        }

        combat *= 8f;

        FleetParamsV3 params = new FleetParamsV3(
                null, // source market
                system.getLocation(),
                "shpe_chaos",
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
        //fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);

        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);

        float nf = getDesiredNumFleetsForSpawnLocation();

        Global.getSector().getHyperspace().addEntity(fleet);
        fleet.addScript(new Aggr);

        return fleet;
    }
}
