package mok_slyk.shpe;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import exerelin.campaign.SectorManager;
import mok_slyk.shpe.scripts.campaign.*;
import mok_slyk.shpe.scripts.utils.Witchcraft;
import mok_slyk.shpe.scripts.world.SHPERelations;
import mok_slyk.shpe.scripts.world.SHPESectorGen;
import org.apache.log4j.Logger;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.TextureData;
import org.magiclib.achievements.MagicAchievementIntel;

public class SHPEModPlugin extends BaseModPlugin {

    private static Logger log = Global.getLogger(SHPEModPlugin.class);
    public static boolean hasGraphicsLib = false;
    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
        hasGraphicsLib = Global.getSettings().getModManager().isModEnabled("shaderLib");
        // Test that the .jar is loaded and working, using the most obnoxious way possible.
        //throw new RuntimeException("Template mod loaded! Remove this crash in TemplateModPlugin.");
        if (hasGraphicsLib) {
            TextureData.readTextureDataCSV("data/lights/shpe_texture_data.csv");
            LightData.readLightDataCSV("data/lights/shpe_light_data.csv");
        }
    }

    @Override
    public void onNewGame() {
        super.onNewGame();

        // The code below requires that Nexerelin is added as a library (not a dependency, it's only needed to compile the mod).
        boolean isNexerelinEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");

        if (!isNexerelinEnabled || SectorManager.getManager().isCorvusMode()) {
            new SHPESectorGen().generate(Global.getSector());
            new SHPERelations().generate(Global.getSector());
            // Add code that creates a new star system (will only run if Nexerelin's Random (corvus) mode is disabled).
       }

    }

    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);
        //if (!Global.getSector().hasTransientScript(ChaosSkillScript.class)) {
        //    Global.getSector().addTransientScript(new ChaosSkillScript());
        //}
        boolean hasCampaignEventListener = false;
        for (CampaignEventListener listener: Global.getSector().getAllListeners()) {
            if (listener.getClass() == ChaosGodsEventManager.class) {
                hasCampaignEventListener = true;
                break;
            }
        }
        if (!hasCampaignEventListener) {
            Global.getSector().addTransientListener(new ChaosGodsEventManager());
            log.info("added manager");
        }
        if (!Global.getSector().getListenerManager().hasListenerOfClass(ChaosGodsEventManager.class)){
            Global.getSector().getListenerManager().addListener(new ChaosGodsEventManager(), true);
        }

    }

    @Override
    public void onNewGameAfterTimePass() {
        super.onNewGameAfterTimePass();

        Global.getSector().addScript(new DisposableChaosFleetManager());
        Global.getSector().addScript(new HyperspaceGodFleetManager());
        //Global.getSector().addScript(new ChaosSkillScript());
        // Global.getSector().getPlayerStats().setSkillLevel("shpe_khorne_chosen", 1);
        if (!Global.getSector().getListenerManager().hasListenerOfClass(ChaosGodsEventManager.class)){
            Global.getSector().getListenerManager().addListener(new ChaosGodsEventManager(), true);
        }
        if (Global.getSector().getPlayerFaction().getRelationship("shpe_chaos") > 0.2f) {
            ChaosGodsEventIntel.getOrCreate(true);
        }
    }
}
