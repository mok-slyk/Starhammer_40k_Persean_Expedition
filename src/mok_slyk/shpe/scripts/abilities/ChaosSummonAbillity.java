package mok_slyk.shpe.scripts.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.CustomCampaignEntity;
import mok_slyk.shpe.scripts.campaign.ChaosGodsEventIntel;

import java.awt.*;

public class ChaosSummonAbillity extends BaseDurationAbility {
    @Override
    protected void activateImpl() {
        if (entity.isPlayerFleet()) {
            CampaignFleetAPI fleet = getFleet();
            Global.getSector().getCampaignUI().showInteractionDialog(new ChaosSummonDialogPluginImpl(), null);
        }
    }

    @Override
    protected void applyEffect(float amount, float level) {

    }

    @Override
    protected void deactivateImpl() {
        cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {

    }

    @Override
    public boolean isUsable() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return false;
        if (!fleet.isInHyperspace()) return false;
        return super.isUsable();
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {

        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return;

        Color gray = Misc.getGrayColor();
        Color highlight = ChaosGodsEventIntel.UNDIVIDED_COLOR;
        Color bad = Misc.getNegativeHighlightColor();

        LabelAPI title = tooltip.addTitle(spec.getName());

        float pad = 10f;

        tooltip.addPara("Call upon the dark powers of chaos to create demonic weapons, wings and ships that you have unlocked via the Touch of Chaos.", pad, highlight, "Touch of Chaos");

        tooltip.addPara("A human sacrifice will be required.", pad, highlight, "human sacrifice");

        tooltip.addPara("Creating weapons, wings and ships in this way requires raw materials and further blood sacrifices.", pad);

        if (!fleet.isInHyperspace()) {
            tooltip.addPara("Can only be used in hyperspace.", bad, pad);
        }
    }

    public boolean hasTooltip() {
        return true;
    }
}
