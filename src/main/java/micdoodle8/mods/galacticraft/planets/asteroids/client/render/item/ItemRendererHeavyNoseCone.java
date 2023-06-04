package micdoodle8.mods.galacticraft.planets.asteroids.client.render.item;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager;
import micdoodle8.mods.galacticraft.core.util.VersionUtil;

public class ItemRendererHeavyNoseCone implements IItemRenderer {

    private void renderHeavyNoseCone(ItemStack item) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);

        for (int i = 0; i < 2; i++) {
            GL11.glPushMatrix();

            if (i == 1) {
                final SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(
                        FMLClientHandler.instance().getClientPlayerEntity().getGameProfile().getName());
                Vector3 color = null;

                if (race != null) {
                    color = race.getTeamColor();
                }

                if (color == null) {
                    color = new Vector3(1, 1, 1);
                }

                GL11.glColor4f(color.floatX(), color.floatY(), color.floatZ(), 1.0F);
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
                this.renderHeavyNoseCone(item);
                break;
            default:
                break;
        }
    }
}
