package micdoodle8.mods.galacticraft.planets.asteroids.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;

public class BlockBasicAsteroids extends Block implements IDetectableResource, IPlantableBlock, ITerraformableBlock {

    @SideOnly(Side.CLIENT)
    private IIcon[] blockIcons;

    public BlockBasicAsteroids(String assetName) {
        super(Material.rock);
        this.blockHardness = 3.0F;
        this.setBlockName(assetName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcons = new IIcon[6];
        this.blockIcons[0] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "asteroid0");
        this.blockIcons[1] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "asteroid1");
        this.blockIcons[2] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "asteroid2");
        this.blockIcons[3] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "oreAluminum");
        this.blockIcons[4] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "oreIlmenite");
        this.blockIcons[5] = par1IconRegister.registerIcon(AsteroidsModule.TEXTURE_PREFIX + "oreIron");
        this.blockIcon = this.blockIcons[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.blockIcons.length) {
            return this.blockIcon;
        }

        return this.blockIcons[meta];
    }

    @Override
    public Item getItemDropped(int meta, Random random, int par3) {
        return switch (meta) {
            default -> super.getItemDropped(meta, random, par3);
        };
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        switch (metadata) {
            case 4:
                final ArrayList<ItemStack> ret = new ArrayList<>();

                int count = this.quantityDropped(metadata, fortune, world.rand);
                for (int i = 0; i < count; i++) {
                    ret.add(new ItemStack(AsteroidsItems.basicItem, 1, 3));
                }
                count = this.quantityDropped(metadata, fortune, world.rand);
                for (int i = 0; i < count; i++) {
                    ret.add(new ItemStack(AsteroidsItems.basicItem, 1, 4));
                }
                return ret;
            default:
                return super.getDrops(world, x, y, z, metadata, fortune);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return switch (meta) {
            case 4 -> 0;
            default -> meta;
        };
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return worldIn.getBlockMetadata(x, y, z);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        switch (meta) {
            case 4:
                if (fortune >= 1) {
                    return random.nextFloat() < fortune * 0.29F - 0.25F ? 2 : 1;
                }
            default:
                return 1;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        int var4;

        for (var4 = 0; var4 < this.blockIcons.length; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
    }

    @Override
    public boolean isValueable(int metadata) {
        return switch (metadata) {
            case 3, 4, 5 -> true;
            default -> false;
        };
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction,
            IPlantable plantable) {
        return false;
    }

    @Override
    public int requiredLiquidBlocksNearby() {
        return 4;
    }

    @Override
    public boolean isPlantable(int metadata) {
        return false;
    }

    @Override
    public boolean isTerraformable(World world, int x, int y, int z) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        final int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 4) {
            return new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        }

        return super.getPickBlock(target, world, x, y, z);
    }
}
