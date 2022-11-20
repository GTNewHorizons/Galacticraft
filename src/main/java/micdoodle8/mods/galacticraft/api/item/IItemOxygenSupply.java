package micdoodle8.mods.galacticraft.api.item;

import net.minecraft.item.ItemStack;

public interface IItemOxygenSupply {
    /*
     * Returns the amount of gas that this oxygen item is able to supply
     */
    float discharge(ItemStack itemStack, float amount);

    int getOxygenStored(ItemStack theItem);
}
