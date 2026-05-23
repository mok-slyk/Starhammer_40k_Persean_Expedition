package mok_slyk.shpe.scripts.utils;

import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static mok_slyk.shpe.scripts.utils.SHPEDebug.publicLogger;

public class LanceBeamStage {
    // everything is recalculated from progress and parameters each frame
    LanceBeamObject beamObject;

    //movement
    float startPosRel;
    float startPosOffsetAbs = 0;

    // drift is over total time
    float posDriftRel = 0;
    float posDriftAbs = 0;
    float maxPosRel;
    float maxPosOffsetAbs = 0;
    float minPosRel;
    float minPosOffsetAbs = 0;

    float currentPos = 0; // is completely recalculated every frame

    Color startColor;
    Color mainColor;
    Color endColor;

    Color currentColor = null;

    float colorFadeIn;
    float colorFadeMain;
    float colorFadeOut;

    float opacityMult;
    float opacityDelay = 0; //before starting to fade in
    float opacityFadeIn;
    float opacityFull;
    float opacityFadeOut;

    float currentOpacity = 0;

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
        if (beamAge < colorFadeIn)
            currentColor = SHPEUtils.lerpColor(startColor, mainColor, beamAge / colorFadeIn);
        else if (beamAge >= colorFadeIn && beamAge < colorFadeIn + colorFadeMain)
            currentColor = mainColor;
        else if (beamAge >= colorFadeIn + colorFadeMain && beamAge < colorFadeIn + colorFadeMain + colorFadeOut)
            currentColor = SHPEUtils.lerpColor(mainColor, endColor, 1 - (beamAge - colorFadeIn - colorFadeMain / colorFadeOut - colorFadeIn - colorFadeMain));
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

    public LanceBeamStage() {
    }

    public LanceBeamStage(LanceBeamStage other) {
        // object reference (shallow copy)
        this.beamObject = other.beamObject;

        // movement
        this.startPosRel = other.startPosRel;
        this.startPosOffsetAbs = other.startPosOffsetAbs;

        this.posDriftRel = other.posDriftRel;
        this.posDriftAbs = other.posDriftAbs;
        this.maxPosRel = other.maxPosRel;
        this.maxPosOffsetAbs = other.maxPosOffsetAbs;
        this.minPosRel = other.minPosRel;
        this.minPosOffsetAbs = other.minPosOffsetAbs;

        // colors (defensive copy)
        this.startColor = other.startColor != null ? new Color(other.startColor.getRGB(), true) : null;
        this.mainColor  = other.mainColor  != null ? new Color(other.mainColor.getRGB(), true)  : null;
        this.endColor   = other.endColor   != null ? new Color(other.endColor.getRGB(), true)   : null;

        this.colorFadeIn = other.colorFadeIn;
        this.colorFadeMain = other.colorFadeMain;
        this.colorFadeOut = other.colorFadeOut;

        this.opacityMult = other.opacityMult;
        this.opacityDelay = other.opacityDelay;
        this.opacityFadeIn = other.opacityFadeIn;
        this.opacityFull = other.opacityFull;
        this.opacityFadeOut = other.opacityFadeOut;

        this.widthStart = other.widthStart;
        this.widthMain = other.widthMain;
        this.widthEnd = other.widthEnd;
    }
}
