package micdoodle8.mods.galacticraft.core.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;
import micdoodle8.mods.galacticraft.api.entity.IFuelable;
import micdoodle8.mods.galacticraft.api.entity.IFuelableTiered;
import micdoodle8.mods.galacticraft.api.recipe.RocketFuelRecipe;
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.util.Annotations.NetworkedField;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class TileEntityFuelLoader extends TileBaseElectricBlockWithInventory
        implements ISidedInventory, IFluidHandler, ILandingPadAttachable {

    private final int tankCapacity = 12000;

    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank fuelTank = new FluidTank(this.tankCapacity);

    private ItemStack[] containingItems = new ItemStack[2];
    // here so the gui updates correctly
    @NetworkedField(targetSide = Side.CLIENT)
    public boolean coorectTier = false;

    public IFuelable attachedFuelable;
    private boolean loadedFuelLastTick = false;

    public TileEntityFuelLoader() {
        this.storage.setMaxExtract(30);
    }

    public int getScaledFuelLevel(int i) {
        final double fuelLevel = this.fuelTank.getFluid() == null ? 0 : this.fuelTank.getFluid().amount;

        return (int) (fuelLevel * i / this.tankCapacity);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.worldObj.isRemote) {
            this.loadedFuelLastTick = false;
            if (this.ticks % 10 == 0 && this.containingItems[1] != null) {
                if (this.containingItems[1].getItem() instanceof ItemCanisterGeneric) {
                    if (this.containingItems[1].getItem() == GCItems.fuelCanister) {
                        final int originalDamage = this.containingItems[1].getItemDamage();
                        final int used = this.fuelTank.fill(
                                new FluidStack(GalacticraftCore.fluidFuel, ItemCanisterGeneric.EMPTY - originalDamage),
                                true);
                        if (originalDamage + used == ItemCanisterGeneric.EMPTY) {
                            this.containingItems[1] = new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY);
                        } else {
                            this.containingItems[1] = new ItemStack(GCItems.fuelCanister, 1, originalDamage + used);
                        }
                        this.markDirty();
                    }
                } else if (FluidContainerRegistry.isFilledContainer(this.containingItems[1])) {
                    final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[1]);

                    if (liquid != null && this.containingItems[1].stackSize == 1) {
                        // boolean isFuel = FluidUtil.testFuel(FluidRegistry.getFluidName(liquid));
                        final boolean isFuel = RocketFuelRecipe.isValidFuel(liquid);
                        if (isFuel && (this.fuelTank.getFluid() == null || this.fuelTank.getFluid().isFluidEqual(liquid)
                                && this.fuelTank.getFluidAmount() + liquid.amount <= this.fuelTank.getCapacity())) {
                            this.fuelTank.fill(liquid, true);
                            this.containingItems[1] = FluidContainerRegistry
                                    .drainFluidContainer(this.containingItems[1]);
                            this.markDirty();
                        }
                    }
                } else if (this.containingItems[1].getItem() instanceof IFluidContainerItem
                        && this.containingItems[1].stackSize == 1) {
                            final IFluidContainerItem fluidContainer = (IFluidContainerItem) this.containingItems[1]
                                    .getItem();
                            final FluidStack liquid = fluidContainer.getFluid(this.containingItems[1]);
                            if (liquid != null && RocketFuelRecipe.isValidFuel(liquid)
                                    && (this.fuelTank.getFluid() == null
                                            || this.fuelTank.getFluid().isFluidEqual(liquid)
                                                    && this.fuelTank.getFluid().amount < this.fuelTank.getCapacity())) {
                                final int toDrain = Integer.min(
                                        this.fuelTank.getCapacity() - this.fuelTank.getFluidAmount(),
                                        liquid.amount);
                                if (toDrain > 0) {
                                    final FluidStack drained = fluidContainer
                                            .drain(this.containingItems[1], toDrain, true);
                                    this.fuelTank.fill(drained, true);
                                    this.markDirty();
                                }
                            }
                        }
            }

            if (this.ticks % 100 == 0) {
                this.attachedFuelable = null;

                for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    final TileEntity pad = new BlockVec3(this).getTileEntityOnSide(this.worldObj, dir);

                    if (pad instanceof TileEntityMulti) {
                        final TileEntity mainTile = ((TileEntityMulti) pad).getMainBlockTile();

                        if (mainTile instanceof IFuelable) {
                            this.attachedFuelable = (IFuelable) mainTile;
                            break;
                        }
                    } else if (pad instanceof IFuelable) {
                        this.attachedFuelable = (IFuelable) pad;
                        break;
                    }
                }
            }

            if (this.fuelTank != null && this.fuelTank.getFluid() != null
                    && this.fuelTank.getFluid().amount > 0
                    && this.isCorrectFuelTier(this.attachedFuelable)) {
                final FluidStack liquid = new FluidStack(
                        GalacticraftCore.fluidFuel,
                        2 * ConfigManagerCore.rocketFuelFactor);

                if (this.hasEnoughEnergyToRun && !this.disabled) {
                    final int filled = this.attachedFuelable.addFuel(liquid, true);
                    this.loadedFuelLastTick = filled > 0;
                    this.fuelTank.drain(filled, true);
                    this.markDirty();
                }
            }
        }
    }

    public boolean isCorrectFuelTier(IFuelable fuelable) {

        if (this.attachedFuelable == null) {
            this.coorectTier = false;
            return false;
        }
        if (fuelable instanceof IFuelableTiered) {
            final int tier = ((IFuelableTiered) fuelable).getRocketTier();
            if (tier > 0 && tier > RocketFuelRecipe.getfuelMaxTier(this.fuelTank.getFluid())) {
                this.coorectTier = false;
                return false;
            }
        }
        this.coorectTier = true;
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        this.containingItems = this.readStandardItemsFromNBT(par1NBTTagCompound);

        if (par1NBTTagCompound.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(par1NBTTagCompound.getCompoundTag("fuelTank"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        this.writeStandardItemsToNBT(par1NBTTagCompound);

        if (this.fuelTank.getFluid() != null) {
            par1NBTTagCompound.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("container.fuelloader.name");
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    // ISidedInventory Implementation:

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        return this.isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        if (slotID == 1 && itemstack != null) {
            return FluidUtil.isEmptyContainer(itemstack);
        }
        return false;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        return slotID == 1 && itemstack != null && itemstack.getItem() == GCItems.fuelCanister
                || slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem());
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return this.fuelTank.getFluid() == null || this.fuelTank.getFluidAmount() < this.fuelTank.getCapacity();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int used = 0;

        if (from.equals(ForgeDirection.getOrientation(this.getBlockMetadata() + 2).getOpposite())
                && RocketFuelRecipe.isValidFuel(resource)) {
            used = this.fuelTank.fill(resource, doFill);
        }

        return used;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo(this.fuelTank) };
    }

    @Override
    public boolean shouldUseEnergy() {
        return this.fuelTank.getFluid() != null && this.fuelTank.getFluid().amount > 0
                && !this.getDisabled(0)
                && this.loadedFuelLastTick;
    }

    @Override
    public boolean canAttachToLandingPad(IBlockAccess world, int x, int y, int z) {
        return true;
    }
}
