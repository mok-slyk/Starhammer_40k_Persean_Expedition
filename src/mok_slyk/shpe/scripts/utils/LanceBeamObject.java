package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class LanceBeamObject {
    WeaponAPI weapon; //can be null for manually spawned in -> won't rotate
    float angle = 0;
    List<LanceBeamStage> stages = new ArrayList<>();
    List<LanceBeamStage> stagesSorted = new ArrayList<>(); //should always be sorted between method calls
    int hitStageStart;
    int hitStageEnd;

    //damage mechanical traits
    boolean didHit;
    float nonEmpDamage;
    DamageType damageType;
    float empDamage;
    float fadeInDamage;
    float fadeOutDamage;
    float fadeInEmp;
    float fadeOutEmp;
    float hitStrengthMult = 1;
    OnHitEffectPlugin onHit;
    boolean applyOnHitToPierced;
    boolean doPierce = false;
    float pierceStrength = 0;
    float impact = 0;

    //visual traits
    SpriteAPI sprite;
    int blendModeSRC;
    int blendModeDEST;

    float scrollSpeed = 0;
    float scrollOffset = 0;

    /**
     * Advances the beam and handles damage mechanics
     * @param amount
     */
    public void advance(float amount) {
        if (weapon != null) {
            angle = weapon.getCurrAngle();
        }

        //handle movement

        stagesSorted.sort(Comparator.comparing(s -> s.currentPos)); //sort ascending for intuitiveness
    }

    /**
     * Renders the beam but doesn't update its state
     */
    public void render() {
        int textureID = sprite.getTextureId(); // TODO: handle animations

        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glEnable(GL_BLEND);
        glBlendFunc(blendModeSRC, blendModeDEST);
        glBegin(GL_QUADS);

        CombatEngineAPI engine = Global.getCombatEngine();

        for (int i = stagesSorted.size()-1; i > 0; i--) { //we descend the list, assumes the stages are sorted

        }
    }
}
