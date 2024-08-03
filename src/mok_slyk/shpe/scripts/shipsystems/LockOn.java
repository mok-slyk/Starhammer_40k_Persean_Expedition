package mok_slyk.shpe.scripts.shipsystems;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;

public class LockOn extends BaseShipSystemScript {
    public static final float DAMAGE_BONUS_PERCENT = 50f;
    public static final float RANGE_BONUS_PERCENT = 50f;
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        float damageBonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
        float rangeBonusPercent = RANGE_BONUS_PERCENT * effectLevel;
        stats.getEnergyWeaponDamageMult().modifyPercent(id, damageBonusPercent);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, rangeBonusPercent);

    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponDamageMult().unmodify(id);
    }
}
