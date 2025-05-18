package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import mok_slyk.shpe.scripts.utils.Witchcraft;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Objects;

import static mok_slyk.shpe.scripts.campaign.ChaosGodsEventIntel.*;

public class ChaosGodsEventManager implements FleetEventListener, CampaignEventListener {
    private static Logger log = Global.getLogger(ChaosGodsEventManager.class);
    public static final int SMALL_TO_LARGE_FLEET_THRESHOLD = 100;

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {}

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (ChaosGodsEventIntel.get() != null && (ChaosGodsEventIntel.get().isEnded() || ChaosGodsEventIntel.get().isEnding())) return;

        if (!battle.isPlayerInvolved()) return;

        int totalFleetPointsDefeated = 0;
        boolean foughtImperium = false;
        boolean foughtChaos = false;
        boolean foughtKhorne = false;
        boolean foughtTzeentch = false;
        boolean foughtNurgle = false;
        boolean foughtSlaanesh = false;

        boolean bigBattle = false;
        boolean superiorFoe = false;
        if (battle.getPlayerSide().contains(primaryWinner)) {
            if (battle.getPlayerCombined().getFleetPoints()*1.1f<battle.getNonPlayerCombined().getFleetPoints()) {
                superiorFoe = true;
            }
            for (CampaignFleetAPI otherFleet : battle.getNonPlayerSideSnapshot()) {
                totalFleetPointsDefeated += otherFleet.getFleetPoints();
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_imperium")) {
                    foughtImperium = true;
                }
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_chaos")) {
                    foughtChaos = true;
                }
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_khorne")) {
                    foughtKhorne = true;
                }
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_nurgle")) {
                    foughtNurgle = true;
                }
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_tzeentch")) {
                    foughtTzeentch = true;
                }
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_slaanesh")) {
                    foughtSlaanesh = true;
                }
            }
            if (totalFleetPointsDefeated > SMALL_TO_LARGE_FLEET_THRESHOLD) {
                bigBattle = true;
            }

            ChaosGodsEventIntel intel = null;

            if (foughtChaos || foughtKhorne || foughtNurgle || foughtTzeentch || foughtSlaanesh) {
                intel = ChaosGodsEventIntel.getOrCreate(true);
            } else {
                intel = ChaosGodsEventIntel.get();
            }
            if (intel == null) {
                return;
            }

            // Gain on kill imperium ships:
            if (foughtImperium) {
                if (bigBattle) {
                    intel.addFactor(new ChaosGodOneTimeFactor(1, "Imperial ships destroyed", "Imperial ships destroyed by your fleet."), null);
                } else {
                    intel.addFactor(new ChaosGodOneTimeFactor(2, "Imperial ships destroyed", "Imperial ships destroyed by your fleet."), null);
                }
            }
            if (foughtChaos) {
                intel.addFactor(new ChaosGodOneTimeFactor(-1, "Chaos ships destroyed", "Chaos Undivided ships destroyed by your fleet."), null);
            }
            if (foughtKhorne) {
                intel.addFactor(new ChaosGodOneTimeFactor(-2, KHORNE_I ,"Khorne ships destroyed", "Khornite ships destroyed by your fleet."), null);
                intel.addFactor(new ChaosGodOneTimeFactor(2, SLAANESH_I ,"Khorne ships destroyed", "Khornite ships destroyed by your fleet."), null);
            }
            if (foughtNurgle) {
                intel.addFactor(new ChaosGodOneTimeFactor(-2, NURGLE_I ,"Nurgle ships destroyed", "Nurglite ships destroyed by your fleet."), null);
                intel.addFactor(new ChaosGodOneTimeFactor(2, TZEENTCH_I ,"Nurgle ships destroyed", "Nurglite ships destroyed by your fleet."), null);
            }
            if (foughtTzeentch) {
                intel.addFactor(new ChaosGodOneTimeFactor(-2, TZEENTCH_I ,"Tzeentch ships destroyed", "Tzeentchian ships destroyed by your fleet."), null);
                intel.addFactor(new ChaosGodOneTimeFactor(3, NURGLE_I ,"Tzeentch ships destroyed", "Tzeentchian ships destroyed by your fleet."), null);
            }
            if (foughtSlaanesh) {
                intel.addFactor(new ChaosGodOneTimeFactor(-2, SLAANESH_I ,"Slaanesh ships destroyed", "Slaaneshi ships destroyed by your fleet."), null);
                intel.addFactor(new ChaosGodOneTimeFactor(2, KHORNE_I ,"Slaanesh ships destroyed", "Slaaneshi ships destroyed by your fleet."), null);
            }
            if (superiorFoe) {
                intel.addFactor(new ChaosGodOneTimeFactor(1, KHORNE_I, "Powerful foe defeated", "Superior fleet defeated by your fleet."), null);
            }
        }
    }

    @Override
    public void reportPlayerOpenedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {

    }

    @Override
    public void reportPlayerOpenedMarketAndCargoUpdated(MarketAPI market) {

    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {

    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        ChaosGodsEventIntel intel = ChaosGodsEventIntel.get();
        if (intel == null || intel.isEnded() || intel.isEnding()) return;
        float drugs = transaction.getQuantityBought(Commodities.DRUGS);
        int drugPoints = (int) (drugs / 100);
        if (drugPoints > 0) {
            intel.addFactor(new ChaosGodOneTimeFactor(drugPoints, SLAANESH_I, "Drugs purchased", "Recreational Drugs purchased by your fleet."), null);
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public void reportBattleFinished(CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {

    }

    @Override
    public void reportFleetDespawned(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {

    }

    @Override
    public void reportFleetSpawned(CampaignFleetAPI fleet) {

    }

    @Override
    public void reportFleetReachedEntity(CampaignFleetAPI fleet, SectorEntityToken entity) {

    }

    @Override
    public void reportFleetJumped(CampaignFleetAPI fleet, SectorEntityToken from, JumpPointAPI.JumpDestination to) {

    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {

    }

    @Override
    public void reportPlayerReputationChange(String faction, float delta) {

    }

    @Override
    public void reportPlayerReputationChange(PersonAPI person, float delta) {

    }

    @Override
    public void reportPlayerActivatedAbility(AbilityPlugin ability, Object param) {

    }

    @Override
    public void reportPlayerDeactivatedAbility(AbilityPlugin ability, Object param) {

    }

    @Override
    public void reportPlayerDumpedCargo(CargoAPI cargo) {

    }

    @Override
    public void reportPlayerDidNotTakeCargo(CargoAPI cargo) {

    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        int sizeSum = 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.isHidden()) continue;
            if (!market.getFactionId().equals(Global.getSector().getPlayerFaction().getId())) continue;
            sizeSum += market.getSize();
        }
        if (sizeSum >= 9) {
            addOrUpdateColonySizeFactor(1);
            log.info("added cs facto");
        }
    }

    protected void addOrUpdateColonySizeFactor(int points) {
        ChaosGodsEventIntel intel = ChaosGodsEventIntel.get();
        if (intel == null || intel.isEnded() || intel.isEnding()) return;

        ColonySizeFactor sizeFactor = null;
        for (EventFactor factor : intel.getFactors()) {
            if (factor instanceof ColonySizeFactor) {
                sizeFactor = (ColonySizeFactor) factor;
                break;
            }
        }

        if (sizeFactor == null) {
            intel.addFactor(new ColonySizeFactor(points), null);
            return;
        }

        sizeFactor.points = points;
    }
}
