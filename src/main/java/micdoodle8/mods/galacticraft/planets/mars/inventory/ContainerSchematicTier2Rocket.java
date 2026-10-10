package micdoodle8.mods.galacticraft.planets.mars.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult;
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars;

public class ContainerSchematicTier2Rocket extends Container {

    public InventorySchematicTier2Rocket craftMatrix = new InventorySchematicTier2Rocket(this);
    public IInventory craftResult = new InventoryCraftResult();
    private final World worldObj;

    public ContainerSchematicTier2Rocket(InventoryPlayer par1InventoryPlayer, int x, int y, int z) {
        final int change = 27;
        this.worldObj = par1InventoryPlayer.player.worldObj;
        this.addSlotToContainer(
                new SlotRocketBenchResult(
                        par1InventoryPlayer.player,
                        this.craftMatrix,
                        this.craftResult,
                        0,
                        142,
                        18 + 69 + change));
        int var6;
        int var7;

        // Cone
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        1,
                        48,
                        -8 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));

        // Body
        for (var6 = 0; var6 < 5; ++var6) {
            this.addSlotToContainer(
                    new SlotSchematicTier2Rocket(
                            this.craftMatrix,
                            2 + var6,
                            39,
                            -6 + var6 * 18 + 16 + change,
                            x,
                            y,
                            z,
                            par1InventoryPlayer.player));
        }

        // Body Right
        for (var6 = 0; var6 < 5; ++var6) {
            this.addSlotToContainer(
                    new SlotSchematicTier2Rocket(
                            this.craftMatrix,
                            7 + var6,
                            57,
                            -6 + var6 * 18 + 16 + change,
                            x,
                            y,
                            z,
                            par1InventoryPlayer.player));
        }

        // Left fins
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        12,
                        21,
                        64 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        13,
                        21,
                        82 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        14,
                        21,
                        100 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));

        // Engine
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        15,
                        48,
                        100 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));

        // Right fins
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        16,
                        75,
                        64 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        17,
                        75,
                        82 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));
        this.addSlotToContainer(
                new SlotSchematicTier2Rocket(
                        this.craftMatrix,
                        18,
                        75,
                        100 + change,
                        x,
                        y,
                        z,
                        par1InventoryPlayer.player));

        // Addons
        for (int var8 = 0; var8 < 3; var8++) {
            this.addSlotToContainer(
                    new SlotSchematicTier2Rocket(
                            this.craftMatrix,
                            19 + var8,
                            93 + var8 * 26,
                            -15 + change,
                            x,
                            y,
                            z,
                            par1InventoryPlayer.player));
        }

        // Player inv:

        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(
                        new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 129 + var6 * 18 + change));
            }
        }

        for (var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 18 + 169 + change));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);

        if (!this.worldObj.isRemote) {
            for (int var2 = 1; var2 < this.craftMatrix.getSizeInventory(); ++var2) {
                final ItemStack var3 = this.craftMatrix.getStackInSlotOnClosing(var2);

                if (var3 != null) {
                    par1EntityPlayer.entityDropItem(var3, 0.0F);
                }
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingSpaceshipT2Recipe(this.craftMatrix));
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1) {
        ItemStack stack = null;
        final Slot currentSlot = this.inventorySlots.get(par1);

        if (currentSlot != null && currentSlot.getHasStack()) {
            final ItemStack currentStack = currentSlot.getStack();
            stack = currentStack.copy();

            final int playerInventoryStart = this.inventorySlots.size() - 36;
            if (!(currentSlot.inventory instanceof InventoryPlayer)) {
                if (!this.mergeItemStack(currentStack, playerInventoryStart, this.inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!this.mergeOneItem(currentStack, 1, playerInventoryStart, false)) {
                if (par1 < playerInventoryStart + 27) {
                    if (!this.mergeItemStack(
                            currentStack,
                            playerInventoryStart + 27,
                            this.inventorySlots.size(),
                            false)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(currentStack, playerInventoryStart, playerInventoryStart + 27, false)) {
                    return null;
                }
            }

            if (currentStack.stackSize == 0) {
                if (par1 == 0) {
                    currentSlot.onPickupFromSlot(par1EntityPlayer, currentStack);
                }
                currentSlot.putStack(null);
                return stack;
            }
            if (currentStack.stackSize == stack.stackSize) {
                return null;
            }
            currentSlot.onPickupFromSlot(par1EntityPlayer, currentStack);
            if (par1 == 0) {
                currentSlot.onSlotChanged();
            }
        }
        return stack;
    }

    protected boolean mergeOneItem(ItemStack itemStack, int start, int end, boolean reverse) {
        if (itemStack.stackSize > 0) {
            for (int index = start; index < end; index++) {
                final Slot slot = this.inventorySlots.get(index);
                if (slot.getStack() == null && slot.isItemValid(itemStack)) {
                    final ItemStack stackOneItem = itemStack.copy();
                    stackOneItem.stackSize = 1;
                    itemStack.stackSize--;
                    slot.putStack(stackOneItem);
                    slot.onSlotChanged();
                    return true;
                }
            }
        }
        return false;
    }
}
