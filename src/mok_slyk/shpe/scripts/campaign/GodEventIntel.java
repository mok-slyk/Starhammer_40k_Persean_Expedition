package mok_slyk.shpe.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

// Event for individual favor and progress bar
public class GodEventIntel extends BaseEventIntel {
    public static int PROGRESS_MAX = 100;

    public static Color[] GOD_COLORS = {
            new Color(180, 5, 18),
            new Color(36, 115, 189),
            new Color(115, 190, 104),
            new Color(135, 50, 162)
    };
    protected Color barColor;
    protected String key;
    protected String eventName;

    public ChaosGodsEventIntel masterEvent = null;

    public static enum Stage {
        START,
        MARK,
        DEVOTION,
        GIFT_1,
        GIFT_2,
        GIFT_3,
        GIFT_4
    }

    public int godID;

    public GodEventIntel(int godID, String name, String key, Color barColor) {
        super();
        this.godID = godID;
        this.eventName = name;
        this.key = key;
        this.barColor = barColor;

        Global.getSector().getMemoryWithoutUpdate().set(key, this);

        setup();
        setProgress(20);
    }

    protected void setup() {
        factors.clear();
        stages.clear();

        setMaxProgress(PROGRESS_MAX);

        addStage(Stage.START,0);
        addStage(Stage.MARK,30);
        addStage(Stage.DEVOTION, 50, StageIconSize.LARGE);
        addStage(Stage.GIFT_1,60);
        addStage(Stage.GIFT_2,70);
        addStage(Stage.GIFT_3,80);
        addStage(Stage.GIFT_4,90);

        getDataFor(Stage.MARK).keepIconBrightWhenLaterStageReached = true;
        getDataFor(Stage.DEVOTION).keepIconBrightWhenLaterStageReached = true;
        getDataFor(Stage.GIFT_1).keepIconBrightWhenLaterStageReached = true;
        getDataFor(Stage.GIFT_2).keepIconBrightWhenLaterStageReached = true;
        getDataFor(Stage.GIFT_3).keepIconBrightWhenLaterStageReached = true;
        getDataFor(Stage.GIFT_4).keepIconBrightWhenLaterStageReached = true;

    }

    public Color getBarColor() {
        return barColor;
    }

