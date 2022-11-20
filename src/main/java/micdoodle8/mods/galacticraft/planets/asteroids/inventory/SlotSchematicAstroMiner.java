package micdoodle8.mods.galacticraft.planets.asteroids.inventory;

import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.RecipeUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotSchematicAstroMiner extends Slot {
    private final int index;
    private final int x, y, z;
    private final EntityPlayer player;

    public SlotSchematicAstroMiner(
            IInventory par2IInventory, int par3, int par4, int par5, int x, int y, int z, EntityPlayer player) {
        super(par2IInventory, par3, par4, par5);
        this.index = par3;
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    @Override
    public void onSlotChanged() {
        if (this.player instanceof EntityPlayerMP) {
            for (final Object element : this.player.worldObj.playerEntities) {
                final EntityPlayerMP var13 = (EntityPlayerMP) element;

                if (var13.dimension == this.player.worldObj.provider.dimensionId) {
                    final double var14 = this.x - var13.posX;
                    final double var16 = this.y - var13.posY;
                    final double var18 = this.z - var13.posZ;

                    if (var14 * var14 + var16 * var16 + var18 * var18 < 20 * 20) {
                        GalacticraftCore.packetPipeline.sendTo(
                                new PacketSimple(
                                        EnumSimplePacket.C_SPAWN_SPARK_PARTICLES,
                                        new Object[] {this.x, this.y, this.z}),
                                var13);
                    }
                }
            }
        }
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (index >= 1 && index <= 8) {
            return itemStack.getItem() == MarsItems.marsItemBasic && itemStack.getItemDamage() == 3;
        } else if (index == 9 || index == 10) {
            return itemStack.getItem() == GCItems.flagPole;
        } else if (index >= 11 && index <= 13) {
            return itemStack.getItem() == AsteroidsItems.basicItem && itemStack.getItemDamage() == 0;
        } else if (index >= 14 && index <= 17) {
            return itemStack.getItem() == AsteroidsItems.orionDrive;
        } else if (index == 18 && GalacticraftCore.isGalaxySpaceLoaded) {
            return itemStack.getItem()
                            == GameRegistry.findItem(Constants.MOD_ID_GALAXYSPACE, "item.RocketControlComputer")
                    && itemStack.getItemDamage() == 102;
        } else if (index == 19 || index == 20) {
            return itemStack.getItem() == GCItems.basicItem && itemStack.getItemDamage() == 14;
        } else if (index >= 21 && index <= 23) {
            return itemStack.getItem() == GCItems.heavyPlatingTier1;
        } else if (index == 24 || index == 25) {
            return itemStack.getItem() == Item.getItemFromBlock(RecipeUtil.getChestBlock())
                    && itemStack.getItemDamage() == 1;
        } else if (index == 26) {
            return itemStack.getItem() == AsteroidsItems.basicItem && itemStack.getItemDamage() == 8;
        } else if (index == 27) {
            return itemStack.getItem() == Item.getItemFromBlock(AsteroidBlocks.beamReceiver);
        } else if (index == 28 || index == 29) {
            return itemStack.getItem() == GameRegistry.findItem(Constants.MOD_ID_GREGTECH, "gt.metaitem.01")
                    && itemStack.getItemDamage() == 32603;
        } else {
            return false;
        }
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
