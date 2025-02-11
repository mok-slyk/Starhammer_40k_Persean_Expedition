package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;


import java.awt.*;
import java.util.*;
import java.util.List;

public class ChaosGodsEventIntel extends BaseEventIntel implements FleetEventListener {
    public static String KEY = "$shpe_cgods_ref";
    public static int PROGRESS_MAX = 100;

    public static enum Stage {
        START,
        UNALIGNED,
        KHORNE_MARK,
        SLAANESH_MARK,
        TZEENTCH_MARK,
        NURGLE_MARK
    }

    public static final String KHORNE_MARK_BUTTON = "khorne_mark_button";
    public static final String KHORNE_UNMARK_BUTTON = "khorne_unmark_button";
    public static final String TZEENTCH_MARK_BUTTON = "tzeentch_mark_button";
    public static final String TZEENTCH_UNMARK_BUTTON = "tzeentch_unmark_button";
    public static final String NURGLE_MARK_BUTTON = "nurgle_mark_button";
    public static final String NURGLE_UNMARK_BUTTON = "nurgle_unmark_button";
    public static final String SLAANESH_MARK_BUTTON = "slaanesh_mark_button";
    public static final String SLAANESH_UNMARK_BUTTON = "slaanesh_unmark_button";

    public static final Color UNDIVIDED_COLOR = new Color(120, 60, 211);
    public Stage stageAt = Stage.START;

    public static final int KHORNE_I = 0;
    public static final int TZEENTCH_I = 1;
    public static final int NURGLE_I = 2;
    public static final int SLAANESH_I = 3;

    public static final int SMALL_TO_LARGE_FLEET_THRESHOLD = 100;

    protected Set<ButtonAPI> buttons = new HashSet<>(8);
    boolean updatePanel = false;

    protected CustomPanelAPI intelPanel;
    protected float intelWidth;
    protected float intelHeight;

    //protected TooltipMakerAPI intelMain;

    public GodEventIntel[] gods = new GodEventIntel[4];
    public ChaosGodsEventIntel(TextPanelAPI text, boolean withIntelNotification) {
        super();

        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);

        gods[KHORNE_I] = new GodEventIntel(KHORNE_I,"Khorne", "$shpe_khorne_ref", new Color(180, 5, 18));
        gods[TZEENTCH_I] = new GodEventIntel(TZEENTCH_I,"Tzeentch", "$shpe_tzeentch_ref", new Color(36, 115, 189));
        gods[NURGLE_I] = new GodEventIntel(NURGLE_I,"Nurgle", "$shpe_nurgle_ref", new Color(115, 190, 104));
        gods[SLAANESH_I] = new GodEventIntel(SLAANESH_I,"Slaanesh", "$shpe_slaanesh_ref", new Color(135, 50, 162));

        for (GodEventIntel god: gods) {
            god.masterEvent = this;
        }

        setup();