    public TooltipMakerAPI.TooltipCreator getStageTooltipImpl(Object stageId) {
        final EventStageData esd = getDataFor(stageId);
        String title = "";
        switch (godID){
            case ChaosGodsEventIntel.KHORNE_I:
                if (esd.id == Stage.MARK) title = "Mark of Khorne"; //hullmod: reduced range, increased damage
                if (esd.id == Stage.DEVOTION) title = "Devotion to Khorne"; //skill: piloted ship: damage dealt increases damage for this combat to a maximum of +100%
                if (esd.id == Stage.GIFT_1) title = "Furious Vengeance"; //buff to mark: effect doubled when below 60% hull
                if (esd.id == Stage.GIFT_2) title = "World Eaters Arsenal"; //ursus claw and butcher cannon
                if (esd.id == Stage.GIFT_3) title = "Demonships of Khorne";
                if (esd.id == Stage.GIFT_4) title = "Exalted Champion of Khorne"; //player skill: piloted ship (and all ships with reckless officer at 50%):
                break;
            case ChaosGodsEventIntel.TZEENTCH_I:
                if (esd.id == Stage.MARK) title = "Mark of Tzeentch"; //hullmod: reduced armor, increased range and accuracy
                if (esd.id == Stage.DEVOTION) title = "Devotion to Tzeentch"; //skill: all ships: +10% fighter and energy damage
                if (esd.id == Stage.GIFT_1) title = "Strands of Fortune"; //hullmod
                if (esd.id == Stage.GIFT_2) title = "Flaming Terrors"; //apis, lotus for all ships; doomwing, firelord for marked ships only
                if (esd.id == Stage.GIFT_3) title = "Demonships of Tzeentch";
                if (esd.id == Stage.GIFT_4) title = "Warp Beasts"; //hullmod: ship fires random high-damage bursts of flame at random enemies, might get screamers?
                break;
            case ChaosGodsEventIntel.NURGLE_I:
                if (esd.id == Stage.MARK) title = "Mark of Nurgle"; //hullmod: reduced speed, hull/armor buff, resistance to malfunctions
                if (esd.id == Stage.DEVOTION) title = "Devotion to Nurgle: "; //skill: regen more hull the lower you get
                if (esd.id == Stage.GIFT_1) title = "Miasma"; //hullmod: lessens detection range, -5% damage taken
                if (esd.id == Stage.GIFT_2) title = "Diseased Weaponry"; //plague bombardment surface abillity + hives of nurgle macrobattery hullmod
                if (esd.id == Stage.GIFT_3) title = "Demonships of Nurgle";
                if (esd.id == Stage.GIFT_4) title = "Ark of Pestilence"; //hullmod: ship infects close enemies and damages them
                break;
            case ChaosGodsEventIntel.SLAANESH_I:
                if (esd.id == Stage.MARK) title = "Mark of Slaanesh"; //hullmod: reduced hull, increased movement speed and fire rate
                if (esd.id == Stage.DEVOTION) title = "Devotion to Slaanesh"; //skill: all ships:
                if (esd.id == Stage.GIFT_1) title = "Siren's Summon"; //hullmod: slows close enemies
                if (esd.id == Stage.GIFT_2) title = "Excessive Warfare"; //bloodlust surface abillity + harvester cannon
                if (esd.id == Stage.GIFT_3) title = "Demonships of Slaanesh";
                if (esd.id == Stage.GIFT_4) title = "Veil of Lust"; //hullmod: debuffs ships for a while after you come close to them: -accuray, -damage
                break;
        }
        final String finalTitle = title;
        return new TooltipMakerAPI.TooltipCreator() {
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }
            public float getTooltipWidth(Object tooltipParam) {
                return BaseEventFactor.TOOLTIP_WIDTH;
            }

            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                float opad = 10f;
                Color h = Misc.getHighlightColor();

                if (finalTitle.isEmpty()) {
                    tooltip.addPara("Something might occur when event progress reaches %s points. "
                                    + "What that is, if anything, will be determined when event progress reaches "
                                    + "%s points.", 0f,
                            h, "" + esd.progress, "" + esd.progressToRollAt);

                    if (esd.isRepeatable) {
                        tooltip.addPara("This event is repeatable.", opad);
                    } else {
                        tooltip.addPara("This event is not repeatable.", opad);
                    }
                } else {
                    tooltip.addTitle(finalTitle);

                    addStageDesc(tooltip, esd.id, opad);

                    esd.addProgressReq(tooltip, opad);
                }
            }
        };
    }

    private void addStageDesc(TooltipMakerAPI info, Object stageID, float initPad) {
        float opad = 10f;
        Color h = GOD_COLORS[godID];
        switch (godID) {
            case ChaosGodsEventIntel.KHORNE_I:
                if (stageID == Stage.MARK) {
                    info.addPara("You have gained enough favor with the god of slaughter to be allowed to dedicate your ships to him. "
                                    + "You gain access to the %s hullmod."
                            , opad
                            , h
                            , "Mark of Khorne");
                }
                if (stageID == Stage.DEVOTION) {
                    info.addPara("To earn further favor with Khorne you need to fully devote yourself to him. "
                                    + "Take on his mark to gain the %s skill."
                            , opad
                            , h
                            , "Chosen of Khorne");
                }
                break;
            case ChaosGodsEventIntel.TZEENTCH_I:
                if (stageID == Stage.MARK) {
                    info.addPara("You have gained enough favor with the god of change to be allowed to dedicate your ships to him. "
                                    + "You gain access to the %s hullmod."
                            , opad
                            , h
                            , "Mark of Tzeentch");
                }
                if (stageID == Stage.DEVOTION) {
                    info.addPara("To earn further favor with Tzeentch you need to fully devote yourself to him. "
                                    + "Take on his mark to gain the %s skill."
                            , opad
                            , h
                            , "Chosen of Tzeentch");
                }
                break;
            case ChaosGodsEventIntel.NURGLE_I:
                if (stageID == Stage.MARK) {
                    info.addPara("You have gained enough favor with the god of decay to be allowed to dedicate your ships to him. "
                                    + "You gain access to the %s hullmod."
                            , opad
                            , h
                            , "Mark of Nurgle");
                }
                if (stageID == Stage.DEVOTION) {
                    info.addPara("To earn further favor with Nurgle you need to fully devote yourself to him. "
                                    + "Take on his mark to gain the %s skill."
                            , opad
                            , h
                            , "Chosen of Nurgle");
                }
                break;
            case ChaosGodsEventIntel.SLAANESH_I:
                if (stageID == Stage.MARK) {
                    info.addPara("You have gained enough favor with the god of excess to be allowed to dedicate your ships to her. "
                                    + "You gain access to the %s hullmod."
                            , opad
                            , h
                            , "Mark of Slaanesh");
                }
                if (stageID == Stage.DEVOTION) {
                    info.addPara("To earn further favor with Slaanesh you need to fully devote yourself to him. "
                                    + "Take on his mark to gain the %s skill."
                            , opad
                            , h
                            , "Chosen of Slaanesh");
                }
                break;
        }
    }

    @Override
    protected String getStageIconImpl(Object stageId) {
        EventStageData esd = getDataFor(stageId);
        switch (godID){
            case ChaosGodsEventIntel.KHORNE_I:
                if (esd.id == Stage.MARK) return Global.getSettings().getSpriteName("icons", "khorne_gold");
                if (esd.id == Stage.DEVOTION) return Global.getSettings().getSpriteName("icons", "khorne_glow");
                if (esd.id == Stage.GIFT_1) return Global.getSettings().getSpriteName("icons", "khorne_vengeance");
                if (esd.id == Stage.GIFT_2) return Global.getSettings().getSpriteName("icons", "khorne_arsenal");
                if (esd.id == Stage.GIFT_3) return Global.getSettings().getSpriteName("icons", "khorne_demonships");
                if (esd.id == Stage.GIFT_4) return Global.getSettings().getSpriteName("icons", "khorne_champion");
                return Global.getSettings().getSpriteName("icons", "chaos_gold");
            case ChaosGodsEventIntel.TZEENTCH_I:
                if (esd.id == Stage.MARK) return Global.getSettings().getSpriteName("icons", "tzeentch_gold");
                if (esd.id == Stage.DEVOTION) return Global.getSettings().getSpriteName("icons", "tzeentch_glow");
                if (esd.id == Stage.GIFT_3) return Global.getSettings().getSpriteName("icons", "tzeentch_demonships");
                return Global.getSettings().getSpriteName("icons", "chaos_gold");
            case ChaosGodsEventIntel.NURGLE_I:
                if (esd.id == Stage.MARK) return Global.getSettings().getSpriteName("icons", "nurgle_gold");
                if (esd.id == Stage.DEVOTION) return Global.getSettings().getSpriteName("icons", "nurgle_glow");
                if (esd.id == Stage.GIFT_3) return Global.getSettings().getSpriteName("icons", "nurgle_demonships");
                return Global.getSettings().getSpriteName("icons", "chaos_gold");
            case ChaosGodsEventIntel.SLAANESH_I:
                if (esd.id == Stage.MARK) return Global.getSettings().getSpriteName("icons", "slaanesh_gold");
                if (esd.id == Stage.DEVOTION) return Global.getSettings().getSpriteName("icons", "slaanesh_glow");
                if (esd.id == Stage.GIFT_3) return Global.getSettings().getSpriteName("icons", "slaanesh_demonships");
                return Global.getSettings().getSpriteName("icons", "chaos_gold");
        }
        return null;
    }

    @Override
    public void setProgress(int progress) {
        //if (this.progress == progress) return;
        int actualMaxProgress = maxProgress;
        if (masterEvent != null) {
            switch (godID) {
                case ChaosGodsEventIntel.KHORNE_I:
                    if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.KHORNE_MARK) {
                        actualMaxProgress = 100;
                    } else if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.SLAANESH_MARK) {
                        actualMaxProgress = 25;
                    } else {
                        actualMaxProgress = 50;
                    }
                    break;
                case ChaosGodsEventIntel.TZEENTCH_I:
                    if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.TZEENTCH_MARK) {
                        actualMaxProgress = 100;
                    } else if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.NURGLE_MARK) {
                        actualMaxProgress = 25;
                    } else {
                        actualMaxProgress = 50;
                    }
                    break;
                case ChaosGodsEventIntel.NURGLE_I:
                    if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.NURGLE_MARK) {
                        actualMaxProgress = 100;
                    } else if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.TZEENTCH_MARK) {
                        actualMaxProgress = 25;
                    } else {
                        actualMaxProgress = 50;
                    }
                    break;
                case ChaosGodsEventIntel.SLAANESH_I:
                    if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.SLAANESH_MARK) {
                        actualMaxProgress = 100;
                    } else if (masterEvent.stageAt == ChaosGodsEventIntel.Stage.KHORNE_MARK) {
                        actualMaxProgress = 25;
                    } else {
                        actualMaxProgress = 50;
                    }
                    break;
            }
        }

        if (progress < 0) progress = 0;
        if (progress > actualMaxProgress) progress = actualMaxProgress;

        EventStageData prev = getLastActiveStage(true);
        prevProgressDeltaWasPositive = this.progress < progress;

        //progress += 30;
        //progress = 40;
        //progress = 40;
        //progress = 499;

        this.progress = progress;


        if (progress < 0) {
            progress = 0;
        }
        if (progress > getMaxProgress()) {
            progress = getMaxProgress();
        }

        // Check to see if randomized events need to be rolled/reset
        for (EventStageData esd : getStages()) {
            if (esd.wasEverReached && esd.isOneOffEvent && !esd.isRepeatable) continue;

            if (esd.randomized) {
                if ((esd.rollData != null && !esd.rollData.equals(RANDOM_EVENT_NONE)) && progress <= esd.progressToResetAt) {
                    resetRandomizedStage(esd);
                }
                if ((esd.rollData == null || esd.rollData.equals(RANDOM_EVENT_NONE)) && progress >= esd.progressToRollAt) {
                    rollRandomizedStage(esd);
                    if (esd.rollData == null) {
                        esd.rollData = RANDOM_EVENT_NONE;
                    }
                }
            }
        }

        // go through all of the stages made active by the new progress value
        // generally this'd just be one stage, but possible to have multiple for a large
        // progress increase
        for (EventStageData curr : getStages()) {
            if (curr.progress <= prev.progress && !prev.wasEverReached &&
                    (prev.rollData == null || prev.rollData.equals(RANDOM_EVENT_NONE))) continue;
            //if (curr.progress > progress) continue;

            // reached
            if (curr.progress <= progress) {
                boolean laterThanPrev = prev == null || ((Enum)prev.id).ordinal() < ((Enum)curr.id).ordinal();
                if (curr != null && (laterThanPrev || !prev.wasEverReached)) {
                    if (curr.sendIntelUpdateOnReaching && curr.progress > 0 && (prev == null || prev.progress < curr.progress)) {
                        sendUpdateIfPlayerHasIntel(curr, getTextPanelForStageChange());
                    }
                    notifyStageReached(curr);
                    curr.rollData = null;
                    curr.wasEverReached = true;

                    progress = getProgress(); // in case it was changed by notifyStageReached()
                }
            }
        }
    }
}
