package mok_slyk.shpe.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.jetbrains.annotations.Nullable;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.plugins.MagicTrailPlugin;
import org.magiclib.util.MagicTrailObject;
import org.magiclib.util.MagicTrailTracker;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.magiclib.plugins.MagicTrailPlugin.getPlugin;

/*
Sections of the following code are derived from code by Nicke535 that was published as part of MagicLib (1.4.6) under the following license:

MIT License

Copyright 2024 Wispborne

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
public class LanceTrailTracker extends MagicTrailTracker{
    protected List<MagicTrailObject> myAllTrailParts;
    protected static Map<CombatEngineLayers, Map<Integer, Map<Float, MagicTrailTracker>>> mainMap;
    protected static Map<CombatEngineLayers, Map<Integer, Map<CombatEntityAPI, List<Float>>>> cuttingMap;
    protected MagicTrailObject myLatestTrailObject;
    protected float myScrollingTextureOffset = 0f;
    private static MagicTrailPlugin currentPlugin = null;

    public static MagicTrailTracker addOrGetTrailTracker(MagicTrailPlugin plugin, Float ID, CombatEntityAPI linkedEntity, CombatEngineLayers layer, SpriteAPI sprite, boolean isAnim) {

        int texID;
        texID = isAnim ? -1 : sprite.getTextureId();

        updatePluginIfNeeded(plugin);

        Map<Float, MagicTrailTracker> trailTrackerMap;
        Map<Integer, Map<Float, MagicTrailTracker>> layerMap = mainMap.get(layer);
        if (layerMap == null) {
            layerMap = new HashMap<>();
            mainMap.put(layer, layerMap);
        }

        trailTrackerMap = layerMap.get(texID);
        if (trailTrackerMap == null) {
            trailTrackerMap = new HashMap<>();
            layerMap.put(texID, trailTrackerMap);
        }

        MagicTrailTracker trailTracker = trailTrackerMap.get(ID);
        if (trailTracker == null) {
            trailTracker = new LanceTrailTracker();
            trailTrackerMap.put(ID, trailTracker);
        }

        if (linkedEntity != null) {
            Map<Integer, Map<CombatEntityAPI, List<Float>>> layerCutMap = cuttingMap.get(layer);
            if (layerCutMap == null) {
                layerCutMap = new HashMap<>();
                cuttingMap.put(layer, layerCutMap);
            }
            Map<CombatEntityAPI, List<Float>> entityCuttingIDMap = layerCutMap.get(texID);
            if (entityCuttingIDMap == null) {
                entityCuttingIDMap = new HashMap<>();
                layerCutMap.put(texID, entityCuttingIDMap);
            }
            List<Float> cuttingIDs = entityCuttingIDMap.get(linkedEntity);
            if (cuttingIDs == null) {
                cuttingIDs = new ArrayList<>();
                entityCuttingIDMap.put(linkedEntity, cuttingIDs);
            }
            if (!cuttingIDs.contains(ID)) {
                cuttingIDs.add(ID);
            }
        }
        return trailTracker;
    }

    private static void updatePluginIfNeeded(MagicTrailPlugin plugin) {
        if (currentPlugin != plugin) {
            currentPlugin = plugin;
            mainMap = (Map<CombatEngineLayers, Map<Integer, Map<Float, MagicTrailTracker>>>) Witchcraft.getFromFieldInObject(plugin, "mainMap");
            cuttingMap = (Map<CombatEngineLayers, Map<Integer, Map<CombatEntityAPI, List<Float>>>>) Witchcraft.getFromFieldInObject(plugin, "cuttingMap");
        }
    }

    public LanceTrailTracker(){
        super();
        MagicTrailPlugin plugin = getPlugin();
        if (plugin == null) return;
        //mainMap = (Map<CombatEngineLayers, Map<Integer, Map<Float, MagicTrailTracker>>>)Witchcraft.getFromFieldInObject(plugin, "mainMap");
        //cuttingMap = (Map<CombatEngineLayers, Map<Integer, Map<CombatEntityAPI, List<Float>>>>)Witchcraft.getFromFieldInObject(plugin, "cuttingMap");
        myAllTrailParts = (List<MagicTrailObject>) Witchcraft.getFromFieldInObject(this, "allTrailParts");
        myLatestTrailObject = (MagicTrailObject) Witchcraft.getFromFieldInObject(this, "latestTrailObject");

    }

    public static void addTrailMemberAdvanced(
            CombatEntityAPI linkedEntity, float ID, SpriteAPI sprite,
            Vector2f position, float startSpeed, float endSpeed,
            float angle, float startAngularVelocity, float endAngularVelocity,
            float startSize, float endSize,
            Color startColor, Color endColor, float opacity,
            float inDuration, float mainDuration, float outDuration,
            int blendModeSRC, int blendModeDEST,
            float textureLoopLength, float textureScrollSpeed, float textureOffset,
            @Nullable Vector2f offsetVelocity, @Nullable Map<String, Object> advancedOptions,
            @Nullable CombatEngineLayers layerToRenderOn, float frameOffsetMult) {

        //First, find the plugin, and if it doesn't exist do nothing
        MagicTrailPlugin plugin = getPlugin();
        if (plugin == null) return;


        Vector2f offsetVel = new Vector2f();
        if (offsetVelocity != null) {
            offsetVel = offsetVelocity;
        }

        CombatEngineLayers layer = CombatEngineLayers.CONTRAILS_LAYER;
        if (layerToRenderOn != null) {
            layer = layerToRenderOn;
        }

        float mult = 1;
        if (frameOffsetMult != 0) {
            mult = frameOffsetMult;
        }

        //Finds the correct maps, and ensures they are actually instantiated [and adds our ID to the cutting map]
        MagicTrailTracker tracker = addOrGetTrailTracker(plugin, ID, linkedEntity, layer, sprite, false);

        //Adjusts scroll speed to our most recent trail's value
        tracker.scrollSpeed = textureScrollSpeed;

        //--Reads in our special options, if we have any--
        float sizePulseWidth = 0f;
        int sizePulseCount = 0;
        if (advancedOptions != null) {
            if (advancedOptions.get("SIZE_PULSE_WIDTH") instanceof Float) {
                sizePulseWidth = (Float) advancedOptions.get("SIZE_PULSE_WIDTH");
            }
            if (advancedOptions.get("SIZE_PULSE_COUNT") instanceof Integer) {
                sizePulseCount = (Integer) advancedOptions.get("SIZE_PULSE_COUNT");
            }
            if (advancedOptions.get("FORWARD_PROPAGATION") instanceof Boolean && (boolean) advancedOptions.get("FORWARD_PROPAGATION")) {
                tracker.usesForwardPropagation = true;
            }
        }
        //--End of special options--

        //Offset tweaker to fix single frame delay for lateral movement
        Vector2f correctedPosition = new Vector2f(position);
        if (linkedEntity instanceof DamagingProjectileAPI) {
            DamagingProjectileAPI proj = (DamagingProjectileAPI) linkedEntity;

            Vector2f shipVelPerAdvance = (Vector2f) new Vector2f(proj.getSource().getVelocity()).scale(Global.getCombatEngine().getElapsedInLastFrame());
            shipVelPerAdvance.scale(mult);
            Vector2f.sub(position, shipVelPerAdvance, correctedPosition);
        }

        //check for specific texture offset or a random one
        float textOffset = 0;
        if (textureOffset == -1) {
            //the texture tracker keep a fixed random texture offset
            if (tracker.textureOffset == -1) {
                tracker.textureOffset = MathUtils.getRandomNumberInRange(0, textureLoopLength);
            }
            textOffset = tracker.textureOffset;
        } else if (textureOffset != 0) {
            textOffset = textureOffset;
        }

        //Creates the custom object we want
        MagicTrailObject objectToAdd = new MagicTrailObject(inDuration, mainDuration, outDuration, startSize, endSize, startAngularVelocity, endAngularVelocity,
                opacity, blendModeSRC, blendModeDEST, startSpeed, endSpeed, startColor, endColor, angle, correctedPosition, textureLoopLength, textOffset, offsetVel,
                sizePulseWidth, sizePulseCount);

        //And finally add it to the correct location in our maps
        tracker.addNewTrailObject(objectToAdd);
    }

    public void renderTrail(int textureID) {
        //First, clear all dead objects, as they can be a pain to calculate around
//        clearAllDeadObjects();

        //List<MagicTrailObject> allTrailParts = (List<MagicTrailObject>) Witchcraft.getFromFieldInObject(this, "allTrailParts");
        //MagicTrailObject latestTrailObject = (MagicTrailObject) Witchcraft.getFromFieldInObject(this, "latestTrailObject");
        //float scrollingTextureOffset = (float) Witchcraft.getFromFieldInObject(this, "scrollingTextureOffset");

        //Then, if we have too few segments to render properly, cancel the function
        int size = myAllTrailParts.size();
        if (size <= 1) {
            return;
        }

        //New trail object's movement
        MagicTrailObject currentLatestTrailObject = myAllTrailParts.get(size - 1);
        if (myLatestTrailObject == null) {
            myLatestTrailObject = currentLatestTrailObject;
        } else if (myLatestTrailObject != currentLatestTrailObject) {
            float partDistance = MathUtils.getDistance(myLatestTrailObject.currentLocation, currentLatestTrailObject.currentLocation);
            //scroll back
            myScrollingTextureOffset -= partDistance / currentLatestTrailObject.textureLoopLength;
            myLatestTrailObject = currentLatestTrailObject;
        }

        //If we are animated, we use our "currentAnimRenderTexture" rather than the textureID we just got supplied
        int trueTextureID = textureID;
        if (isAnimated) {
            trueTextureID = currentAnimRenderTexture;
        }

        //Otherwise, we actually render the thing
        //This part instantiates OpenGL

        glPushMatrix();
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, trueTextureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glEnable(GL_BLEND);
        glBlendFunc(myAllTrailParts.get(size - 1).blendModeSRC, myAllTrailParts.get(size - 1).blendModeDEST); //NOTE: uses the most recent blend mode added to the trail
        glBegin(GL_QUADS);

        CombatEngineAPI engine = Global.getCombatEngine();


        //Iterate through all trail parts except the oldest one: the idea is that each part renders in relation to the *previous* part
        //Note that this behaviour is inverted compared to forward-propagating render (the old method)
        float texDistTracker = currentLatestTrailObject.textureOffset;
        float texLocator = 0f;
        for (int i = size - 1; i > 0; i--) {
            //First, get a handle for our parts so we can make the code shorter
            MagicTrailObject part1 = myAllTrailParts.get(i);    //Current part
            MagicTrailObject part2 = myAllTrailParts.get(i - 1);    //Next part

            //Then, determine the corner points of both this and the next trail part
            float partRadius = part1.currentSize * 0.5f;
            Vector2f point1Left = MathUtils.getPointOnCircumference(part1.currentLocation, partRadius, part1.angle - 90f);
            Vector2f point1Right = MathUtils.getPointOnCircumference(part1.currentLocation, partRadius, part1.angle + 90f);
            partRadius = part2.currentSize * 0.5f;
            Vector2f point2Left = MathUtils.getPointOnCircumference(part2.currentLocation, partRadius, part2.angle - 90f);
            Vector2f point2Right = MathUtils.getPointOnCircumference(part2.currentLocation, partRadius, part2.angle + 90f);

            //Saves an easy value for the distance between the current two parts
            float partDistance = MathUtils.getDistance(part1.currentLocation, part2.currentLocation);

            //-------------------------------------------------------------------Actual rendering shenanigans------------------------------------------------------------------------------------------
            //If we are outside the viewport, don't render at all! Just tick along our texture tracker, and do nothing else
            if (!engine.getViewport().isNearViewport(part1.currentLocation, partDistance * 2f)) {
                //Change our texture distance tracker depending on looping mode
                //  -If we have -1 as loop length, we ensure that the entire texture is used over the entire trail
                //  -Otherwise, we adjust the texture distance upward to account for how much distance there is between our two points
                if (part1.textureLoopLength <= 0f) {
                    texDistTracker = (float) (i - 1) / (float) size;
                } else {
                    texDistTracker += partDistance / part1.textureLoopLength;
                }

                continue;
            }

            //Changes opacity slightly at beginning and end: the last and first 2 segments have lower opacity
            float opacityMult = 1f;
            if (i < 2) {
                opacityMult *= ((float) i / 2f);
            } else if (i > size - 3) {
                opacityMult *= ((float) size - 1f - (float) i) / 2f;
            }

            //Sets the current render color
            glColor4ub((byte) part1.currentColor.getRed(), (byte) part1.currentColor.getGreen(), (byte) part1.currentColor.getBlue(), (byte) (part1.currentOpacity * opacityMult * 255));

            texLocator = texDistTracker + myScrollingTextureOffset;

            //Sets corner 1, or the first left corner
            glTexCoord4f(0f, texLocator, 0f, 1f);
            glVertex2f(point1Left.getX(), point1Left.getY());

            //Sets corner 2, or the first right corner
            glTexCoord4f(1f, texLocator, 0f, 1f);
            glVertex2f(point1Right.getX(), point1Right.getY());

            //Change our texture distance tracker depending on looping mode
            //  -If we have -1 as loop length, we ensure that the entire texture is used over the entire trail
            //  -Otherwise, we adjust the texture distance upward to account for how much distance there is between our two points
            if (part1.textureLoopLength <= 0f) {
                texDistTracker = (float) (i - 1) / (float) size;
            } else {
                texDistTracker += partDistance / part1.textureLoopLength;
            }

            //Changes opacity slightly at beginning and end: the last and first 2 segments have lower opacity
            opacityMult = 1f;
            if ((i - 1) < 2) {
                opacityMult *= ((float) (i - 1) / 2f);
            } else if ((i - 1) > size - 3) {
                opacityMult *= ((float) size - 1f - ((float) i - 1f)) / 2f;
            }

            //Changes render color to our next segment's opacity
            glColor4ub((byte) part2.currentColor.getRed(),
                    (byte) part2.currentColor.getGreen(),
                    (byte) part2.currentColor.getBlue(),
                    (byte) (part2.currentOpacity * opacityMult * 255));

            texLocator = texDistTracker + myScrollingTextureOffset;

            // Calculate widths
            float bottomWidth = point1Right.getX() - point1Left.getX();
            float topWidth = point2Right.getX() - point2Left.getX();

            // Calculate q offset
            //float texOffset = (1.0f - (topWidth / bottomWidth))/2;
            float qTop = topWidth/bottomWidth;

            //Sets corner 3, or the second right corner
            glTexCoord4f(1f*qTop, texLocator*qTop, 0f, qTop);
            glVertex2f(point2Right.getX(), point2Right.getY());

            //Sets corner 4, or the second left corner
            glTexCoord4f(0f, texLocator*qTop, 0f, qTop);
            glVertex2f(point2Left.getX(), point2Left.getY());
        }


        //And finally stops OpenGL
        glEnd();
        glPopMatrix();
    }

    @Override
    public void tickTimersInTrail(float amount) {
        if (!isExpired()) {
            if (myAllTrailParts.isEmpty()) {
                myLatestTrailObject = null;
            }
            myScrollingTextureOffset -= (amount * scrollSpeed) / 1000f;
        }
        super.tickTimersInTrail(amount);
    }
}
