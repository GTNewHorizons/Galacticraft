package micdoodle8.mods.galacticraft.core.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.core.client.render.tile.TileEntityScreenRenderer;

public class ItemRendererScreen implements IItemRenderer {

    private void renderScreen(ItemRenderType type) {
        GL11.glPushMatrix();

        FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TileEntityScreenRenderer.blockTexture);

        switch (type) {
            case INVENTORY:
                GL11.glTranslatef(-0.5F, 0.525F, -0.5F);
                // GL11.glScalef(0.6F, 0.6F, 0.6F);
                break;
            case EQUIPPED:
                GL11.glTranslatef(1.0F, 1.0F, 0.0F);
                GL11.glRotatef(90, 0, -1, 0);
                // GL11.glScalef(0.5F, 0.5F, 0.5F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.2F, 0.9F, 1.2F);
                GL11.glRotatef(90, 0, 1, 0);
                // GL11.glTranslatef(1.3F, 0.9F, 0.6F);
                // GL11.glRotatef(150, 0, 1, 0);
                GL11.glScalef(1.3F, 1.3F, 1.3F);
                break;
            default:
                GL11.glTranslatef(-0.5F, 0.525F, -0.5F);
                break;
        }

        GL11.glRotatef(90, 0, 0, -1);
        TileEntityScreenRenderer.screenModel0.renderAll();

        GL11.glPopMatrix();
    }

    /**
     * IItemRenderer implementation *
     */
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return switch (type) {
            case ENTITY -> true;
            case EQUIPPED -> true;
            case EQUIPPED_FIRST_PERSON -> true;
            case INVENTORY -> true;
            default -> false;
        };
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case EQUIPPED:
                this.renderScreen(type);
                break;
            case EQUIPPED_FIRST_PERSON:
                this.renderScreen(type);
                break;
            case INVENTORY:
                this.renderScreen(type);
                break;
            case ENTITY:
                this.renderScreen(type);
                break;
            default:
        }
    }
}
