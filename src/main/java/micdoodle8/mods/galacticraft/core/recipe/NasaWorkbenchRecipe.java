package micdoodle8.mods.galacticraft.core.recipe;

import java.util.HashMap;
import java.util.Map.Entry;

import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class NasaWorkbenchRecipe implements INasaWorkbenchRecipe {

    private final ItemStack output;
    private final HashMap<Integer, ItemStack> input;

    public NasaWorkbenchRecipe(ItemStack output, HashMap<Integer, ItemStack> input) {
        this.output = output;
        this.input = input;
    }

    @Override
    public boolean matches(IInventory inventory) {
        for (final Entry<Integer, ItemStack> entry : this.input.entrySet()) {
            final ItemStack stackAt = inventory.getStackInSlot(entry.getKey());

            if (!this.checkItemEquals(stackAt, entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    private boolean checkItemEquals(ItemStack target, ItemStack input) {
        if ((input == null) == (target != null)) {
            return false;
        }
        return target == null && input == null
                || target.getItem() == input.getItem() && (target.getItemDamage() == OreDictionary.WILDCARD_VALUE
                        || target.getItemDamage() == input.getItemDamage());
    }

    @Override
    public int getRecipeSize() {
        return this.input.size();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output.copy();
    }

    @Override
    public HashMap<Integer, ItemStack> getRecipeInput() {
        return this.input;
    }
}
