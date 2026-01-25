package mok_slyk.shpe.scripts.utils;

import java.awt.*;

public class LanceBeamStage {
    LanceBeamObject beamObject;

    //movement
    float startPosRel;
    float startPosOffsetAbs;
    float posDriftRel;
    float posDriftAbs;
    float maxPosRel;
    float maxPosOffsetAbs;
    float minPosRel;
    float minPosOffsetAbs;

    float currentPos;

    Color startColor;
    Color mainColor;
    Color endColor;

    Color currentColor;

    float colorFadeIn;
    float colorFadeMain;
    float colorFadeOut;

    float opacityMult;
    float opacityFadeIn;
    float opacityFull;
    float opacityFadeOut;

    float currentOpacity;

}
