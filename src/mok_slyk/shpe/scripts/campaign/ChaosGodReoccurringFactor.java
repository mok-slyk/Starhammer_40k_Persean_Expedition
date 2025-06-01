package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ChaosGodReoccurringFactor extends BaseChaosGodFactor{

    public static float DURATION_DAYS = 30f;
    public int points;
    protected long timestamp;

    protected String bulletPointText = "";
    protected String mainRowToolTip = "";

    public ChaosGodReoccurringFactor(int points, int godIndex, String bulletPointText, String mainRowToolTip) {
        super(godIndex);
        this.points = points;
        timestamp = Global.getSector().getClock().getTimestamp();
        this.bulletPointText = bulletPointText;
        this.mainRowToolTip = mainRowToolTip;
    }

    @Override
    public boolean isOneTime() {
        return false;
    }

    @Override
    public int getProgress(BaseEventIntel intel) {
        return points;
    }

    public boolean hasOtherFactorsOfClass(BaseEventIntel intel, Class c) {
        for (EventFactor factor : intel.getFactors()) {
            if (factor != this && c.isInstance(factor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getProgressStr(BaseEventIntel intel) {
        if (getProgress(intel) == 0) {
            return "";
        }
        return super.getProgressStr(intel);
    }

    @Override
    public Color getDescColor(BaseEventIntel intel) {
        if (getProgress(intel) == 0) {
            return Misc.getGrayColor();
        }
        return super.getDescColor(intel);
    }

    @Override
    public boolean isExpired() {
        return timestamp != 0 && DURATION_DAYS != 0f && Global.getSector().getClock().getElapsedDaysSince(timestamp) > DURATION_DAYS;
    }

    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        return super.shouldShow(intel);
    }

    protected String getBulletPointText(BaseEventIntel intel) {
        return bulletPointText;
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        return bulletPointText;
    }

    @Override
    public void addBulletPointForOneTimeFactor(BaseEventIntel intel, TooltipMakerAPI info, Color tc, float initPad) {
        String text = getBulletPointText(intel);
        if (text == null) text = getDesc(intel);
        if (text != null) {
            info.addPara(text + ": %s", initPad, tc, getProgressColor(intel),
                    getProgressStr(intel));
        }
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(mainRowToolTip,0f);
            }

        };
    }
}
