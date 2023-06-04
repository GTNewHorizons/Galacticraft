package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.blocks.BlockMachine;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineTiered;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockMachine extends ItemBlockDesc {

    public ItemBlockMachine(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int index = 0;
        final int typenum = itemstack.getItemDamage() & 12;

        if (this.field_150939_a == GCBlocks.machineBase) {
            index = typenum / 4;
        } else if (this.field_150939_a == GCBlocks.machineTiered) {
            

            // Tier 2 versions of the same
            switch (typenum) {
                case BlockMachineTiered.ELECTRIC_FURNACE_METADATA:
                    return "tile.machine.2";
                case BlockMachineTiered.STORAGE_MODULE_METADATA:
                    return "tile.machine.1";
                case 8 + BlockMachineTiered.ELECTRIC_FURNACE_METADATA:
                    return "tile.machine.7";
                case 8 + BlockMachineTiered.STORAGE_MODULE_METADATA:
                    return "tile.machine.8";
                default:
                    break;
            }
        } else {
            switch (typenum) {
                case BlockMachine2.OXYGEN_STORAGE_MODULE_METADATA:
                    index = 6;
                    break;
                case BlockMachine2.CIRCUIT_FABRICATOR_METADATA:
                    index = 5;
                    break;
                case BlockMachine2.ELECTRIC_COMPRESSOR_METADATA:
                    index = 4;
                    break;
                default:
                    break;
            }
        }

        return this.field_150939_a.getUnlocalizedName() + "." + index;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            return;
        }

        final int typenum = stack.getItemDamage() & 12;

        // The player could be a FakePlayer made by another mod e.g. LogisticsPipes
        if (player instanceof EntityPlayerSP) {
            if (this.field_150939_a == GCBlocks.machineBase && typenum == BlockMachine.COMPRESSOR_METADATA) {
                ClientProxyCore.playerClientHandler.onBuild(1, (EntityPlayerSP) player);
            } else if (this.field_150939_a == GCBlocks.machineBase2
                    && typenum == BlockMachine2.CIRCUIT_FABRICATOR_METADATA) {
                        ClientProxyCore.playerClientHandler.onBuild(2, (EntityPlayerSP) player);
                    }
        }
    }

    @Override
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
