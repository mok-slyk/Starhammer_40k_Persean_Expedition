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

public class ChaosGodsEventIntel extends BaseEventIntel {
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
    boolean updatePanel = false;
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

    public static ChaosGodsEventIntel getOrCreate(boolean withNotification) {
        ChaosGodsEventIntel intel = get();
        if (intel == null) {
            intel = new ChaosGodsEventIntel(null, withNotification);
        }
        return intel;
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
        EventProgressBarAPI[] bars = new EventProgressBarAPI[4];

        intelWidth = width;
        intelHeight = height;

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
            } else if (stageAt == Stage.TZEENTCH_MARK) {
                ButtonAPI b1 = main.addAreaCheckbox(
                        "Forsake", TZEENTCH_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[1], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b1.getPosition().inTL(800, 150 + 1 * 155);
            } else if (stageAt == Stage.NURGLE_MARK) {
                ButtonAPI b2 = main.addAreaCheckbox(
                        "Forsake", NURGLE_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[2], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b2.getPosition().inTL(800, 150 + 2 * 155);
            } else if (stageAt == Stage.SLAANESH_MARK) {
                ButtonAPI b3 = main.addAreaCheckbox(
                        "Forsake", SLAANESH_UNMARK_BUTTON, GodEventIntel.GOD_COLORS[3], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b3.getPosition().inTL(800, 150 + 3 * 155);
            }
        } else {
            if (gods[KHORNE_I].getProgress() == 50) {
                ButtonAPI b0 = main.addAreaCheckbox(
                        "Take on Mark", KHORNE_MARK_BUTTON, GodEventIntel.GOD_COLORS[0], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b0.getPosition().inTL(800, 150);
            }

            if (gods[TZEENTCH_I].getProgress() == 50) {
                ButtonAPI b1 = main.addAreaCheckbox(
                        "Take on Mark", TZEENTCH_MARK_BUTTON, GodEventIntel.GOD_COLORS[1], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b1.getPosition().inTL(800, 150 + 1 * 155);
            }

            if (gods[NURGLE_I].getProgress() == 50) {
                ButtonAPI b2 = main.addAreaCheckbox(
                        "Take on Mark", NURGLE_MARK_BUTTON, GodEventIntel.GOD_COLORS[2], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b2.getPosition().inTL(800, 150 + 2 * 155);
            }

            if (gods[SLAANESH_I].getProgress() == 50) {
                ButtonAPI b3 = main.addAreaCheckbox(
                        "Take on Mark", SLAANESH_MARK_BUTTON, GodEventIntel.GOD_COLORS[3], new Color(40, 40, 40), new Color(240, 240, 240), 120, 30, 0f
                );
                b3.getPosition().inTL(800, 150 + 3 * 155);
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

        Color c = getFactionForUIColors().getBaseUIColor();
        Color bg = getFactionForUIColors().getDarkUIColor();
        mFac.addSectionHeading("Monthly factors", c, bg, Alignment.MID, opad).getPosition().setXAlignOffset(0);

        float strW = 40f;
        float rh = 20f;
        //rh = 15f;
        mFac.beginTable2(getFactionForUIColors(), rh, false, false,
                "Monthly factors", factorWidth - strW - 3,
                "Progress", strW
        );

        for (EventFactor factor : factors) {
            if (factor.isOneTime()) continue;
            if (!factor.shouldShow(this)) continue;

            String desc = factor.getDesc(this);
            if (desc != null) {
                mFac.addRowWithGlow(Alignment.LMID, factor.getDescColor(this), desc,
                        Alignment.RMID, factor.getProgressColor(this), factor.getProgressStr(this));
                TooltipMakerAPI.TooltipCreator t = factor.getMainRowTooltip(this);
                if (t != null) {
                    mFac.addTooltipToAddedRow(t, TooltipMakerAPI.TooltipLocation.RIGHT, false);
                }
            }
            factor.addExtraRows(mFac, this);
        }

        //mFac.addButton("TEST", new String(), factorWidth, 20f, opad);
        mFac.addTable("None", -1, opad);
        mFac.getPrev().getPosition().setXAlignOffset(-5);

        main.endSubTooltip();

        TooltipMakerAPI oFac = main.beginSubTooltip(factorWidth);

        oFac.addSectionHeading("Recent one-time factors", c, bg, Alignment.MID, opad).getPosition().setXAlignOffset(0);

        oFac.beginTable2(getFactionForUIColors(), 20f, false, false,
                "One-time factors", factorWidth - strW - 3,
                "Progress", strW
        );

        List<EventFactor> reversed = new ArrayList<EventFactor>(factors);
        Collections.reverse(reversed);
        for (EventFactor factor : reversed) {
            if (!factor.isOneTime()) continue;
            if (!factor.shouldShow(this)) continue;

            String desc = factor.getDesc(this);
            if (desc != null) {
                oFac.addRowWithGlow(Alignment.LMID, factor.getDescColor(this), desc,
                        Alignment.RMID, factor.getProgressColor(this), factor.getProgressStr(this));
                TooltipMakerAPI.TooltipCreator t = factor.getMainRowTooltip(this);
                if (t != null) {
                    oFac.addTooltipToAddedRow(t, TooltipMakerAPI.TooltipLocation.LEFT);
                }
            }
            factor.addExtraRows(oFac, this);
        }

        oFac.addTable("None", -1, opad);
        oFac.getPrev().getPosition().setXAlignOffset(-5);
        main.endSubTooltip();


        float factorHeight = Math.max(mFac.getHeightSoFar(), oFac.getHeightSoFar());
        mFac.setHeightSoFar(factorHeight);
        oFac.setHeightSoFar(factorHeight);


        if (withMonthlyFactors() && withOneTimeFactors()) {
            main.addCustom(mFac, opad * 2f);
            mFac.getPosition().inTL(10, 775);
            main.addCustomDoNotSetPosition(oFac).getPosition().rightOfTop(mFac, opad);
        } else if (withMonthlyFactors()) {
            main.addCustom(mFac, opad * 2f);
        } else if (withOneTimeFactors()) {
            main.addCustom(oFac, opad * 2f);
        }

        panel.addUIElement(main).inTL(0, 0);

    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId == KHORNE_MARK_BUTTON) {
            setStage(Stage.KHORNE_MARK);
            gods[SLAANESH_I].setProgress(gods[SLAANESH_I].getProgress());
            gods[KHORNE_I].notifyStageReached(gods[KHORNE_I].getLastActiveStage(false));
            ui.updateUIForItem(this);
        }
        if (buttonId == TZEENTCH_MARK_BUTTON) {
            setStage(Stage.TZEENTCH_MARK);
            gods[NURGLE_I].setProgress(gods[NURGLE_I].getProgress());
            gods[TZEENTCH_I].notifyStageReached(gods[TZEENTCH_I].getLastActiveStage(false));
            ui.updateUIForItem(this);
        }
        if (buttonId == NURGLE_MARK_BUTTON) {
            setStage(Stage.NURGLE_MARK);
            gods[TZEENTCH_I].setProgress(gods[TZEENTCH_I].getProgress());
            gods[NURGLE_I].notifyStageReached(gods[NURGLE_I].getLastActiveStage(false));
            ui.updateUIForItem(this);
        }
        if (buttonId == SLAANESH_MARK_BUTTON) {
            setStage(Stage.SLAANESH_MARK);
            gods[KHORNE_I].setProgress(gods[KHORNE_I].getProgress());
            gods[SLAANESH_I].notifyStageReached(gods[SLAANESH_I].getLastActiveStage(false));
            ui.updateUIForItem(this);
        }
        if (buttonId == KHORNE_UNMARK_BUTTON) {
            setStage(Stage.UNALIGNED);
            Global.getSector().getPlayerStats().setSkillLevel("shpe_khorne_chosen", 0);
            gods[KHORNE_I].setProgress(0);
            ui.updateUIForItem(this);
        }
        if (buttonId == TZEENTCH_UNMARK_BUTTON) {
            setStage(Stage.UNALIGNED);
            Global.getSector().getPlayerStats().setSkillLevel("shpe_tzeentch_chosen", 0);
            gods[TZEENTCH_I].setProgress(0);
            ui.updateUIForItem(this);
        }
        if (buttonId == NURGLE_UNMARK_BUTTON) {
            setStage(Stage.UNALIGNED);
            Global.getSector().getPlayerStats().setSkillLevel("shpe_nurgle_chosen", 0);
            gods[NURGLE_I].setProgress(0);
            ui.updateUIForItem(this);
        }
        if (buttonId == SLAANESH_UNMARK_BUTTON) {
            setStage(Stage.UNALIGNED);
            Global.getSector().getPlayerStats().setSkillLevel("shpe_slaanesh_chosen", 0);
            gods[SLAANESH_I].setProgress(0);
            ui.updateUIForItem(this);
        }
    }

    protected EventStageData getStage(Object stageId) {
        for (EventStageData stage : stages) {
            if (stage.id == stageId) {
                return stage;
            }
        }
        return null;
    }

    public void setStage(Stage stageId){
        stageAt = stageId;
        setProgress(getStage(stageId).progress);
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
                            + "Destroy their enemies and embody their ideals to gain their favor."
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
