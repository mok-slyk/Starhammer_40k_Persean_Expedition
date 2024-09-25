package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.util.*;


public class UrsusClawEffect implements OnFireEffectPlugin, OnHitEffectPlugin, EveryFrameWeaponEffectPlugin {
    Logger log = Global.getLogger(this.getClass());
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        ensurePlugin();
        Objects.requireNonNull(UrsusClawCablePlugin.getPlugin()).projectiles.put(projectile, new UrsusClawProjectileData(this, weapon, 1));
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        ensurePlugin();
        UrsusClawProjectileData data = Objects.requireNonNull(UrsusClawCablePlugin.getPlugin()).projectiles.get(projectile);
        UrsusClawCablePlugin.getPlugin().projectiles.remove(projectile);
        if (data == null) {
            log.warn("data is null");
            return;
        }
        Vector2f offset = Vector2f.sub(point, target.getLocation(), null);
        if (!shieldHit && (target instanceof CombatAsteroidAPI || target instanceof ShipAPI)) {
            UrsusClawCablePlugin.getPlugin().hits.add(new UrsusClawHitData(data.weapon, data.barrel, target, offset));
        }
    }

    private void ensurePlugin() {
        if (UrsusClawCablePlugin.getPlugin() == null) {
            UrsusClawCablePlugin plugin = new UrsusClawCablePlugin();
            Global.getCombatEngine().addPlugin(plugin);
            Global.getCombatEngine().getCustomData().put(UrsusClawCablePlugin.PLUGIN_KEY, plugin);
        }
    }

    public static class UrsusClawProjectileData {
        final public UrsusClawEffect effect;
        final public WeaponAPI weapon;
        final public int barrel;

        public UrsusClawProjectileData(UrsusClawEffect effect, WeaponAPI weapon, int barrel){
            this.effect = effect;
            this.weapon = weapon;
            this.barrel = barrel;
        }
    }

    public static class UrsusClawHitData {
        public final WeaponAPI weapon;
        public final int barrel;
        public final CombatEntityAPI target;
        public final Vector2f offset;

        public UrsusClawHitData(WeaponAPI weapon, int barrel, CombatEntityAPI target, Vector2f offset) {
            this.weapon = weapon;
            this.barrel = barrel;
            this.target = target;
            this.offset = offset;
        }
    }
}
