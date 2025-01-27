package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.loading.SkillSpec;
import mok_slyk.shpe.scripts.skills.ChosenOfTzeentch;
import org.apache.log4j.Logger;
import org.json.JSONException;

import java.io.IOException;

public class ChaosSkillScript implements EveryFrameScript {
    private static final Logger log = Global.getLogger(ChaosSkillScript.class);
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (ChaosGodsEventIntel.get().gods[ChaosGodsEventIntel.TZEENTCH_I].isStageActive(GodEventIntel.Stage.DEVOTION)) {
            try {
                Global.getSettings().loadCSV("data/characters/skills/skill_data.csv", "shpe");
            } catch (JSONException e) {
                log.warn("jsonex");
            } catch (IOException e) {
                log.warn("ioex");
            }
            Global.getSettings().getSkillSpec(ChosenOfTzeentch.ID).getTags().remove("npc_only");
            Global.getSettings().resetCached();
            log.info(Global.getSettings().getSkillSpec(ChosenOfTzeentch.ID).getTags());
            Global.getSector().getPlayerStats().setSkillLevel("shpe_tzeentch_chosen", 1);


            log.info("removed tag");
        } else {
            Global.getSettings().getSkillSpec(ChosenOfTzeentch.ID).addTag("npc_only");
            Global.getSettings().resetCached();
            //Global.getSettings().putSpec(SkillSpec());
            log.info("added tag");
            log.info(Global.getSettings().getSkillSpec(ChosenOfTzeentch.ID).getTags());
        }

    }
}
