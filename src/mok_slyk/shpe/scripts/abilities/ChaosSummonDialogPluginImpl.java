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
        URSUS_BUY,
        BUTCHER,
        BUTCHER_BUY,
        APIS,
        APIS_BUY,
        DOOM,
        DOOM_BUY,
        FIRE,
        FIRE_BUY,
        LOTUS,
        LOTUS_BUY,
        HARVESTER,
        HARVESTER_BUY,
        TORMENTOR,
        TORMENTOR_BUY,
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
                boolean canWeapons = isChaosGodStage(ChaosGodsEventIntel.KHORNE_I, Stage.GIFT_2) || isChaosGodStage(ChaosGodsEventIntel.SLAANESH_I, Stage.GIFT_2);
                boolean canShips = false;
                boolean canFighters = isChaosGodStage(ChaosGodsEventIntel.TZEENTCH_I, Stage.GIFT_2);

                options.addOption("Summon fighters", OptionId.FIGHTERS, null);
                options.setEnabled(OptionId.FIGHTERS, canFighters);
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
                boolean canChooseWeapons = isChaosGodStage(ChaosGodsEventIntel.KHORNE_I, Stage.GIFT_2) || isChaosGodStage(ChaosGodsEventIntel.SLAANESH_I, Stage.GIFT_2);
                boolean canChooseShips = false;
                boolean canChooseFighters = isChaosGodStage(ChaosGodsEventIntel.TZEENTCH_I, Stage.GIFT_2);

                options.addOption("Summon fighters", OptionId.FIGHTERS, null);
                options.setEnabled(OptionId.FIGHTERS, canChooseFighters);
                options.addOption("Summon ships", OptionId.SHIPS, null);
                options.setEnabled(OptionId.SHIPS, canChooseShips);
                options.addOption("Summon weapons", OptionId.WEAPONS, null);
                options.setEnabled(OptionId.WEAPONS, canChooseWeapons);
                options.addOption("Cancel", OptionId.CANCEL, null);
                break;

            //SHIPS:----------------------------------------------------------------------------------
            case SHIPS:
                textPanel.addParagraph("Choose a ship to summon.");

                options.clearOptions();


                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;

            //WEAPONS:----------------------------------------------------------------------------------
            case WEAPONS:
                textPanel.addParagraph("Choose a weapon to summon.");

                options.clearOptions();

                if (isChaosGodStage(ChaosGodsEventIntel.KHORNE_I, Stage.GIFT_2)) {
                    options.addOption("Butcher Cannon", OptionId.BUTCHER, null);
                    options.addOption("Ursus Claw", OptionId.URSUS, null);
                }
                if (isChaosGodStage(ChaosGodsEventIntel.SLAANESH_I, Stage.GIFT_2)) {
                    options.addOption("Harvester Cannon", OptionId.HARVESTER, null);
                    options.addOption("Tormentor Cannon", OptionId.TORMENTOR, null);
                }

                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case BUTCHER:
                textPanel.addParagraph("Do you want to summon a butcher cannon?");
                boolean canAffordButcher = textPanel.addCostPanel("Each weapon will cost (available)",Commodities.CREW, 5, true, Commodities.METALS, 80, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.BUTCHER_BUY, null);
                options.setEnabled(OptionId.BUTCHER_BUY, canAffordButcher);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case BUTCHER_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 5, textPanel);
                cargo.removeCommodity(Commodities.CREW, 5);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 80, textPanel);
                cargo.removeCommodity(Commodities.METALS, 80);
                AddRemoveCommodity.addWeaponGainText("shpe_butcher_cannon", 1, textPanel);
                cargo.addWeapons("shpe_butcher_cannon", 1);
                optionSelected(null, OptionId.BUTCHER);
                break;
            case URSUS:
                textPanel.addParagraph("Do you want to summon an ursus claw?");
                boolean canAffordUrsus = textPanel.addCostPanel("Each weapon will cost (available)",Commodities.CREW, 50, true, Commodities.METALS, 250, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.URSUS_BUY, null);
                options.setEnabled(OptionId.URSUS_BUY, canAffordUrsus);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case URSUS_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 50, textPanel);
                cargo.removeCommodity(Commodities.CREW, 50);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 250, textPanel);
                cargo.removeCommodity(Commodities.METALS, 250);
                AddRemoveCommodity.addWeaponGainText("shpe_ursus_claw", 1, textPanel);
                cargo.addWeapons("shpe_ursus_claw", 1);
                optionSelected(null, OptionId.URSUS);
                break;
            case TORMENTOR:
                textPanel.addParagraph("Do you want to summon a tormentor cannon?");
                boolean canAffordTormentor = textPanel.addCostPanel("Each weapon will cost (available)",Commodities.CREW, 20, true, Commodities.METALS, 100, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.TORMENTOR_BUY, null);
                options.setEnabled(OptionId.TORMENTOR_BUY, canAffordTormentor);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case TORMENTOR_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 20, textPanel);
                cargo.removeCommodity(Commodities.CREW, 20);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 100, textPanel);
                cargo.removeCommodity(Commodities.METALS, 100);
                AddRemoveCommodity.addWeaponGainText("shpe_tormentor_cannon", 1, textPanel);
                cargo.addWeapons("shpe_tormentor_cannon", 1);
                optionSelected(null, OptionId.TORMENTOR);
                break;
            case HARVESTER:
                textPanel.addParagraph("Do you want to summon a tormentor cannon?");
                boolean canAffordHarvester = textPanel.addCostPanel("Each weapon will cost (available)",Commodities.CREW, 10, true, Commodities.METALS, 30, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.HARVESTER_BUY, null);
                options.setEnabled(OptionId.HARVESTER_BUY, canAffordHarvester);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case HARVESTER_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 10, textPanel);
                cargo.removeCommodity(Commodities.CREW, 10);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 30, textPanel);
                cargo.removeCommodity(Commodities.METALS, 30);
                AddRemoveCommodity.addWeaponGainText("shpe_harvester_cannon", 1, textPanel);
                cargo.addWeapons("shpe_harvester_cannon", 1);
                optionSelected(null, OptionId.HARVESTER);
                break;

            //FIGHTERS:----------------------------------------------------------------------------------
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
            case APIS:
                textPanel.addParagraph("Do you want to summon a wing of Apis bombers?");
                boolean canAffordApis = textPanel.addCostPanel("Each wing will cost (available)",Commodities.CREW, 5, true, Commodities.METALS, 150, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.APIS_BUY, null);
                options.setEnabled(OptionId.APIS_BUY, canAffordApis);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case APIS_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 5, textPanel);
                cargo.removeCommodity(Commodities.CREW, 5);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 150, textPanel);
                cargo.removeCommodity(Commodities.METALS, 150);
                AddRemoveCommodity.addFighterGainText("shpe_apis_Bomber_wing", 1, textPanel);
                cargo.addFighters("shpe_apis_Bomber_wing", 1);
                optionSelected(null, OptionId.APIS);
                break;
            case LOTUS:
                textPanel.addParagraph("Do you want to summon a wing of Lotus interceptors?");
                boolean canAffordLotus = textPanel.addCostPanel("Each wing will cost (available)",Commodities.CREW, 5, true, Commodities.METALS, 150, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.LOTUS_BUY, null);
                options.setEnabled(OptionId.LOTUS_BUY, canAffordLotus);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case LOTUS_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 5, textPanel);
                cargo.removeCommodity(Commodities.CREW, 5);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 150, textPanel);
                cargo.removeCommodity(Commodities.METALS, 150);
                AddRemoveCommodity.addFighterGainText("shpe_lotus_Interceptor_wing", 1, textPanel);
                cargo.addFighters("shpe_lotus_Interceptor_wing", 1);
                optionSelected(null, OptionId.LOTUS);
                break;
            case DOOM:
                textPanel.addParagraph("Do you want to summon a wing of Doom Wing daemon engines?");
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
            case FIRE:
                textPanel.addParagraph("Do you want to summon a wing of Fire Lord daemon engines?");
                boolean canAffordFire = textPanel.addCostPanel("Each wing will cost (available)",Commodities.CREW, 50, true, Commodities.METALS, 100, true);
                options.clearOptions();

                options.addOption("Summon", OptionId.FIRE_BUY, null);
                options.setEnabled(OptionId.FIRE_BUY, canAffordFire);
                options.addOption("Cancel", OptionId.CHOOSE, null);
                break;
            case FIRE_BUY:
                AddRemoveCommodity.addCommodityLossText(Commodities.CREW, 50, textPanel);
                cargo.removeCommodity(Commodities.CREW, 50);
                AddRemoveCommodity.addCommodityLossText(Commodities.METALS, 100, textPanel);
                cargo.removeCommodity(Commodities.METALS, 100);
                AddRemoveCommodity.addFighterGainText("shpe_fire_lord_Engine_wing", 1, textPanel);
                cargo.addFighters("shpe_fire_lord_Engine_wing", 1);
                optionSelected(null, OptionId.FIRE);
                break;

            //CANCEL:----------------------------------------------------------------------------------
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
