package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class ItemBlockBase extends ItemBlockDesc {

    public ItemBlockBase(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        String name = switch (itemstack.getItemDamage()) {
            case 3 -> "decoblock1";
            case 4 -> "decoblock2";
            case 5 -> "oreCopper";
            case 6 -> "oreTin";
            case 7 -> "oreAluminum";
            case 8 -> "oreSilicon";
            case 9 -> "copperBlock";
            case 10 -> "tinBlock";
            case 11 -> "aluminumBlock";
            case 12 -> "meteorironBlock";
            default -> "null";
        };

        return this.field_150939_a.getUnlocalizedName() + "." + name;
    }

    @Override
    public String getUnlocalizedName() {
        return this.field_150939_a.getUnlocalizedName() + ".0";
    }
}
