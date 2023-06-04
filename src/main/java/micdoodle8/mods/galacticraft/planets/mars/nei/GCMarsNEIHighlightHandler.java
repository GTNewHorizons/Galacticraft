package micdoodle8.mods.galacticraft.planets.mars.nei;

import java.util.List;

import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.guihook.GuiContainerManager;

public class GCMarsNEIHighlightHandler implements IHighlightHandler {

    @Override
    public List<String> handleTextData(ItemStack stack, World world, EntityPlayer player, MovingObjectPosition mop,
            List<String> currenttip, ItemInfo.Layout layout) {
        String name = null;
        try {
            final String s = GuiContainerManager.itemDisplayNameShort(stack);
            if (s != null && !s.endsWith("Unnamed")) {
                name = s;
            }

            if (name != null) {
                currenttip.add(name);
            }
        } catch (final Exception e) {}

        if (stack.getItem() == Items.redstone) {
            final int md = world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
            String s = "" + md;
            if (s.length() < 2) {
                s = " " + s;
            }
            currenttip.set(currenttip.size() - 1, name + " " + s);
        }

        return currenttip;
    }

    @Override
    public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop) {
        final int x = mop.blockX;
        final int y = mop.blockY;
        final int z = mop.blockZ;
        final Block b = world.getBlock(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        if (b == MarsBlocks.marsBlock) {
            if (meta == 2) {
                return new ItemStack(MarsBlocks.marsBlock, 1, 2);
            }

            if (meta == 9) {
                return new ItemStack(MarsBlocks.marsBlock, 1, 9);
            }
        } else if ((b == AsteroidBlocks.blockBasic) && (meta == 4)) {
            return new ItemStack(AsteroidBlocks.blockBasic, 1, 4);
        }
        return null;
    }
}
