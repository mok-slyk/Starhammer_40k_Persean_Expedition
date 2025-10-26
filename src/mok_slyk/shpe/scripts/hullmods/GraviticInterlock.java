package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.DeployedFleetMemberAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.FleetMemberDeploymentListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GraviticInterlock extends BaseHullMod {
    public static String KEY = "$shpe_glock_ref";
    private static Logger log = Global.getLogger(GraviticInterlock.class);
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (stats.getFleetMember() == null) {
            log.info("no fleet member");
            return;
        }
        FleetDataAPI fleetData = stats.getFleetMember().getFleetData();
        if (fleetData == null) {
            log.info("no fleet data");
            return;
        }
        if (Global.getSector().getPlayerFleet() != fleetData.getFleet()) return;
        Map<String, String> matches = matchHooksToInterlocks(fleetData.getMembersInPriorityOrder());
        Global.getSector().getPlayerMemoryWithoutUpdate().set(KEY, matches);
        if (matches.containsKey(stats.getFleetMember().getId())) {
            log.info("match found");
            stats.getSuppliesPerMonth().modifyMult(id, 0.5f);
            stats.getFuelUseMod().modifyMult(id, 0f);
            FleetMemberAPI carrier = getMemberInPlayerFleetByID(matches.get(stats.getFleetMember().getId()));
            if (carrier != null && carrier.getVariant().hasHullMod("shpe_enclosed_hook")) {
                stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, 0);
                stats.getSensorProfile().modifyMult(id,0);
            } else {
                stats.getSensorProfile().modifyMult(id,0.5f);
            }
        } else {
            log.info("no match found");
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        //if (index == 0) return "" + (int)RANGE_PENALTY_PERCENT + "%";
        return null;
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color t = Misc.getTextColor();
        Color g = Misc.getGrayColor();

        @SuppressWarnings("unchecked")
        Map<String, String> matches = (Map<String, String>) Global.getSector().getPlayerMemoryWithoutUpdate().get(KEY);

        tooltip.addPara("This ship is not optimized for independent FTL travel. Instead, it has the necessary interlocks to be transported by a ship equipped with a Gravitic Hook, "
                + "completely nullifying its fuel usage and reducing its monthly supply consumption by 50%%.",
                opad, h, "Gravitic Hook", "completely nullifying its fuel usage", "50%");

        if (ship.getVariant().hasHullMod("shpe_warden_interlock")) {
            tooltip.addPara("This ship is small enough to be further protected by a ship with an Enclosed Gravitic Hook", opad, h, "Enclosed Gravitic Hook");
        }

        if(matches == null || ship.getFleetMember() == null) return;

        tooltip.addSectionHeading("Transporting Ship", Alignment.MID, opad);

        if (matches.containsKey(ship.getFleetMember().getId())) {
            FleetMemberAPI carrier = getMemberInPlayerFleetByID(matches.get(ship.getFleetMember().getId()));
            if (carrier == null) return;

            tooltip.addPara("This ship is being transported by the " + carrier.getShipName() + " (" + carrier.getVariant().getFullDesignationWithHullNameForShip() + ")",
                    opad, h, carrier.getShipName() + " (" + carrier.getVariant().getFullDesignationWithHullNameForShip() + ")");

            tooltip.addSectionHeading("Current Effects", Alignment.MID, opad);

            tooltip.addPara("Reduces fuel usage to 0.", opad, h, "0");
            tooltip.addPara("Decreases monthly supply usage by 50%%.", opad, h, "50%");

            if (carrier.getVariant().hasHullMod("shpe_enclosed_hook")) {
                tooltip.addPara("Decreases the effect of solar coronae, hyperspace storms and similar hazards by 100%%.", opad, h, "100%");
                tooltip.addPara("Decreases sensor profile to 0.", opad, h, "0");
            } else {
                tooltip.addPara("Decreases sensor profile by 50%%.", opad, h, "50%");
            }
        } else {
            tooltip.addPara("This ship is not being transported by any ship with Gravitic Hooks.",
                    opad, bad, "not being transported by any ship with Gravitic Hooks");
        }
    }

    public static Map<String, String> matchHooksToInterlocks(List<FleetMemberAPI> members) {
        List<FleetMemberAPI> hookMembers = new ArrayList<>();
        List<FleetMemberAPI> interlockMembers = new ArrayList<>();
        Map<String, String> matches = new LinkedHashMap<>();
        Map<FleetMemberAPI, Integer> usedHooks = new HashMap<>();

        for (FleetMemberAPI member: members) {
            if (member.getVariant().hasHullMod("shpe_gravitic_hook") || member.getVariant().hasHullMod("shpe_warden_hook")  || member.getVariant().hasHullMod("shpe_enclosed_hook")) {
                hookMembers.add(member);
            } else if (member.getVariant().hasHullMod("shpe_gravitic_interlock") || member.getVariant().hasHullMod("shpe_warden_interlock")) {
                interlockMembers.add(member);
            }
        }

        // Sort interlocks so wardens have lower priority
        interlockMembers.sort(Comparator.comparing(m -> !isWarden(m)));
        hookMembers.sort(Comparator.comparing(m -> !m.getVariant().hasHullMod("shpe_enclosed_hook")));

        // Try to match each interlock
        for (FleetMemberAPI interlock : interlockMembers) {
            for (FleetMemberAPI hook : hookMembers) {
                if (usedHooks.get(hook) != null && usedHooks.get(hook) >= GraviticHook.getGraviticHookCapacity(hook.getVariant())) continue;

                boolean compatible = false;

                if (isWarden(interlock))
                    compatible = true;
                else
                    compatible = !onlySupportsWardens(hook);

                if (compatible) {
                    matches.put(interlock.getId(), hook.getId());
                    usedHooks.put(hook, (usedHooks.get(hook) == null ? 1 : usedHooks.get(hook)+1));
                    break; // move to next interlock
                }
            }
        }
        return matches;
    }

    public static boolean isWarden(FleetMemberAPI member) {
        return member.getVariant().hasHullMod("shpe_warden_interlock");
    }

    public static boolean onlySupportsWardens(FleetMemberAPI member) {
        return member.getVariant().hasHullMod("shpe_warden_hook")  || member.getVariant().hasHullMod("shpe_enclosed_hook");
    }

    public static FleetMemberAPI getMemberInPlayerFleetByID(String id) {
        for (FleetMemberAPI member:Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if (Objects.equals(member.getId(), id)) return member;
        }
        return null;
    }
}

