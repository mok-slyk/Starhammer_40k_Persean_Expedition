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
        for (Map.Entry<DamagingProjectileAPI, UrsusClawEffect.UrsusClawProjectileData> entry: projectiles.entrySet()) {
            DamagingProjectileAPI proj = entry.getKey();
            if(proj.getLocation() == null || proj.isExpired()){
                projectiles.remove(proj);
                continue;
            }
            UrsusClawEffect.UrsusClawProjectileData data = entry.getValue();
            renderCable(data.weapon.getFirePoint(data.barrel), proj.getLocation());
        }
        for (UrsusClawEffect.UrsusClawHitData hit: hits) {
            renderCable(hit.weapon.getFirePoint(hit.barrel), Vector2f.add(hit.target.getLocation(), hit.offset, null));
            //do movement:
            Vector2f forceDirection = Vector2f.sub(hit.weapon.getFirePoint(hit.barrel), hit.target.getLocation(), null).normalise(null);
            CombatUtils.applyForce(hit.target, forceDirection, 500f*amount);
            float torque = (float) Math.abs(Math.toRadians(VectorUtils.getFacing(forceDirection)-VectorUtils.getFacing(hit.offset)))*100*amount;
            hit.target.setAngularVelocity(hit.target.getAngularVelocity()+torque/hit.target.getMass());
        }
    }

    private void renderCable(Vector2f start, Vector2f end) {
        SpriteAPI sprite = Global.getSettings().getSprite("fx", "ursus_cable");
        Vector2f dist = Vector2f.sub(end, start, null);
        float length = dist.length();
        Vector2f scale = new Vector2f(6, length);
        MagicRender.singleframe(sprite, Vector2f.add(start, (Vector2f) dist.scale(0.5f), null), scale, VectorUtils.getFacing(dist)-90, new Color(255, 255, 255), false);
    }

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {}
    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {}
    @Override
    public void renderInUICoords(ViewportAPI viewport) {}
}
