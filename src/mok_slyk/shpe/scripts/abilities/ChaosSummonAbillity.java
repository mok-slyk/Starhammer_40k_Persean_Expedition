package mok_slyk.shpe.scripts.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.campaign.CustomCampaignEntity;

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
}
