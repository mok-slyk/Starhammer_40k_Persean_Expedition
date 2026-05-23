package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class LanceBeamSpawner {
    private static Logger log = Global.getLogger(LanceBeamSpawner.class);

    public static LanceBeamObject spawnSimpleLanceBeam(CombatEngineAPI engine,
                                            Vector2f start,
                                            float angle,
                                            float range,
                                            float intro,
                                            float full ,
                                            float fade,
                                            SpriteAPI coreSprite,
                                            Color coreColorStart,
                                            Color coreColorMain,
                                            Color coreColorEnd,
                                            float coreWidthStart,
                                            float coreWidthMain,
                                            float coreWidthEnd,
                                            float damage,
                                            DamageType damageType,
                                            float emp,
                                            WeaponAPI weapon,
                                            int barrel,
                                            float breakOff
    ) {
        LanceBeamPlugin plugin = LanceBeamPlugin.getPlugin();
        LanceBeamLayer layer1 = new LanceBeamLayer();
        layer1.sprite = coreSprite;
        layer1.blendModeSRC = GL_SRC_ALPHA;
        layer1.blendModeDEST = GL_ONE;
        layer1.scrollSpeed = 1;

        LanceBeamObject beam = new LanceBeamObject();
        beam.pos = start;
        beam.angle = angle;
        beam.range = range;
        beam.extensionTime = 0.2f;
        beam.fadeInTime = intro;
        beam.damageWindowTime = full;
        beam.fadeOutTime = fade;
        beam.nonEmpDamage = damage;
        beam.empDamage = emp;
        beam.damageType = damageType;
        beam.weapon = weapon;
        beam.barrelIndex = barrel;
        beam.breakOffTime = breakOff;

        LanceBeamStage stage1 = new LanceBeamStage();
        stage1.colorFadeIn = intro;
        stage1.colorFadeMain = full;
        stage1.colorFadeOut = fade;
        stage1.startColor = coreColorStart;
        stage1.mainColor = coreColorMain;
        stage1.endColor = coreColorEnd;
        stage1.widthStart = coreWidthStart * 0.1f;
        stage1.widthMain = coreWidthMain * 0.1f;
        stage1.widthEnd = coreWidthEnd * 0.1f;
        stage1.opacityFadeIn = intro;
        stage1.opacityFull = full;
        stage1.opacityFadeOut = fade;
        stage1.opacityMult = 1;
        stage1.startPosRel = 0;
        stage1.minPosRel = 0;
        stage1.maxPosRel = 0;
        stage1.beamObject = beam;
        layer1.stages.add(stage1);

        LanceBeamStage stage2 = new LanceBeamStage(stage1);
        stage2.widthStart = coreWidthStart;
        stage2.widthMain = coreWidthMain;
        stage2.widthEnd = coreWidthEnd;
        stage2.minPosRel = 0.2f;
        stage2.maxPosRel = 0.4f;
        stage2.startPosOffsetAbs = 20;
        layer1.stages.add(stage2);

        LanceBeamStage stage3 = new LanceBeamStage(stage2);
        stage3.minPosRel = 0.6f;
        stage3.maxPosRel = 0.8f;
        stage3.startPosRel = 1;
        stage3.startPosOffsetAbs = -20;
        layer1.stages.add(stage3);

        LanceBeamStage stage4 = new LanceBeamStage(stage3);
        stage4.minPosRel = 1;
        stage4.maxPosRel = 1;
        stage4.startPosRel = 1;
        stage4.startPosOffsetAbs = 0;
        stage4.widthStart = coreWidthStart * 0.1f;
        stage4.widthMain = coreWidthMain * 0.1f;
        stage4.widthEnd = coreWidthEnd * 0.1f;
        layer1.stages.add(stage4);

        beam.layers.add(layer1);

        if (plugin != null) {
            plugin.addBeam(beam, CombatEngineLayers.BELOW_INDICATORS_LAYER);
        }
        return beam;
    }
}
