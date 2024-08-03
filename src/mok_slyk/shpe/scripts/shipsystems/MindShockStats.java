package mok_slyk.shpe.scripts.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.magiclib.util.MagicRender;

public class MindShockStats extends BaseShipSystemScript {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        super.apply(stats, id, state, effectLevel);
        //MagicRender
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        super.unapply(stats, id);
    }
}
