package mok_slyk.shpe.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.plugins.MagicTrailPlugin;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.*;
import java.util.List;

public class UrsusClawCablePlugin extends BaseEveryFrameCombatPlugin {
    public Map<DamagingProjectileAPI, UrsusClawEffect.UrsusClawProjectileData> projectiles = new HashMap<>();
    public Set<UrsusClawEffect.UrsusClawHitData> hits = new HashSet<>();
    public static final String PLUGIN_KEY = "shpe_ursus_plugin";

    private List<UrsusClawEffect.UrsusClawHitData> hitToDelete = new ArrayList<>();
    public void addProjectile(DamagingProjectileAPI proj, UrsusClawEffect.UrsusClawProjectileData data) {
        UrsusClawEffect.UrsusClawHitData oldHit = getWeaponHitForBarrel(data.weapon, data.barrel);
        if (oldHit != null) {
            despawnHitCable(oldHit);
        }
        projectiles.put(proj, data);
    }

    private UrsusClawEffect.UrsusClawHitData getWeaponHitForBarrel(WeaponAPI weapon, int barrel) {
        for (UrsusClawEffect.UrsusClawHitData data: hits) {
            if (data.weapon == weapon && data.barrel == barrel) return data;
        }
        return null;
    }

    public static UrsusClawCablePlugin getPlugin() {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null) {
            return null;
        }
        return (UrsusClawCablePlugin) engine.getCustomData().get(PLUGIN_KEY);
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        // do visuals:
        for (Iterator<Map.Entry<DamagingProjectileAPI, UrsusClawEffect.UrsusClawProjectileData>> iterator = projectiles.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<DamagingProjectileAPI, UrsusClawEffect.UrsusClawProjectileData> entry = iterator.next();
            DamagingProjectileAPI proj = entry.getKey();
            if (proj.getLocation() == null || proj.isExpired()) {
                despawnProjectileCable(proj);
                iterator.remove();
            }
            UrsusClawEffect.UrsusClawProjectileData data = entry.getValue();
            renderCable(data.weapon.getFirePoint(data.barrel), proj.getLocation());
        }
        for (Iterator<UrsusClawEffect.UrsusClawHitData> iterator = hits.iterator(); iterator.hasNext(); ) {
            UrsusClawEffect.UrsusClawHitData hit = iterator.next();
            renderCable(hit.weapon.getFirePoint(hit.barrel), hit.getPosition());
            //do movement:
            Vector2f forceVector = Vector2f.sub(hit.weapon.getFirePoint(hit.barrel), hit.target.getLocation(), null);
            float length = forceVector.length();
            float strength = Math.min(1500, length);
            Vector2f forceDirection = forceVector.normalise(null);
            CombatUtils.applyForce(hit.target, forceDirection, strength * amount);
            float torque = (float) Math.abs(Math.toRadians(VectorUtils.getFacing(forceDirection) - VectorUtils.getFacing(hit.getGlobalOffset()))) * strength * amount;
            hit.target.setAngularVelocity(hit.target.getAngularVelocity() + torque / hit.target.getMass());

            if (shouldHitCableBreak(hit)) {
                despawnHitCable(hit);
                //iterator.remove();
            }
        }
        for (UrsusClawEffect.UrsusClawHitData hit: hitToDelete) {
            hits.remove(hit);
        }
        hitToDelete.clear();
    }

    private boolean shouldHitCableBreak(UrsusClawEffect.UrsusClawHitData hit) {
        Vector2f forceVector = Vector2f.sub(hit.weapon.getFirePoint(hit.barrel), hit.getPosition(), null);
        float length = forceVector.length();
        if (length > hit.weapon.getRange()*1.2f) return true;
        if (length < 100) return true;
        if (hit.target instanceof ShipAPI && !((ShipAPI) hit.target).isAlive()) return true;
        return false;
    }

    private void renderCable(Vector2f start, Vector2f end) {
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "ursus_cable");
        Vector2f dist = Vector2f.sub(end, start, null);
        float length = dist.length();
        Vector2f scale = new Vector2f(6, length);
        MagicRender.singleframe(sprite, Vector2f.add(start, (Vector2f) dist.scale(0.5f), null), scale, VectorUtils.getFacing(dist)-90, new Color(255, 255, 255), false);
    }
    private void despawnProjectileCable(DamagingProjectileAPI proj){
        UrsusClawEffect.UrsusClawProjectileData data = projectiles.get(proj);

        Vector2f start = data.weapon.getFirePoint(data.barrel);
        Vector2f end = proj.getLocation();
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "ursus_cable");
        Vector2f dist = Vector2f.sub(end, start, null);
        float length = dist.length();
        Vector2f scale = new Vector2f(6, length);
        MagicRender.battlespace(sprite, Vector2f.add(start, (Vector2f) dist.scale(0.5f), null), proj.getVelocity(), scale, new Vector2f(), VectorUtils.getFacing(dist)-90, 0,
                new Color(255, 255, 255), false, 0,0, 1
        );
        //projectiles.remove(proj);
    }

    protected void despawnHitCable(UrsusClawEffect.UrsusClawHitData data) {
        Vector2f start = data.weapon.getFirePoint(data.barrel);
        Vector2f end = data.getPosition();
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "ursus_cable");
        Vector2f dist = Vector2f.sub(end, start, null);
        float length = dist.length();
        Vector2f scale = new Vector2f(6, length);
        MagicRender.battlespace(sprite, Vector2f.add(start, (Vector2f) dist.scale(0.5f), null), data.target.getVelocity(), scale, new Vector2f(), VectorUtils.getFacing(dist)-90, 0,
                new Color(255, 255, 255), false, 0,0, 1
        );
        //hits.remove(data);
        hitToDelete.add(data);
    }

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {}
    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {}
    @Override
    public void renderInUICoords(ViewportAPI viewport) {}
}
