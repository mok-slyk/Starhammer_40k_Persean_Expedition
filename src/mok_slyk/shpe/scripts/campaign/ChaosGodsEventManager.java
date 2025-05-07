package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;

import java.util.Objects;

import static mok_slyk.shpe.scripts.campaign.ChaosGodsEventIntel.*;

public class ChaosGodsEventManager implements FleetEventListener {
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
                intel.addFactor(new ChaosGodOneTimeFactor(2, NURGLE_I ,"Tzeentch ships destroyed", "Tzeentchian ships destroyed by your fleet."), null);
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
}
