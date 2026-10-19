package micdoodle8.mods.galacticraft.core.nei;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult;

public class NasaWorkbenchOverlayHandler extends DefaultOverlayHandler {

    public NasaWorkbenchOverlayHandler(int x, int y) {
        super(x, y);
    }

    @Override
    protected Set<Slot> getCraftMatrixSlots(GuiContainer gui, IRecipeHandler handler) {
        Set<Slot> inputSlots = new HashSet<>();
        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (!(slot instanceof SlotRocketBenchResult) && !(slot.inventory instanceof InventoryPlayer)) {
                inputSlots.add(slot);
            }
        }
        return inputSlots;
    }
}
