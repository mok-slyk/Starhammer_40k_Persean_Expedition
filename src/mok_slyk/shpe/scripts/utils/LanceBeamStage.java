package mok_slyk.shpe.scripts.utils;

import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class LanceBeamStage {
    // everything is recalculated from progress and parameters each frame
    LanceBeamObject beamObject;

    //movement
    float startPosRel;
    float startPosOffsetAbs;

    // drift is over total time
    float posDriftRel;
    float posDriftAbs;
    float maxPosRel;
    float maxPosOffsetAbs;
    float minPosRel;
    float minPosOffsetAbs;

    float currentPos; // is completely recalculated every frame

    Color startColor;
    Color mainColor;
    Color endColor;

    Color currentColor;

    float colorFadeIn;
    float colorFadeMain;
    float colorFadeOut;

    float opacityMult;
    float opacityDelay = 0; //before starting to fade in
    float opacityFadeIn;
    float opacityFull;
    float opacityFadeOut;

    float currentOpacity;

    float widthStart;
    float widthMain;
    float widthEnd;

    float currentWidth;

    public void advance(float amount) {
        float beamExtension = beamObject.extension;
        float beamAge = beamObject.age;
        float beamLifetime = beamObject.fadeInTime + beamObject.fadeOutTime + beamObject.damageWindowTime;
        float beamLifeFraction = beamAge / beamLifetime;

        //calculate position
        currentPos = startPosOffsetAbs + startPosRel * beamExtension + beamLifeFraction * (posDriftAbs + posDriftRel * beamExtension);
        currentPos = MathUtils.clamp(currentPos, minPosRel * beamExtension + minPosOffsetAbs, maxPosRel * beamExtension * maxPosOffsetAbs);

        // calculate opacity
        if (beamAge < opacityDelay) currentOpacity = 0;
        else if (beamAge >= opacityDelay && beamAge < opacityDelay + opacityFadeIn)
            currentOpacity = ((beamAge - opacityDelay) / (opacityFadeIn - opacityDelay)) * opacityMult;
        else if (beamAge >= opacityDelay + opacityFadeIn && beamAge < opacityDelay + opacityFadeIn + opacityFull)
            currentOpacity = opacityMult;
        else if (beamAge >= opacityDelay + opacityFadeIn + opacityFull && beamAge < opacityDelay + opacityFadeIn + opacityFull + opacityFadeOut)
            currentOpacity = (1 - ((beamAge - opacityDelay - opacityFadeIn - opacityFull) / (opacityFadeOut - opacityDelay - opacityFadeIn - opacityFull))) * opacityMult;
        else
            currentOpacity = 0;

        //calculate color
        if (beamAge < beamObject.fadeInTime)
            currentColor = SHPEUtils.lerpColor(startColor, mainColor, beamAge / beamObject.fadeInTime);
        else if (beamAge >= beamObject.fadeInTime && beamAge < beamObject.fadeInTime + beamObject.damageWindowTime)
            currentColor = mainColor;
        else if (beamAge >= beamObject.fadeInTime + beamObject.damageWindowTime && beamAge < beamObject.fadeInTime + beamObject.damageWindowTime + beamObject.fadeOutTime)
            currentColor = SHPEUtils.lerpColor(mainColor, endColor, 1 - (beamAge - beamObject.fadeInTime - beamObject.damageWindowTime / beamObject.fadeOutTime - beamObject.fadeInTime - beamObject.damageWindowTime));
        else
            currentColor = endColor;

        //calculate width
        float fac = 1;
        if (beamAge < beamObject.fadeInTime)
            currentWidth = (beamAge / beamObject.fadeInTime) * widthMain + (1 - (beamAge / beamObject.fadeInTime)) * widthStart;
        else if (beamAge >= beamObject.fadeInTime && beamAge < beamObject.fadeInTime + beamObject.damageWindowTime)
            currentWidth = widthMain;
        else if (beamAge >= beamObject.fadeInTime + beamObject.damageWindowTime && beamAge < beamObject.fadeInTime + beamObject.damageWindowTime + beamObject.fadeOutTime) {
            fac = 1 - (beamAge - beamObject.fadeInTime - beamObject.damageWindowTime / beamObject.fadeOutTime - beamObject.fadeInTime - beamObject.damageWindowTime);
            currentWidth = fac * widthMain + (fac-1) * widthEnd;
        }
        else
            currentWidth = widthEnd;
    }

    public Vector2f getWorldPosition() {
        return MathUtils.getPointOnCircumference(beamObject.pos, currentPos, beamObject.angle);
    }
}
