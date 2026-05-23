package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.*;

public class LanceBeamPlugin extends BaseEveryFrameCombatPlugin {
    public static final String KEY = "shpe_lancebeam_plugin";
    CombatEngineAPI engine;
    LanceBeamRenderer renderer;
    Map<CombatEngineLayers, List<LanceBeamObject>> layerMap = new HashMap<>();
    @Override
    public void init(CombatEngineAPI engine) {
        renderer = new LanceBeamRenderer(this);
        engine.addLayeredRenderingPlugin(renderer);

        engine.getCustomData().put(KEY, this);

        this.engine = engine;
    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        if (engine == null || engine.isPaused()) return;

        // cull expired beams and advance other beams
        for (Map.Entry<CombatEngineLayers, List<LanceBeamObject>> entry : layerMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                for (Iterator<LanceBeamObject> iter = entry.getValue().iterator(); iter.hasNext(); ) {
                    LanceBeamObject beam = iter.next();
                    if (beam.age > beam.fadeInTime + beam.damageWindowTime + beam.fadeOutTime) {
                        iter.remove();
                    } else {
                        beam.advance(amount, engine);
                    }
                }
            }
        }

    }

    public static LanceBeamPlugin getPlugin() {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null) {
            return null;
        }
        return (LanceBeamPlugin) engine.getCustomData().get(KEY);
    }

    public void addBeam(LanceBeamObject beam, CombatEngineLayers layer) {
        layerMap.computeIfAbsent(layer, k -> new ArrayList<>()).add(beam);
    }


}

class LanceBeamRenderer extends BaseCombatLayeredRenderingPlugin {
    private LanceBeamPlugin parent;

    //No render distance limit!
    @Override
    public float getRenderRadius() {
        return 999999999999999999999f;
    }
    protected LanceBeamRenderer(LanceBeamPlugin parent) {
        this.parent = parent;
    }
    @Override
    public void render(CombatEngineLayers layer, ViewportAPI viewport) {
        List<LanceBeamObject> beams = parent.layerMap.get(layer);
        if (beams != null) {
            for (LanceBeamObject beam: beams) {
                beam.render();
            }
        }
    }
}
