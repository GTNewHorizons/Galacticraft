package micdoodle8.mods.galacticraft.planets.asteroids.inventory;

import micdoodle8.mods.galacticraft.core.inventory.SlotRocketBenchResult;
import micdoodle8.mods.galacticraft.planets.mars.util.RecipeUtilMars;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerSchematicAstroMiner extends Container {
    public InventorySchematicAstroMiner craftMatrix = new InventorySchematicAstroMiner(this);
    public IInventory craftResult = new InventoryCraftResult();
    private final World worldObj;

    public ContainerSchematicAstroMiner(InventoryPlayer inventory, int x, int y, int z) {
        this.worldObj = inventory.player.worldObj;

        // OUT
        this.addSlotToContainer(
                new SlotRocketBenchResult(inventory.player, this.craftMatrix, this.craftResult, 0, 143, 55));

        // MINER
        // top & bottom
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                this.addSlotToContainer(new SlotSchematicAstroMiner(
                        this.craftMatrix, 1 + i * 4 + j, 44 + j * 18, 19 + i * 72, x, y, z, inventory.player));
            }
        }
        // poles
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 9, 116, 19, x, y, z, inventory.player));
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 10, 116, 91, x, y, z, inventory.player));
        // bore
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 11, 26, 37, x, y, z, inventory.player));
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 12, 8, 55, x, y, z, inventory.player));
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 13, 26, 73, x, y, z, inventory.player));
        // orion drives
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 14, 44, 37, x, y, z, inventory.player));
        for (int i = 0; i < 3; i++) {
            this.addSlotToContainer(
                    new SlotSchematicAstroMiner(this.craftMatrix, 15 + i, 26 + i * 18, 55, x, y, z, inventory.player));
        }
        // control
        for (int i = 0; i < 3; i++) {
            this.addSlotToContainer(
                    new SlotSchematicAstroMiner(this.craftMatrix, 18 + i, 62 + i * 18, 37, x, y, z, inventory.player));
        }
        // back
        for (int i = 0; i < 3; i++) {
            this.addSlotToContainer(
                    new SlotSchematicAstroMiner(this.craftMatrix, 21 + i, 116, 37 + i * 18, x, y, z, inventory.player));
        }
        // storage
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 24, 80, 55, x, y, z, inventory.player));
        this.addSlotToContainer(new SlotSchematicAstroMiner(this.craftMatrix, 25, 98, 55, x, y, z, inventory.player));
        // propulsion
        for (int i = 0; i < 4; i++) {
            this.addSlotToContainer(
                    new SlotSchematicAstroMiner(this.craftMatrix, 26 + i, 44 + i * 18, 73, x, y, z, inventory.player));
        }

        // PLAYER INV
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 178));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inventory, 9 + j + i * 9, 8 + j * 18, 120 + i * 18));
            }
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
        this.craftResult.setInventorySlotContents(0, RecipeUtilMars.findMatchingAstroMinerRecipe(this.craftMatrix));
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack stack = null;
        final Slot currentSlot = (Slot) this.inventorySlots.get(slotIndex);

        if (currentSlot != null && currentSlot.getHasStack()) {
            final ItemStack currentStack = currentSlot.getStack();
            stack = currentStack.copy();

            if (!this.mergeOneItem(currentStack)) {
                return null;
            }

            if (currentStack.stackSize == 0) {
                if (slotIndex == 0) {
                    currentSlot.onPickupFromSlot(player, currentStack);
                }
                currentSlot.putStack(null);
                return stack;
            }
            if (currentStack.stackSize == stack.stackSize) {
                return null;
            }
            currentSlot.onPickupFromSlot(player, currentStack);
            if (slotIndex == 0) {
                currentSlot.onSlotChanged();
            }
        }
        return stack;
    }

    protected boolean mergeOneItem(ItemStack itemStack) {
        boolean nothingLeft = false;
        if (itemStack.stackSize > 0) {
            for (int i = 1; i <= 29; ++i) {
                final Slot slot = (Slot) this.inventorySlots.get(i);
                final ItemStack slotStack = slot.getStack();
                if (slotStack == null && slot.isItemValid(itemStack)) {
                    final ItemStack stackOneItem = itemStack.copy();
                    stackOneItem.stackSize = 1;
                    itemStack.stackSize--;
                    slot.putStack(stackOneItem);
                    slot.onSlotChanged();
                    nothingLeft = true;
                    break;
                }
            }
        }
        return nothingLeft;
    }
}
