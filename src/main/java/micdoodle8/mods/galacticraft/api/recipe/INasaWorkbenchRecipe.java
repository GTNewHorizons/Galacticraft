package micdoodle8.mods.galacticraft.api.recipe;

import java.util.HashMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface INasaWorkbenchRecipe {
    boolean matches(IInventory inventory);

    int getRecipeSize();

    ItemStack getRecipeOutput();

    HashMap<Integer, ItemStack> getRecipeInput();
}
