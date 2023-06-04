package micdoodle8.mods.galacticraft.core.client.gui.screen;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.client.IScreenManager;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.MapUtil;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldProvider;

import org.lwjgl.opengl.GL11;

public class DrawGameScreen extends IScreenManager {

    private static int texCount = 1;

    private float tickDrawn = -1F;
    public boolean initialise = true;
    public boolean initialiseLast = false;
    private boolean readyToInitialise = false;
    private int tileCount = 0;
    private int callCount = 0;
    private int tickMapDone = -1;

    private final float scaleX;
    private final float scaleZ;

    public TileEntity driver;
    public Class<?> telemetryLastClass;
    public String telemetryLastName;
    public Entity telemetryLastEntity;
    public Render telemetryLastRender;
    public static DynamicTexture reusableMap; // = new DynamicTexture(MapUtil.SIZE_STD2, MapUtil.SIZE_STD2);
    public int[] localMap = null;
    public boolean mapDone = false;
    public boolean mapFirstTick = false;

    public DrawGameScreen(float scaleXparam, float scaleZparam, TileEntity te) {
        this.scaleX = scaleXparam;
        this.scaleZ = scaleZparam;
        this.driver = te;
        this.mapFirstTick = true;
    }

    public boolean check(float scaleXparam, float scaleZparam) {
        if (this.mapDone) {
            return this.scaleX == scaleXparam && this.scaleZ == scaleZparam;
        }

        return false;
    }

    private void makeMap() {
        if (this.mapDone || DrawGameScreen.reusableMap == null || this.driver.getWorldObj().provider.dimensionId != 0) {
            return;
        }
        this.localMap = new int[MapUtil.SIZE_STD2 * MapUtil.SIZE_STD2];
        final boolean result = MapUtil
                .getMap(this.localMap, this.driver.getWorldObj(), this.driver.xCoord, this.driver.zCoord);
        if (result) {
            TextureUtil
                    .uploadTexture(reusableMap.getGlTextureId(), this.localMap, MapUtil.SIZE_STD2, MapUtil.SIZE_STD2);
            this.mapDone = true;
            GCLog.debug("Created texture no:" + texCount++);
        }
    }

    public void drawScreen(int type, float ticks, boolean cornerBlock) {
        if (type >= GalacticraftRegistry.getMaxScreenTypes()) {
            System.out.println("Wrong gamescreen type detected - this is a bug." + type);
            return;
        }

        if (cornerBlock) {
            if ((this.mapFirstTick || (int) ticks % 400 == 0) && !this.mapDone && this.tickMapDone != (int) ticks) {
                this.tickMapDone = (int) ticks;
                this.makeMap();
                this.mapFirstTick = false;
            }
            this.doDraw(type, ticks);
            this.initialise = true;
            this.initialiseLast = false;
            return;
        }

        // Performance code: if type > 1 then we only want
        // to draw the screen once per tick, for multi-screens

        // Spend the first tick just initialising the counter
        if (this.initialise) {
            if (!this.initialiseLast) {
                this.tickDrawn = ticks;
                this.readyToInitialise = false;
                this.initialiseLast = true;
                return;
            }

            if (!this.readyToInitialise && ticks == this.tickDrawn) {
                return;
            }

            if (!this.readyToInitialise) {
                this.readyToInitialise = true;
                this.tickDrawn = ticks;
                this.tileCount = 1;
                return;
            }
            if (ticks == this.tickDrawn) {
                this.tileCount++;
                return;
            }
            // Start normal operations
            this.initialise = false;
            this.initialiseLast = false;
            this.readyToInitialise = false;
        }

        if (++this.callCount < this.tileCount) {
            // Normal situation, everything OK
            if (this.callCount == 1 || this.tickDrawn == ticks) {
                this.tickDrawn = ticks;
                return;
            }
            this.initialise = true;
            // but draw this tick [probably a tileEntity moved out of the frustum]
        }

        if (this.callCount == this.tileCount) {
            this.callCount = 0;
            // Again if this is not the tickDrawn then something is wrong, reinitialise
            if (this.tileCount > 1 && ticks != this.tickDrawn) {
                this.initialise = true;
            }
        }

        this.tickDrawn = ticks;

        this.doDraw(type, ticks);
    }

    private void doDraw(int type, float ticks) {
        final float lightMapSaveX = OpenGlHelper.lastBrightnessX;
        final float lightMapSaveY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        if (type > 0) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        GalacticraftRegistry.getGameScreen(type).render(type, ticks, this.scaleX, this.scaleZ, this);

        if (type > 0) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
    }

    @Override
    public WorldProvider getWorldProvider() {
        if (this.driver != null) {
            return this.driver.getWorldObj().provider;
        }

        return null;
    }
}
