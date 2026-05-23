package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.graphics.SpriteAPI;

import java.util.ArrayList;
import java.util.List;

import static mok_slyk.shpe.scripts.utils.SHPEDebug.publicLogger;

public class LanceBeamLayer {
    //visual traits
    SpriteAPI sprite;
    int blendModeSRC;
    int blendModeDEST;

    float scrollSpeed = 0; // per second
    float scrollOffset = 0;

    List<LanceBeamStage> stages = new ArrayList<>();

    public void advance(float amount) {
        for (LanceBeamStage stage: stages) {
            stage.advance(amount);
            publicLogger.info("stage state: " + stages.indexOf(stage) + ", " + stage.currentPos);
        }

        //ensure stages are ordered in space, so they can render correctly
        for (int i = stages.size()-2; i > 0; i--) {
            if(stages.get(i).currentPos > stages.get(i+1).currentPos) {
                stages.get(i).currentPos = stages.get(i+1).currentPos - 0.01f;
            }
        }

        scrollOffset = (scrollOffset + scrollSpeed * amount) % sprite.getHeight();
    }
}
