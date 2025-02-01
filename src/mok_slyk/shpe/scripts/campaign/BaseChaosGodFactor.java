package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;

import java.awt.*;

public class BaseChaosGodFactor extends BaseEventFactor {
    protected int godIndex = -1;
    protected boolean undivided = true;
    @Override
    public Color getProgressColor(BaseEventIntel intel) {
        if (undivided) return ChaosGodsEventIntel.UNDIVIDED_COLOR;
        return GodEventIntel.GOD_COLORS[godIndex];
    }

    public BaseChaosGodFactor(){}

    public BaseChaosGodFactor(int godIndex) {
        this.godIndex = godIndex;
        this.undivided = false;
    }
}
