package mok_slyk.shpe.scripts.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.rules.Option;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveAnyItem;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.tutorial.TutorialWelcomeDialogPluginImpl;
import mok_slyk.shpe.scripts.campaign.ChaosGodsEventIntel;
import mok_slyk.shpe.scripts.campaign.GodEventIntel;
import mok_slyk.shpe.scripts.campaign.GodEventIntel.Stage;

import java.util.Map;

public class ChaosSummonDialogPluginImpl implements InteractionDialogPlugin {

    public static enum OptionId {
        INIT,
        SAC,
        CHOOSE,
        WEAPONS,
        SHIPS,
        FIGHTERS,
        CANCEL,

        //gear options:
        URSUS,
        BUTCHER,
        APIS,
        DOOM,
        DOOM_BUY,
        FIRE,
        LOTUS,
        HARVESTER
    }
    protected InteractionDialogAPI dialog;
    protected TextPanelAPI textPanel;
    protected OptionPanelAPI options;
    protected VisualPanelAPI visual;

    protected CampaignFleetAPI playerFleet;
    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        textPanel = dialog.getTextPanel();
        options = dialog.getOptionPanel();
        visual = dialog.getVisualPanel();

        playerFleet = Global.getSector().getPlayerFleet();

        visual.showImagePortion("illustrations", "chaos_summon", 640, 400, 0, 0, 480, 300);
        optionSelected(null, OptionId.INIT);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (optionData == null) return;

        OptionId option = (OptionId) optionData;
        if (optionText != null) {
            dialog.addOptionSelectedText(option);
        }

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();

        switch (option) {
            case INIT:
                textPanel.addParagraph("You prepare to make a blood sacrifice to call upon the powers of the warp");
                boolean canAfford = textPanel.addCostPanel("Sacrifices required (available)", Commodities.CREW, 30, true);

                options.clearOptions();
                options.addOption("Make the sacrifice", OptionId.SAC, null);
                options.addOption("Cancel", OptionId.CANCEL, null);
                options.setEnabled(OptionId.SAC, canAfford);
                break;
            case SAC:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 30, textPanel);
                cargo.removeCommodity(Commodities.CREW, 30);
                textPanel.addParagraph("The cries of the dying are drowned out by the screaming of unseen demonic entities whirling about your ship.");
                textPanel.addParagraph("Make your demand.");

                options.clearOptions();
                boolean canWeapons = true;
                boolean canShips = true;
                boolean canFighters = true;

                options.addOption("Summon fighters", OptionId.FIGHTERS, null);
                options.setEnabled(OptionId.FIGHTERS, canShips);
                options.addOption("Summon ships", OptionId.SHIPS, null);
                options.setEnabled(OptionId.SHIPS, canShips);
                options.addOption("Summon weapons", OptionId.WEAPONS, null);
                options.setEnabled(OptionId.WEAPONS, canWeapons);
                options.addOption("Cancel", OptionId.CANCEL, null);
                break;
            case CHOOSE:
                textPanel.addParagraph("The warp roils around you.");
                textPanel.addParagraph("Make your demand.");

                options.clearOptions();
                boolean canChooseWeapons = true;
                boolean canChooseShips = true;
                boolean canChooseFighters = true;

                options.addOption("Summon fighters", OptionId.FIGHTERS, null);
                options.setEnabled(OptionId.FIGHTERS, canChooseFighters);
                options.addOption("Summon ships", OptionId.SHIPS, null);
                options.setEnabled(OptionId.SHIPS, canChooseShips);
                options.addOption("Summon weapons", OptionId.WEAPONS, null);
                options.setEnabled(OptionId.WEAPONS, canChooseWeapons);
                options.addOption("Cancel", OptionId.CANCEL, null);
                break;
            case SHIPS:
                textPanel.addParagraph("Choose a ship to summon.");

                options.clearOptions();


                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case WEAPONS:
                textPanel.addParagraph("Choose a weapon to summon.");

                options.clearOptions();

                if (isChaosGodStage(0, Stage.GIFT_2)) {
                    options.addOption("Butcher Cannon", OptionId.BUTCHER, null);
                    options.addOption("Ursus Claw", OptionId.URSUS, null);
                }

                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case FIGHTERS:
                textPanel.addParagraph("Choose a fighter wing to summon.");

                options.clearOptions();

                if (isChaosGodStage(1, Stage.GIFT_2)) {
                    options.addOption("Apis Bomber", OptionId.APIS, null);
                    options.addOption("Doom Wing", OptionId.DOOM, null);
                    options.addOption("Fire Lord", OptionId.FIRE, null);
                    options.addOption("Lotus Fighter", OptionId.LOTUS, null);
                }
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case DOOM:
                textPanel.addParagraph("Do you want to summon a wing of Doom Wing daemon engines?");
                //textPanel.addParagraph("Each wing will cost:");
                boolean canAffordDoom = textPanel.addCostPanel("Each wing will cost (available)",Commodities.CREW, 30, true, Commodities.METALS, 100, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.DOOM_BUY, null);
                options.setEnabled(OptionId.DOOM_BUY, canAffordDoom);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case DOOM_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 30, textPanel);
                cargo.removeCommodity(Commodities.CREW, 30);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 100, textPanel);
                cargo.removeCommodity(Commodities.METALS, 100);
                AddRemoveCommodity.addFighterGainText("shpe_doom_wing_Engine_wing", 1, textPanel);
                cargo.addFighters("shpe_doom_wing_Engine_wing", 1);
                optionSelected(null, OptionId.DOOM);
                break;
            case CANCEL:
                Global.getSector().setPaused(false);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    protected boolean isChaosGodStage(int godID, Stage stage) {
        return ChaosGodsEventIntel.get().gods[godID].isStageActive(stage);
    }

    protected boolean canChooseFighters() {
        return isChaosGodStage(1, Stage.GIFT_2);//tzeentch fighters
    }

    protected boolean canChooseShips() {
        return isChaosGodStage(0, Stage.GIFT_3)||isChaosGodStage(1, Stage.GIFT_3)||isChaosGodStage(2, Stage.GIFT_3)||isChaosGodStage(3, Stage.GIFT_3);
    }

    protected boolean canChooseWeapons() {
        return isChaosGodStage(0, Stage.GIFT_2)||isChaosGodStage(2, Stage.GIFT_2)||isChaosGodStage(3, Stage.GIFT_2);
    }
}
