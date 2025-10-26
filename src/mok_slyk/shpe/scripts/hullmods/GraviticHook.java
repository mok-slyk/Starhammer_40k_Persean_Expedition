package mok_slyk.shpe.scripts.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetMember;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraviticHook extends BaseHullMod {
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

        int hooks = getGraviticHookCapacity(ship.getVariant());

        @SuppressWarnings("unchecked")
        Map<String, String> matches = (Map<String, String>) Global.getSector().getPlayerMemoryWithoutUpdate().get(GraviticInterlock.KEY);

        if (hooks == 1) {
            tooltip.addPara("This ship is equipped with a Gravitic Hook that allows it to transport a ship equipped with a Gravitic Interlock, "
                            + "completely nullifying its fuel usage and reducing its monthly supply consumption by 50%%.",
                    opad, h, "Gravitic Hook", "Gravitic Interlock", "completely nullifying its fuel usage", "50%");
        } else {
            tooltip.addPara("This ship is equipped with "+hooks+" Gravitic Hooks that allow it to transport ships equipped with a Gravitic Interlock, "
                            + "completely nullifying their fuel usage and reducing their monthly supply consumption by 50%%.",
                    opad, h, hooks+" Gravitic Hooks", "Gravitic Interlock", "completely nullifying their fuel usage", "50%");
        }

        if (ship.getVariant().hasHullMod("shpe_enclosed_hook") || ship.getVariant().hasHullMod("shpe_warden_hook")) {
            tooltip.addPara("The gravitic hook on this ship is purpose-built to transport Warden Gunships and can't transport other ships",
                    opad, h, "Warden Gunships");
        }

        if (ship.getVariant().hasHullMod("shpe_enclosed_hook")) {
            tooltip.addPara("The enclosed nature of this ship's gravitic hook system completely shields transported ships from solar coronae and similar hazards and reduces their sensor profile to 0.",
                    opad, h, "0");
        }

        if(matches == null || ship.getFleetMember() == null) return;

        tooltip.addSectionHeading("Transported Ships", Alignment.MID, opad);

        if (matches.containsValue(ship.getFleetMember().getId())) {
            List<FleetMemberAPI> carried = new ArrayList<>();
            for (Map.Entry<String, String> entry : matches.entrySet()) {
                if (Objects.equals(entry.getValue(), ship.getFleetMember().getId())) {
                    FleetMemberAPI carriedMember = GraviticInterlock.getMemberInPlayerFleetByID(entry.getKey());
                    if (carriedMember == null) continue;
                    carried.add(carriedMember);
                }
            }
            tooltip.addPara("This ship is currently transporting the following ships:", opad);
            for (FleetMemberAPI member : carried) {
                tooltip.addPara(member.getShipName() + " (" + member.getVariant().getFullDesignationWithHullNameForShip() + ")", opad, h, member.getShipName() + " (" + member.getVariant().getFullDesignationWithHullNameForShip() + ")");
            }
        } else {
            tooltip.addPara("This ship is not currently transporting any ship.", opad);
        }
    }

    public static int getGraviticHookCapacity(ShipVariantAPI ship) {
        int hooks = switch(ship.getHullSpec().getBaseHullId()) {
            case "shpe_custodian":
                yield 3;
            case "shpe_emissary":
                yield 2;
            default:
                yield 1;
        };
        return hooks;
    }
}
