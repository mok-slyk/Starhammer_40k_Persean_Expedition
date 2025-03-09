package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.fleets.BaseLimitedFleetManager;
import com.fs.starfarer.api.impl.campaign.fleets.DisposableFleetManager;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.bases.LuddicPathCells;
import com.fs.starfarer.api.impl.campaign.intel.bases.PirateBaseManager;
import com.fs.starfarer.api.util.Misc;

/**
 * Adds the following types of fleets:
 * 1) Occasional pather fleet in hyper around a populated system
 * 2) A few more in and around systems with pather/church presence
 */
public class DisposableChaosFleetManager extends DisposableFleetManager {

	protected Object readResolve() {
		super.readResolve();
		return this;
	}
	
	@Override
	protected String getSpawnId() {
		return "shpe_chaos_disp"; // not a faction id, just an identifier for this spawner
	}
	
	@Override
	protected int getDesiredNumFleetsForSpawnLocation() {
		MarketAPI pather = getLargestMarket(Factions.LUDDIC_PATH);
		MarketAPI church = getLargestMarket(Factions.LUDDIC_CHURCH);
		
		float desiredNumFleets = 1f;
		
		if (church != null) {
			desiredNumFleets++;
		}
		if (pather != null) {
			desiredNumFleets += pather.getSize();
		}
		
		int cells = getPatherCellsLevel();
		desiredNumFleets += cells;
		
		//return (int) Math.round(desiredNumFleets);
		return 100;
	}
	
	protected int getPatherCellsLevel() {
		if (currSpawnLoc == null) return 0;
		int total = 0;
		for (MarketAPI market : Global.getSector().getEconomy().getMarkets(currSpawnLoc)) {
			if (market.isHidden()) continue;
			MarketConditionAPI mc = market.getCondition(Conditions.PATHER_CELLS);
			if (mc != null && mc.getPlugin() instanceof LuddicPathCells) {
				LuddicPathCells cells = (LuddicPathCells) mc.getPlugin();
				if (cells.getIntel() != null) {
					if (cells.getIntel().isSleeper()) {
						total++;
					} else {
						total += 2;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public float getSpawnRateMult() {
		return 1000f;
	}

	@Override
	protected int getMaxFleets() {
		return 1000;
	}

	protected MarketAPI getLargestMarket(String faction) {
		if (currSpawnLoc == null) return null;
		MarketAPI largest = null;
		int maxSize = 0;
		for (MarketAPI market : Global.getSector().getEconomy().getMarkets(currSpawnLoc)) {
			if (market.isHidden()) continue;
			if (!market.getFactionId().equals(faction)) continue;
			
			if (market.getSize() > maxSize) {
				maxSize = market.getSize();
				largest = market;
			}
		}
		return largest;
	}
	
	protected CampaignFleetAPI spawnFleetImpl() {
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
				5000, // combatPts
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
		
		if (nf == 1) {
			setLocationAndOrders(fleet, 1f, 1f);
		} else {
			setLocationAndOrders(fleet, 0.5f, 1f);
		}
		
		return fleet;
	}
	
}








