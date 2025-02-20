package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.VectorUtils;
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
        Objects.requireNonNull(UrsusClawCablePlugin.getPlugin()).addProjectile(projectile, new UrsusClawProjectileData(this, weapon, closestBarrel(projectile)));
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
        Vector2f offset = VectorUtils.rotate(Vector2f.sub(point, target.getLocation(), null), -target.getFacing(), new Vector2f());
        if (!shieldHit && (target instanceof CombatAsteroidAPI || target instanceof ShipAPI)) {
            UrsusClawCablePlugin.getPlugin().hits.add(new UrsusClawHitData(data.weapon, data.barrel, target, offset));
        }
    }

    private int closestBarrel(DamagingProjectileAPI proj){
        int closest = 0;
        float shortestSquare = Float.MAX_VALUE;
        Vector2f pos = proj.getLocation();
        int i = 0;
        while (true) {
            try {
                Vector2f point = proj.getWeapon().getFirePoint(i);
                if (point == null) break;
                float dist = Math.abs(Vector2f.sub(point, pos, null).lengthSquared());
                if (dist < shortestSquare) {
                    shortestSquare = dist;
                    closest = i;
                }
            } catch (NullPointerException | IndexOutOfBoundsException e) {break;}
            i++;
        }
        return closest;
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

        public Vector2f getPosition(){
            return Vector2f.add(target.getLocation(), getGlobalOffset(), null);
        }

        public Vector2f getGlobalOffset(){
            return VectorUtils.rotate(offset, target.getFacing(), new Vector2f());
        }
    }
}
