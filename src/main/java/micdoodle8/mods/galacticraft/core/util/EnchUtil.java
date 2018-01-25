package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.common.Loader;

public class EnchUtil
{
    static
    {
        setSoulBoundID();
    }

    // -2 - EnderIO not found
    // -1 - enchantment not found
    private static int soulBoundID;

    public static int getSoulBoundID()
    {
        return soulBoundID;
    }

    private static void setSoulBoundID()
    {
        if (!Loader.isModLoaded("EnderIO"))
        {
            soulBoundID = -2;
            return;
        }
        for (Enchantment ench : Enchantment.enchantmentsList)
        {
            if (ench != null && ench.getName().equals("enchantment.enderio.soulBound"))
            {
                soulBoundID = ench.effectId;
                return;
            }
        }
        soulBoundID = -1;
    }

    public static boolean isSoulBounded(ItemStack stack)
    {
        NBTTagList stackEnch = stack.getEnchantmentTagList();
        if (getSoulBoundID() >= 0 && stackEnch != null)
        {
            for (int i = 0; i < stackEnch.tagCount(); i++)
            {
                int id = stackEnch.getCompoundTagAt(i).getInteger("id");
                if (id == getSoulBoundID())
                    return true;
            }
        }
        return false;
    }
}
