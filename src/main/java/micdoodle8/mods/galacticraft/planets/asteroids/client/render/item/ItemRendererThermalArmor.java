package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import micdoodle8.mods.galacticraft.core.util.VersionUtil;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;

public class ItemRendererThermalArmor implements IItemRenderer {

    private void renderThermalArmor(ItemRenderType type, RenderBlocks render, ItemStack item, float translateX,
            float translateY, float translateZ) {
        GL11.glPushMatrix();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GL11.glEnable(GL11.GL_BLEND);

        for (int i = 0; i < 2; i++) {
            GL11.glPushMatrix();

            if (i == 1) {
                final float time = FMLClientHandler.instance().getClientPlayerEntity().ticksExisted / 15.0F;
                float r = (float) Math.max(Math.cos(time), 0.0F);
                float b = (float) Math.max(Math.cos(time) * -1, 0.0F);

                if (r <= 0.6 && b <= 0.6) {
                    r = 0.0F;
                    b = 0.0F;
                }

                GL11.glColor4f(r, b / 2.0F, b, r + b / 1.5F);
            }

            final IIcon iicon = FMLClientHandler.instance().getClientPlayerEntity().getItemIcon(item, i);

            if (iicon == null) {
                GL11.glPopMatrix();
                return;
            }

            FMLClientHandler.instance().getClient().getTextureManager().bindTexture(
                    FMLClientHandler.instance().getClient().getTextureManager()
                            .getResourceLocation(item.getItemSpriteNumber()));
            VersionUtil.setMipMap(false, false);
            final Tessellator tessellator = Tessellator.instance;
            final float f = iicon.getMinU();
            final float f1 = iicon.getMaxU();
            final float f2 = iicon.getMinV();
            final float f3 = iicon.getMaxV();
            final float f4 = 0.0F;
            final float f5 = 1.0F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            final float f6 = 16.0F;
            GL11.glScalef(f6, f6, f6);
            GL11.glTranslatef(-f4, -f5, 0.0F);
            ItemRenderer
                    .renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    /**
     * IItemRenderer implementation *
     */
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return switch (type) {
            case ENTITY -> false;
            case EQUIPPED -> false;
            case EQUIPPED_FIRST_PERSON -> false;
            case INVENTORY -> true;
            default -> false;
        };
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return switch (helper) {
            case INVENTORY_BLOCK -> false;
            default -> false;
        };
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case INVENTORY:
                this.renderThermalArmor(type, (RenderBlocks) data[0], item, -0.5f, -0.5f, -0.5f);
                break;
            default:
                break;
        }
    }
}
