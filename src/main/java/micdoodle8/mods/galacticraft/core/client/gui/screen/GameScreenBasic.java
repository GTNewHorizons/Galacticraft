package micdoodle8.mods.galacticraft.core.client.gui.screen;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import micdoodle8.mods.galacticraft.api.client.IGameScreen;
import micdoodle8.mods.galacticraft.api.client.IScreenManager;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.render.RenderPlanet;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class GameScreenBasic implements IGameScreen {

    private TextureManager renderEngine;

    private float frameA;
    private float frameBx;
    private float frameBy;
    private float textureAx = 0F;
    private float textureAy = 0F;
    private float textureBx = 1.0F;
    private float textureBy = 1.0F;

    public GameScreenBasic() {
        // This can be called from either server or client, so don't include
        // client-side only code on the server.
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.renderEngine = FMLClientHandler.instance().getClient().renderEngine;
        }
    }

    @Override
    public void setFrameSize(float frameSize) {
        this.frameA = frameSize;
    }

    @Override
    public void render(int type, float ticks, float scaleX, float scaleY, IScreenManager scr) {
        this.frameBx = scaleX - this.frameA;
        this.frameBy = scaleY - this.frameA;

        if (scaleX == scaleY) {
            this.textureAx = 0F;
            this.textureAy = 0F;
            this.textureBx = 1.0F;
            this.textureBy = 1.0F;
        } else if (scaleX < scaleY) {
            this.textureAx = (1.0F - scaleX / scaleY) / 2;
            this.textureAy = 0F;
            this.textureBx = 1.0F - this.textureAx;
            this.textureBy = 1.0F;
        } else if (scaleY < scaleX) {
            this.textureAx = 0F;
            this.textureAy = (1.0F - scaleY / scaleX) / 2;
            this.textureBx = 1.0F;
            this.textureBy = 1.0F - this.textureAy;
        }

        switch (type) {
            case 0:
                this.drawBlackBackground(0.09F);
                // ClientProxyCore.overworldTextureLocal = null;
                break;
            case 1:
                if (scr instanceof DrawGameScreen && ((DrawGameScreen) scr).mapDone) {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, DrawGameScreen.reusableMap.getGlTextureId());
                    this.draw2DTexture();
                } else if (ClientProxyCore.overworldTexturesValid) {
                    GL11.glPushMatrix();
                    final float centreX = scaleX / 2;
                    final float centreY = scaleY / 2;
                    GL11.glTranslatef(centreX, centreY, 0F);
                    RenderPlanet.renderPlanet(
                            ClientProxyCore.overworldTextureWide.getGlTextureId(),
                            Math.min(scaleX, scaleY) - 0.2F,
                            ticks,
                            45F);
                    GL11.glPopMatrix();
                } else {
                    this.renderEngine.bindTexture(
                            new ResourceLocation(
                                    GalacticraftCore.ASSET_PREFIX,
                                    "textures/gui/celestialbodies/earth.png"));
                    if (!ClientProxyCore.overworldTextureRequestSent) {
                        GalacticraftCore.packetPipeline.sendToServer(
                                new PacketSimple(
                                        PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE,
                                        new Object[] {}));
                        ClientProxyCore.overworldTextureRequestSent = true;
                    }
                    this.draw2DTexture();
                }
                break;
        }
    }

    private void draw2DTexture() {
        final Tessellator tess = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tess.setColorRGBA(255, 255, 255, 255);
        tess.startDrawingQuads();

        tess.addVertexWithUV(this.frameA, this.frameBy, 0F, this.textureAx, this.textureBy);
        tess.addVertexWithUV(this.frameBx, this.frameBy, 0F, this.textureBx, this.textureBy);
        tess.addVertexWithUV(this.frameBx, this.frameA, 0F, this.textureBx, this.textureAy);
        tess.addVertexWithUV(this.frameA, this.frameA, 0F, this.textureAx, this.textureAy);
        tess.draw();
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
}
