package micdoodle8.mods.galacticraft.core.client.gui.screen;

import java.nio.DoubleBuffer;

import micdoodle8.mods.galacticraft.api.client.IGameScreen;
import micdoodle8.mods.galacticraft.api.client.IScreenManager;
import micdoodle8.mods.galacticraft.api.entity.ITelemetry;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GameScreenText implements IGameScreen {

    private float frameA;
    private float frameBx;
    private float frameBy;
    private int yPos;
    private DoubleBuffer planes;

    public GameScreenText() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.planes = BufferUtils.createDoubleBuffer(4 * Double.SIZE);
        }
    }

    @Override
    public void setFrameSize(float frameSize) {
        this.frameA = frameSize;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(int type, float ticks, float sizeX, float sizeY, IScreenManager scr) {
        final DrawGameScreen screen = (DrawGameScreen) scr;

        this.frameBx = sizeX - this.frameA;
        this.frameBy = sizeY - this.frameA;
        this.drawBlackBackground(0.0F);
        this.planeEquation(this.frameA, this.frameA, 0, this.frameA, this.frameBy, 0, this.frameA, this.frameBy, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, this.planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE0);
        this.planeEquation(this.frameBx, this.frameBy, 0, this.frameBx, this.frameA, 0, this.frameBx, this.frameA, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, this.planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE1);
        this.planeEquation(this.frameA, this.frameBy, 0, this.frameBx, this.frameBy, 0, this.frameBx, this.frameBy, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE2, this.planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE2);
        this.planeEquation(this.frameBx, this.frameA, 0, this.frameA, this.frameA, 0, this.frameA, this.frameA, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE3, this.planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE3);
        this.yPos = 0;

        final TileEntityTelemetry telemeter = TileEntityTelemetry.getNearest(screen.driver);
        // Make the text to draw. To look good it's important the width and height
        // of the whole text box are correctly set here.
        String strName = "";
        final String[] str = { GCCoreUtil.translate("gui.display.nolink"), "", "", "", "" };
        Render renderEntity = null;
        Entity entity = null;
        float Xmargin = 0;

        if (telemeter != null && telemeter.clientData.length >= 3) {
            if (telemeter.clientClass != null) {
                if (telemeter.clientClass == screen.telemetryLastClass && (telemeter.clientClass != EntityPlayerMP.class
                        || telemeter.clientName.equals(screen.telemetryLastName))) {
                    // Used cached data from last time if possible
                    entity = screen.telemetryLastEntity;
                    renderEntity = screen.telemetryLastRender;
                    strName = screen.telemetryLastName;
                } else {
                    // Create an entity to render, based on class, and get its name
                    entity = null;

                    if (telemeter.clientClass == EntityPlayerMP.class) {
                        strName = telemeter.clientName;
                        entity = new EntityOtherPlayerMP(screen.driver.getWorldObj(), telemeter.clientGameProfile);
                        renderEntity = (Render) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
                    } else {
                        try {
                            entity = telemeter.clientClass.getConstructor(World.class)
                                    .newInstance(screen.driver.getWorldObj());
                        } catch (final Exception ex) {}
                        if (entity != null) {
                            strName = entity.getCommandSenderName();
                        }
                        renderEntity = (Render) RenderManager.instance.entityRenderMap.get(telemeter.clientClass);
                    }
                }

                // Setup special visual types from data sent by Telemetry
                if (entity instanceof EntityHorse) {
                    ((EntityHorse) entity).setHorseType(telemeter.clientData[3]);
                    ((EntityHorse) entity).setHorseVariant(telemeter.clientData[4]);
                }
                if (entity instanceof EntityVillager) {
                    ((EntityVillager) entity).setProfession(telemeter.clientData[3]);
                    ((EntityVillager) entity).setGrowingAge(telemeter.clientData[4]);
                } else if (entity instanceof EntityWolf) {
                    ((EntityWolf) entity).setCollarColor(telemeter.clientData[3]);
                    ((EntityWolf) entity).func_70918_i(telemeter.clientData[4] == 1);
                } else if (entity instanceof EntitySheep) {
                    ((EntitySheep) entity).setFleeceColor(telemeter.clientData[3]);
                    ((EntitySheep) entity).setSheared(telemeter.clientData[4] == 1);
                } else if (entity instanceof EntityOcelot) {
                    ((EntityOcelot) entity).setTameSkin(telemeter.clientData[3]);
                } else if (entity instanceof EntitySkeleton) {
                    ((EntitySkeleton) entity).setSkeletonType(telemeter.clientData[3]);
                } else if (entity instanceof EntityZombie) {
                    ((EntityZombie) entity).setVillager(telemeter.clientData[3] == 1);
                    ((EntityZombie) entity).setChild(telemeter.clientData[4] == 1);
                }
            }

            if (entity instanceof ITelemetry) {
                ((ITelemetry) entity).receiveData(telemeter.clientData, str);
            } else if (entity instanceof EntityLivingBase) {
                // Living entity:
                // data0 = time to show red damage
                // data1 = health in half-hearts
                // data2 = pulse
                // data3 = hunger (for player); horsetype (for horse)
                // data4 = oxygen (for player); horsevariant (for horse)
                str[0] = telemeter.clientData[0] > 0 ? GCCoreUtil.translate("gui.player.ouch") : "";
                if (telemeter.clientData[1] >= 0) {
                    str[1] = GCCoreUtil.translate("gui.player.health") + ": " + telemeter.clientData[1] + "%";
                } else {
                    str[1] = "";
                }
                str[2] = "" + telemeter.clientData[2] + " " + GCCoreUtil.translate("gui.player.bpm");
                if (telemeter.clientData[3] > -1) {
                    str[3] = GCCoreUtil.translate("gui.player.food") + ": " + telemeter.clientData[3] + "%";
                }
                if (telemeter.clientData[4] > -1) {
                    int oxygen = telemeter.clientData[4];
                    oxygen = oxygen % 4096 + oxygen / 4096;
                    if (oxygen == 180 || oxygen == 90) {
                        str[4] = GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": OK";
                    } else {
                        str[4] = GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": "
                                + this.makeOxygenString(oxygen)
                                + GCCoreUtil.translate("gui.seconds");
                    }
                }
            } else
                // Generic - could be boats or minecarts etc - just show the speed
                // TODO can add more here, e.g. position data?
                if (telemeter.clientData[2] >= 0) {
                    str[2] = makeSpeedString(telemeter.clientData[2]);
                }
        } else {
            // Default - draw a simple time display just to show the Display Screen is
            // working
            final World w1 = screen.driver.getWorldObj();
            final int time1 = w1 != null ? (int) ((w1.getWorldTime() + 6000L) % 24000L) : 0;
            str[2] = this.makeTimeString(time1 * 360);
        }

        final int textWidthPixels = 155;
        int textHeightPixels = 60; // 1 lines
        if (str[3].isEmpty()) {
            textHeightPixels -= 10;
        }
        if (str[4].isEmpty()) {
            textHeightPixels -= 10;
        }

        // First pass - approximate border size
        float borders = this.frameA * 2 + 0.05F * Math.min(sizeX, sizeY);
        float scaleXTest = (sizeX - borders) / textWidthPixels;
        float scaleYTest = (sizeY - borders) / textHeightPixels;
        float scale = sizeX;
        if (scaleYTest < scaleXTest) {
            scale = sizeY;
        }
        // Second pass - the border size may be more accurate now
        borders = this.frameA * 2 + 0.05F * scale;
        scaleXTest = (sizeX - borders) / textWidthPixels;
        scaleYTest = (sizeY - borders) / textHeightPixels;
        scale = sizeX;
        float scaleText = scaleXTest;
        if (scaleYTest < scaleXTest) {
            scale = sizeY;
            scaleText = scaleYTest;
        }

        // Centre the text in the display
        final float border = this.frameA + 0.025F * scale;
        if (entity != null && renderEntity != null) {
            Xmargin = (sizeX - borders) / 2;
        }
        final float Xoffset = (sizeX - borders - textWidthPixels * scaleText) / 2 + Xmargin;
        final float Yoffset = (sizeY - borders - textHeightPixels * scaleText) / 2 + scaleText;
        GL11.glTranslatef(border + Xoffset, border + Yoffset, 0.0F);
        GL11.glScalef(scaleText, scaleText, 1.0F);

        // Actually draw the text
        final int whiteColour = ColorUtil.to32BitColor(255, 240, 216, 255);
        this.drawText(strName, whiteColour);
        this.drawText(str[0], whiteColour);
        this.drawText(str[1], whiteColour);
        this.drawText(str[2], whiteColour);
        this.drawText(str[3], whiteColour);
        this.drawText(str[4], whiteColour);

        // If there is an entity to render, draw it on the left of the text
        if (renderEntity != null && entity != null) {
            GL11.glTranslatef(
                    -Xmargin / 2 / scaleText,
                    textHeightPixels / 2 + (-Yoffset + (sizeY - borders) / 2) / scaleText,
                    -0.0005F);
            final float scalefactor = 38F / (float) Math.pow(Math.max(entity.height, entity.width), 0.65);
            GL11.glScalef(scalefactor, scalefactor, 0.0015F);
            GL11.glRotatef(180F, 0, 0, 1);
            GL11.glRotatef(180F, 0, 1, 0);
            if (entity instanceof ITelemetry) {
                ((ITelemetry) entity).adjustDisplay(telemeter.clientData);
            }
            RenderPlayerGC.flagThermalOverride = true;
            renderEntity.doRender(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
            RenderPlayerGC.flagThermalOverride = false;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        // TODO Cross-dimensional tracking (i.e. old entity setDead, new entity created)
        // TODO Deal with text off screen (including where localizations longer than
        // English)

        screen.telemetryLastClass = telemeter == null ? null : telemeter.clientClass;
        screen.telemetryLastEntity = entity;
        screen.telemetryLastRender = renderEntity;
        screen.telemetryLastName = strName;
        GL11.glDisable(GL11.GL_CLIP_PLANE3);
        GL11.glDisable(GL11.GL_CLIP_PLANE2);
        GL11.glDisable(GL11.GL_CLIP_PLANE1);
        GL11.glDisable(GL11.GL_CLIP_PLANE0);
    }

    private String makeTimeString(int l) {
        final int hrs = l / 360000;
        final int mins = l / 6000 - hrs * 60;
        final int secs = l / 100 - hrs * 3600 - mins * 60;
        final String hrsStr = hrs > 9 ? "" + hrs : "0" + hrs;
        final String minsStr = mins > 9 ? "" + mins : "0" + mins;
        final String secsStr = secs > 9 ? "" + secs : "0" + secs;
        return hrsStr + ":" + minsStr + ":" + secsStr;
    }

    public static String makeSpeedString(int speed100) {
        final int sp1 = speed100 / 100;
        final int sp2 = speed100 % 100;
        final String spstr1 = GCCoreUtil.translate("gui.rocket.speed") + ": " + sp1;
        final String spstr2 = (sp2 > 9 ? "" : "0") + sp2;
        return spstr1 + "." + spstr2 + " " + GCCoreUtil.translate("gui.lander.velocityu");
    }

    private String makeOxygenString(int oxygen) {
        // Server takes 1 air away every 9 ticks (OxygenUtil.getDrainSpacing)
        final int sp1 = oxygen * 9 / 20;
        final int sp2 = oxygen * 9 % 20 / 2;
        final String spstr1 = "" + sp1;
        final String spstr2 = "" + sp2;
        return spstr1 + "." + spstr2;
    }

    private void drawText(String str, int colour) {
        Minecraft.getMinecraft().fontRenderer.drawString(str, 0, this.yPos, colour, false);
        this.yPos += 10;
    }

    private void drawBlackBackground(float greyLevel) {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(greyLevel, greyLevel, greyLevel, 1.0F);
        tess.startDrawingQuads();

        tess.addVertex(this.frameA, this.frameBy, 0.005F);
        tess.addVertex(this.frameBx, this.frameBy, 0.005F);
        tess.addVertex(this.frameBx, this.frameA, 0.005F);
        tess.addVertex(this.frameA, this.frameA, 0.005F);
        tess.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void planeEquation(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3,
            float z3) {
        final double[] result = new double[4];
        result[0] = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
        result[1] = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
        result[2] = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        result[3] = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1));
        this.planes.put(result, 0, 4);
        this.planes.position(0);
    }
}