        // now that the event is fully constructed, add it and send notification
        Global.getSector().getIntelManager().addIntel(this, !withIntelNotification, text);
    }

    public static ChaosGodsEventIntel get() {
        return (ChaosGodsEventIntel) Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    @Override
    protected void notifyEnding() {
        super.notifyEnding();
    }

    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().getMemoryWithoutUpdate().unset(KEY);
    }

    protected Object readResolve() {
        if (getDataFor(Stage.START) == null) {
            setup();
        }
        return this;
    }

    EventProgressBarAPI[] bars = new EventProgressBarAPI[4];

    protected void setup() {
        factors.clear();
        stages.clear();

        setMaxProgress(PROGRESS_MAX);

        addStage(Stage.START,0);
        addStage(Stage.UNALIGNED,10);
        addStage(Stage.NURGLE_MARK,20);
        addStage(Stage.SLAANESH_MARK,30);
        addStage(Stage.TZEENTCH_MARK,40);
        addStage(Stage.KHORNE_MARK,50);

        getDataFor(Stage.KHORNE_MARK).sendIntelUpdateOnReaching = false;
        getDataFor(Stage.TZEENTCH_MARK).sendIntelUpdateOnReaching = false;
        getDataFor(Stage.NURGLE_MARK).sendIntelUpdateOnReaching = false;
        getDataFor(Stage.SLAANESH_MARK).sendIntelUpdateOnReaching = false;

        setProgress(0);
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        intelPanel = panel;
        intelWidth = width;
        intelHeight = height;
        buttons.clear();

        float opad = 20f;
        uiWidth = width;

        TooltipMakerAPI main = panel.createUIElement(width, height, true);
        //intelMain = main;

        main.setTitleOrbitronVeryLarge();
        main.addTitle(getName(), Misc.getBasePlayerColor());

        for (int i = 0; i < gods.length; i++) {
            bars[i] = main.addEventProgressBar(gods[i], 130f);

            TooltipMakerAPI.TooltipCreator barTC = gods[i].getBarTooltip();
            if (barTC != null) {
                main.addTooltipToPrevious(barTC, TooltipMakerAPI.TooltipLocation.BELOW, false);
            }

            for (EventStageData curr : gods[i].getStages()) {
                if (curr.progress <= 0) continue; // no icon for "starting" stage

                if (curr.wasEverReached && curr.isOneOffEvent && !curr.isRepeatable) continue;

                if (curr.hideIconWhenPastStageUnlessLastActive &&
                        curr.progress <= gods[i].getProgress() &&
                        gods[i].getLastActiveStage(true) != curr) {
                    continue;
                }

                EventStageDisplayData data = gods[i].createDisplayData(curr.id);
                UIComponentAPI marker = main.addEventStageMarker(data);
                float xOff = bars[i].getXCoordinateForProgress(curr.progress) - bars[i].getPosition().getX();
                marker.getPosition().aboveLeft(bars[i], data.downLineLength).setXAlignOffset(xOff - data.size / 2f - 1);

                TooltipMakerAPI.TooltipCreator tc = gods[i].getStageTooltip(curr.id);
                if (tc != null) {
                    main.addTooltipTo(tc, marker, TooltipMakerAPI.TooltipLocation.LEFT, false);
                }
            }

            //progress marker
            UIComponentAPI marker = main.addEventProgressMarker(gods[i]);
            float xOff = bars[i].getXCoordinateForProgress(gods[i].getProgress()) - bars[i].getPosition().getX();
            marker.getPosition().belowLeft(bars[i], -getBarProgressIndicatorHeight() * 0.5f - 2)
                    .setXAlignOffset(xOff - getBarProgressIndicatorWidth() / 2 - 1);

        }

        float imageSize = getImageSizeForStageDesc(null);
        String icon = getStageIcon(null);
        float indent = 10f;

        main.addSpacer(opad);
        TooltipMakerAPI info = main.beginImageWithText(icon, imageSize, width, true);
        //TooltipMakerAPI info = main.beginImageWithText("graphics/icons/missions/ga_intro.png", 64);
        addStageDescriptionText(info, width - imageSize - opad, null);
        if (info.getHeightSoFar() > 0) {
            main.addImageWithText(opad).getPosition().setXAlignOffset(indent);
            main.addSpacer(10).getPosition().setXAlignOffset(-indent);
        }

        if (isMarked()) {
            if (stageAt == Stage.KHORNE_MARK) {
                ButtonAPI b0 = main.addAreaCheckbox(
                        "Forsake", KHORNE_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[0], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b0.getPosition().inTL(800, 150);
                buttons.add(b0);
            } else if (stageAt == Stage.TZEENTCH_MARK) {
                ButtonAPI b1 = main.addAreaCheckbox(
                        "Forsake", TZEENTCH_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[1], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b1.getPosition().inTL(800, 150 + 1 * 155);
                buttons.add(b1);
            } else if (stageAt == Stage.NURGLE_MARK) {
                ButtonAPI b2 = main.addAreaCheckbox(
                        "Forsake", NURGLE_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[2], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b2.getPosition().inTL(800, 150 + 2 * 155);
                buttons.add(b2);
            } else if (stageAt == Stage.SLAANESH_MARK) {
                ButtonAPI b3 = main.addAreaCheckbox(
                        "Forsake", SLAANESH_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[3], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b3.getPosition().inTL(800, 150 + 3 * 155);
                buttons.add(b3);
            }
        } else {
            if (gods[KHORNE_I].getProgress() == 50) {
                ButtonAPI b0 = main.addAreaCheckbox(
                        "Take on Mark", KHORNE_MARK_BUTTON, GodEventIntel.GOD_COLORS[0], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b0.getPosition().inTL(800, 150);
                buttons.add(b0);
            }

            if (gods[TZEENTCH_I].getProgress() == 50) {
                ButtonAPI b1 = main.addAreaCheckbox(
                        "Take on Mark", TZEENTCH_MARK_BUTTON, GodEventIntel.GOD_COLORS[1], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b1.getPosition().inTL(800, 150 + 1 * 155);
                buttons.add(b1);
            }

            if (gods[NURGLE_I].getProgress() == 50) {
                ButtonAPI b2 = main.addAreaCheckbox(
                        "Take on Mark", NURGLE_MARK_BUTTON, GodEventIntel.GOD_COLORS[2], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b2.getPosition().inTL(800, 150 + 2 * 155);
                buttons.add(b2);
            }

            if (gods[SLAANESH_I].getProgress() == 50) {
                ButtonAPI b3 = main.addAreaCheckbox(
                        "Take on Mark", SLAANESH_MARK_BUTTON, GodEventIntel.GOD_COLORS[3], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b3.getPosition().inTL(800, 150 + 3 * 155);
                buttons.add(b3);
            }
        }

        float barW = getBarWidth();
        float factorWidth = (barW - opad) / 2f;

        if (withMonthlyFactors() != withOneTimeFactors()) {
            //factorWidth = barW;
            factorWidth = (int) (barW * 0.6f);
        }

        TooltipMakerAPI mFac = main.beginSubTooltip(factorWidth);

        panel.addUIElement(main).inTL(0, 0);

        List<EventFactor> godFactors = new ArrayList<>();
        godFactors.addAll(gods[KHORNE_I].getFactors());
        godFactors.addAll(gods[TZEENTCH_I].getFactors());
        godFactors.addAll(gods[NURGLE_I].getFactors());
        godFactors.addAll(gods[SLAANESH_I].getFactors());

    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId == KHORNE_MARK_BUTTON) {
            stageAt = Stage.KHORNE_MARK;
            gods[SLAANESH_I].setProgress(gods[SLAANESH_I].getProgress());
            ui.updateUIForItem(this);
        }
        if (buttonId == TZEENTCH_MARK_BUTTON) {
            stageAt = Stage.TZEENTCH_MARK;
            gods[NURGLE_I].setProgress(gods[NURGLE_I].getProgress());
            ui.updateUIForItem(this);
        }
        if (buttonId == NURGLE_MARK_BUTTON) {
            stageAt = Stage.NURGLE_MARK;
            gods[TZEENTCH_I].setProgress(gods[TZEENTCH_I].getProgress());
            ui.updateUIForItem(this);
        }
        if (buttonId == SLAANESH_MARK_BUTTON) {
            stageAt = Stage.SLAANESH_MARK;
            gods[KHORNE_I].setProgress(gods[KHORNE_I].getProgress());
            ui.updateUIForItem(this);
        }
        if (buttonId == KHORNE_UNMARK_BUTTON) {
            stageAt = Stage.UNALIGNED;
            gods[KHORNE_I].setProgress(0);
            ui.updateUIForItem(this);
        }
        if (buttonId == TZEENTCH_UNMARK_BUTTON) {
            stageAt = Stage.UNALIGNED;
            gods[TZEENTCH_I].setProgress(0);
            ui.updateUIForItem(this);
        }
        if (buttonId == NURGLE_UNMARK_BUTTON) {
            stageAt = Stage.UNALIGNED;
            gods[NURGLE_I].setProgress(0);
            ui.updateUIForItem(this);
        }
        if (buttonId == SLAANESH_UNMARK_BUTTON) {
            stageAt = Stage.UNALIGNED;
            gods[SLAANESH_I].setProgress(0);
            ui.updateUIForItem(this);
        }
    }

    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        if (buttonId == KHORNE_MARK_BUTTON || buttonId == TZEENTCH_MARK_BUTTON || buttonId == NURGLE_MARK_BUTTON || buttonId == SLAANESH_MARK_BUTTON
            || buttonId == KHORNE_UNMARK_BUTTON || buttonId == TZEENTCH_UNMARK_BUTTON || buttonId == NURGLE_UNMARK_BUTTON || buttonId == SLAANESH_UNMARK_BUTTON
        ) {
            return true;
        }
        return false;
    }

    public boolean isMarked() {
        return stageAt == Stage.KHORNE_MARK || stageAt == Stage.TZEENTCH_MARK || stageAt == Stage.NURGLE_MARK || stageAt == Stage.SLAANESH_MARK;
    }

    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        switch ((String)buttonId) {
            case KHORNE_MARK_BUTTON:
                prompt.addPara("Are you sure you want to dedicate yourself to Khorne?", Misc.getTextColor(), 0f);
                break;
            case TZEENTCH_MARK_BUTTON:
                prompt.addPara("Are you sure you want to dedicate yourself to Tzeentch?", Misc.getTextColor(), 0f);
                break;
            case NURGLE_MARK_BUTTON:
                prompt.addPara("Are you sure you want to dedicate yourself to Nurgle?", Misc.getTextColor(), 0f);
                break;
            case SLAANESH_MARK_BUTTON:
                prompt.addPara("Are you sure you want to dedicate yourself to Slaanesh?", Misc.getTextColor(), 0f);
                break;
            case KHORNE_UNMARK_BUTTON:
                prompt.addPara("Are you sure you want to forsake your mark? This will anger Khorne.", Misc.getTextColor(), 0f);
                break;
            case TZEENTCH_UNMARK_BUTTON:
                prompt.addPara("Are you sure you want to forsake your mark? This will anger Tzeentch.", Misc.getTextColor(), 0f);
                break;
            case NURGLE_UNMARK_BUTTON:
                prompt.addPara("Are you sure you want to forsake your mark? This will anger Nurgle.", Misc.getTextColor(), 0f);
                break;
            case SLAANESH_UNMARK_BUTTON:
                prompt.addPara("Are you sure you want to forsake your mark? This will anger Slaanesh.", Misc.getTextColor(), 0f);
                break;
        }
    }

    @Override
    protected void advanceImpl(float amount) {

        //handle buttons
        /*
        for (ButtonAPI button: buttons) {
            if (button.isChecked()) {
                button.setChecked(false);
                if (button.getCustomData() == Stage.KHORNE_MARK) {
                    stageAt = Stage.KHORNE_MARK;
                    doPanelUpdate();
                }
                if (button.getCustomData() == Stage.TZEENTCH_MARK) {
                    stageAt = Stage.TZEENTCH_MARK;
                    doPanelUpdate();
                }
                if (button.getCustomData() == Stage.NURGLE_MARK) {
                    stageAt = Stage.NURGLE_MARK;
                    doPanelUpdate();
                }
                if (button.getCustomData() == Stage.SLAANESH_MARK) {
                    stageAt = Stage.SLAANESH_MARK;
                    doPanelUpdate();
                }
            }
        }

         */
        /*
        if (updatePanel) {
            intelPanel.removeComponent(intelMain);
            createLargeDescription(intelPanel, intelWidth, intelHeight);
            updatePanel = false;
        }
         */

        super.advanceImpl(amount);
    }

    protected String getStageIcon(Object stageId) {
        if (stageAt == Stage.KHORNE_MARK) return Global.getSettings().getSpriteName("icons", "khorne_glow");
        if (stageAt == Stage.TZEENTCH_MARK) return Global.getSettings().getSpriteName("icons", "tzeentch_glow");
        if (stageAt == Stage.NURGLE_MARK) return Global.getSettings().getSpriteName("icons", "nurgle_glow");
        if (stageAt == Stage.SLAANESH_MARK) return Global.getSettings().getSpriteName("icons", "slaanesh_glow");
        return Global.getSettings().getSpriteName("icons", "chaos_motes");
    }

    @Override
    public void addStageDescriptionText(TooltipMakerAPI info, float width, Object stageId) {
        float opad = 10f;
        float small = 0f;
        Color h = Misc.getHighlightColor();
        if (stageAt == Stage.KHORNE_MARK) {
            info.addPara("You are marked by Khorne, favor with Slaanesh cannot exceed 25, favor with other gods cannot exceed 50. ",
                    small);
        } else if (stageAt == Stage.TZEENTCH_MARK) {
            info.addPara("You are marked by Tzeentch, favor with Nurgle cannot exceed 25, favor with other gods cannot exceed 50. ",
                    small);
        } else if (stageAt == Stage.SLAANESH_MARK) {
            info.addPara("You are marked by Slaanesh, favor with Khorne cannot exceed 25, favor with other gods cannot exceed 50. ",
                    small);
        } else if (stageAt == Stage.NURGLE_MARK) {
            info.addPara("You are marked by Nurgle, favor with Tzeentch cannot exceed 25, favor with other gods cannot exceed 50. ",
                    small);
        } else {
            info.addPara("The gods take little interest in you. "
                            + ""
                            + ""
                            + "",
                    small);
        }
    }

    @Override
    protected String getName() {
        return "Touch of Chaos";
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_IMPORTANT);
        //tags.remove(Tags.INTEL_MAJOR_EVENT);
        return tags;
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icons", "chaos_swirl");
    }

    public void doPanelUpdate() {
        updatePanel = true;
    }

    public boolean runWhilePaused() {
        return false;
    }

    @Override
    protected String getSoundForOneTimeFactorUpdate(EventFactor factor) {
        return null;
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {}

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (isEnded() || isEnding()) return;

        if (!battle.isPlayerInvolved()) return;

        int totalFleetPointsDefeated = 0;
        boolean foughtImperium = false;
        boolean foughtChaos = false;
        boolean foughtKhorne = false;
        boolean foughtTzeentch = false;
        boolean foughtNurgle = false;
        boolean foughtSlaanesh = false;

        boolean bigBattle = false;
        boolean superiorFoe = false;
        if (battle.getPlayerSide().contains(primaryWinner)) {
            if (battle.getPlayerCombined().getFleetPoints()*1.1f<battle.getNonPlayerCombined().getFleetPoints()) {
                superiorFoe = true;
            }
            for (CampaignFleetAPI otherFleet : battle.getNonPlayerSideSnapshot()) {
                totalFleetPointsDefeated += otherFleet.getFleetPoints();
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_imperium")) {
                    foughtImperium = true;
                }
                if (Objects.equals(otherFleet.getFaction().getId(), "shpe_chaos")) {
                    foughtChaos = true;
                }
            }
            if (totalFleetPointsDefeated > SMALL_TO_LARGE_FLEET_THRESHOLD) {
                bigBattle = true;
            }

            // Gain on kill imperium ships:
            if (foughtImperium) {
                if (bigBattle) {
                    addFactor(new ChaosGodOneTimeFactor(1, "Imperial ships destroyed", "Imperial ships destroyed by your fleet."), null);
                } else {
                    addFactor(new ChaosGodOneTimeFactor(2, "Imperial ships destroyed", "Imperial ships destroyed by your fleet."), null);
                }
            }
            if (foughtChaos) {
                addFactor(new ChaosGodOneTimeFactor(-1, "Chaos ships destroyed", "Chaos Undivided ships destroyed by your fleet."), null);
            }
            if (superiorFoe) {
                addFactor(new ChaosGodOneTimeFactor(1, KHORNE_I, "Superior foe defeated", "Superior fleet defeated by your fleet."), null);
            }
        }
    }

    @Override
    public void addFactor(EventFactor factor, InteractionDialogAPI dialog) {
        addingFactorDialog = dialog;
        factors.add(factor);
        if (factor.isOneTime()) {
            TextPanelAPI textPanel = dialog == null ? null : dialog.getTextPanel();
            sendUpdateIfPlayerHasIntel(factor, textPanel);
        }
        addingFactorDialog = null;
        if (factor instanceof BaseChaosGodFactor) {
            if (((BaseChaosGodFactor) factor).undivided) {
                gods[KHORNE_I].addFactor(factor);
                gods[TZEENTCH_I].addFactor(factor);
                gods[NURGLE_I].addFactor(factor);
                gods[SLAANESH_I].addFactor(factor);
            } else {
                gods[((BaseChaosGodFactor) factor).godIndex].addFactor(factor);
            }
        }
    }

    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        addEventFactorBulletPoints(info, mode, isUpdate, tc, initPad);
    }

    //RunCode mok_slyk.shpe.scripts.campaign.ChaosGodsEventIntel.get().addGodProgress(1,20);
    public void addGodProgress(int godID, int prog) {
        gods[godID].setProgress(gods[godID].getProgress()+prog);
    }
}
