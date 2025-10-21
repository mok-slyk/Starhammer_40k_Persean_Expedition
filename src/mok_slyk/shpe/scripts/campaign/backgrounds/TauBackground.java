package mok_slyk.shpe.scripts.campaign.backgrounds;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import exerelin.campaign.backgrounds.BaseCharacterBackground;
import exerelin.utilities.NexFactionConfig;

import java.util.Objects;

public class TauBackground extends BaseCharacterBackground {
    public static String KEY = "$shpe_is_tau";
    @Override
    public boolean canBeSelected(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        String id = factionSpec.getId();
        return (!id.equals("shpe_imperium") && !id.equals("shpe_mechanicus") && !id.equals("shpe_chaos"));
    }

    @Override
    public void canNotBeSelectedReason(TooltipMakerAPI tooltip, FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        tooltip.addPara("Can't be a member of a faction that has experience with the T'au", 10f);
    }

    @Override
    public void onNewGameAfterTimePass(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, true);
    }
}
