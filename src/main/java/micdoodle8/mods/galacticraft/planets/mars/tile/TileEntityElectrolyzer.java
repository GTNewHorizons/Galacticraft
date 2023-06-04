package micdoodle8.mods.galacticraft.planets.mars.tile;

import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.grid.IHydrogenNetwork;
import micdoodle8.mods.galacticraft.api.transmission.grid.IOxygenNetwork;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenStorage;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory;
import micdoodle8.mods.galacticraft.core.oxygen.NetworkHelper;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule;
import micdoodle8.mods.galacticraft.core.util.Annotations.NetworkedField;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve;

import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.relauncher.Side;

public class TileEntityElectrolyzer extends TileBaseElectricBlockWithInventory
        implements ISidedInventory, IDisableableMachine, IFluidHandler, IOxygenStorage, IOxygenReceiver {

    private final int tankCapacity = 4000;

    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank waterTank = new FluidTank(this.tankCapacity);

    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank = new FluidTank(this.tankCapacity);

    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank2 = new FluidTank(this.tankCapacity);

    public int processTimeRequired = 3;

    @NetworkedField(targetSide = Side.CLIENT)
    public int processTicks = 0;

    private ItemStack[] containingItems = new ItemStack[4];

    public TileEntityElectrolyzer() {
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 150 : 120);
        this.setTierGC(2);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!this.worldObj.isRemote) {
            if (this.containingItems[1] != null) {
                final FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(this.containingItems[1]);

                if (liquid != null && liquid.getFluid().getName().equals(FluidRegistry.WATER.getName())) {
                    if (this.waterTank.getFluid() == null
                            || this.waterTank.getFluid().amount + liquid.amount <= this.waterTank.getCapacity()) {
                        this.waterTank.fill(liquid, true);

                        this.containingItems[1] = FluidUtil.getUsedContainer(this.containingItems[1]);
                    }
                }
            }

            // Only drain with atmospheric valve
            this.checkFluidTankTransfer(2, this.liquidTank);
            this.checkFluidTankTransfer(3, this.liquidTank2);

            if (this.hasEnoughEnergyToRun && this.canProcess()) {
                // 50% extra speed boost for Tier 2 machine if powered by Tier 2 power
                if (this.tierGC == 2) {
                    this.processTimeRequired = this.poweredByTierGC == 2 ? 2 : 3;
                }

                if (this.processTicks == 0) {
                    this.processTicks = this.processTimeRequired;
                } else if (--this.processTicks <= 0) {
                    this.doElectrolysis();
                    this.processTicks = this.canProcess() ? this.processTimeRequired : 0;
                }
            } else {
                this.processTicks = 0;
            }

            this.produceOxygen(ForgeDirection.getOrientation(this.getOxygenOutputDirection()));
            this.produceHydrogen(ForgeDirection.getOrientation(this.getHydrogenOutputDirection()));
        }
    }

    private void doElectrolysis() {
        // Can't be called if the gasTank fluid is null
        final int waterAmount = this.waterTank.getFluid().amount;
        if (waterAmount == 0) {
            return;
        }

        this.placeIntoFluidTanks(2);
        this.waterTank.drain(1, true);
    }

    private int placeIntoFluidTanks(int amountToDrain) {
        final int fuelSpace = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount();
        final int fuelSpace2 = this.liquidTank2.getCapacity() - this.liquidTank2.getFluidAmount();
        int amountToDrain2 = amountToDrain * 2;

        if (amountToDrain > fuelSpace) {
            amountToDrain = fuelSpace;
        }
        this.liquidTank.fill(FluidRegistry.getFluidStack("oxygen", amountToDrain), true);

        if (amountToDrain2 > fuelSpace2) {
            amountToDrain2 = fuelSpace2;
        }
        this.liquidTank2.fill(FluidRegistry.getFluidStack("hydrogen", amountToDrain2), true);

        return amountToDrain;
    }

    private void checkFluidTankTransfer(int slot, FluidTank tank) {
        if (this.containingItems[slot] != null
                && this.containingItems[slot].getItem() instanceof ItemAtmosphericValve) {
            tank.drain(4, true);
        }
    }

    public int getScaledGasLevel(int i) {
        return this.waterTank.getFluid() != null ? this.waterTank.getFluid().amount * i / this.waterTank.getCapacity()
                : 0;
    }

    public int getScaledFuelLevel(int i) {
        return this.liquidTank.getFluid() != null
                ? this.liquidTank.getFluid().amount * i / this.liquidTank.getCapacity()
                : 0;
    }

    public int getScaledFuelLevel2(int i) {
        return this.liquidTank2.getFluid() != null
                ? this.liquidTank2.getFluid().amount * i / this.liquidTank2.getCapacity()
                : 0;
    }

    public boolean canProcess() {
        if (this.waterTank.getFluid() == null || this.waterTank.getFluid().amount <= 0 || this.getDisabled(0)) {
            return false;
        }

        final boolean tank1HasSpace = this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity();
        final boolean tank2HasSpace = this.liquidTank2.getFluidAmount() < this.liquidTank2.getCapacity();

        return tank1HasSpace || tank2HasSpace;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.processTicks = nbt.getInteger("processTicks");
        this.containingItems = this.readStandardItemsFromNBT(nbt);

        if (nbt.hasKey("waterTank")) {
            this.waterTank.readFromNBT(nbt.getCompoundTag("waterTank"));
        }

        if (nbt.hasKey("gasTank")) {
            this.liquidTank.readFromNBT(nbt.getCompoundTag("gasTank"));
        }
        if (nbt.hasKey("gasTank2")) {
            this.liquidTank2.readFromNBT(nbt.getCompoundTag("gasTank2"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("processTicks", this.processTicks);
        this.writeStandardItemsToNBT(nbt);

        if (this.waterTank.getFluid() != null) {
            nbt.setTag("waterTank", this.waterTank.writeToNBT(new NBTTagCompound()));
        }

        if (this.liquidTank.getFluid() != null) {
            nbt.setTag("gasTank", this.liquidTank.writeToNBT(new NBTTagCompound()));
        }
        if (this.liquidTank2.getFluid() != null) {
            nbt.setTag("gasTank2", this.liquidTank2.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected ItemStack[] getContainingItems() {
        return this.containingItems;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public String getInventoryName() {
        return GCCoreUtil.translate("tile.marsMachine.6.name");
    }

    // ISidedInventory Implementation:

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { 0, 1 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            switch (slotID) {
                case 0:
                    return ItemElectricBase.isElectricItemCharged(itemstack);
                case 1:
                    return itemstack.getItem() == Items.water_bucket;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        if (this.isItemValidForSlot(slotID, itemstack)) {
            switch (slotID) {
                case 0:
                    return ItemElectricBase.isElectricItemEmpty(itemstack);
                case 1:
                    return itemstack.getItem() == Items.bucket;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
        final Item item = itemstack.getItem();
        switch (slotID) {
            case 0:
                return ItemElectricBase.isElectricItem(item);
            case 1:
                return item == Items.bucket || item == Items.water_bucket;
        }

        return false;
    }

    @Override
    public boolean shouldUseEnergy() {
        return this.canProcess();
    }

    @Override
    public double getPacketRange() {
        return 320.0D;
    }

    @Override
    public ForgeDirection getElectricInputDirection() {
        return ForgeDirection.DOWN;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 1)) {
            return this.liquidTank2.getFluid() != null && this.liquidTank2.getFluidAmount() > 0;
        }

        // 2->5 3->4 4->2 5->3
        if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == (side ^ 1)) {
            return this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0;
        }

        return false;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 1)) {
            if (resource != null && resource.isFluidEqual(this.liquidTank2.getFluid())) {
                return this.liquidTank2.drain(resource.amount, doDrain);
            }
        }

        // 2->5 3->4 4->2 5->3
        if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == (side ^ 1)) {
            if (resource != null && resource.isFluidEqual(this.liquidTank.getFluid())) {
                return this.liquidTank.drain(resource.amount, doDrain);
            }
        }

        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();
        if (side == (metaside ^ 1)) {
            return this.liquidTank2.drain(maxDrain, doDrain);
        }

        // 2->5 3->4 4->2 5->3
        if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == (side ^ 1)) {
            return this.liquidTank.drain(maxDrain, doDrain);
        }

        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (from.ordinal() == this.getBlockMetadata() + 2) {
            // Can fill with water
            return fluid != null && fluid.getName().equals(FluidRegistry.WATER.getName());
        }

        return false;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int used = 0;

        if (resource != null && this.canFill(from, resource.getFluid())) {
            used = this.waterTank.fill(resource, doFill);
        }

        return used;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTankInfo[] tankInfo = new FluidTankInfo[] {};
        final int metaside = this.getBlockMetadata() + 2;
        final int side = from.ordinal();

        if (metaside == side) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo(this.waterTank) };
        } else if (metaside == (side ^ 1)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo(this.liquidTank2) };
        } else if (7 - (metaside ^ (metaside > 3 ? 0 : 1)) == (side ^ 1)) {
            tankInfo = new FluidTankInfo[] { new FluidTankInfo(this.liquidTank) };
        }

        return tankInfo;
    }

    @Override
    public int getBlockMetadata() {
        if (this.blockMetadata == -1) {
            this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        }

        return this.blockMetadata & 3;
    }

    @Override
    public void setOxygenStored(float amount) {
        this.liquidTank.setFluid(new FluidStack(AsteroidsModule.fluidOxygenGas, (int) amount));
    }

    @Override
    public float getOxygenStored() {
        return this.liquidTank.getFluidAmount();
    }

    public int getHydrogenStored() {
        return this.liquidTank2.getFluidAmount();
    }

    @Override
    public float getMaxOxygenStored() {
        return this.liquidTank.getCapacity();
    }

    private int getOxygenOutputDirection() {
        final int metaside = this.getBlockMetadata() + 2;
        return 7 - (metaside ^ (metaside > 3 ? 0 : 1)) ^ 1;
    }

    private int getHydrogenOutputDirection() {
        final int metaside = this.getBlockMetadata() + 2;
        return metaside ^ 1;
    }

    private boolean produceOxygen(ForgeDirection outputDirection) {
        final float provide = this.getOxygenProvide(outputDirection);

        if (provide > 0) {
            final TileEntity outputTile = new BlockVec3(this).modifyPositionFromSide(outputDirection)
                    .getTileEntity(this.worldObj);
            final IOxygenNetwork outputNetwork = NetworkHelper
                    .getOxygenNetworkFromTileEntity(outputTile, outputDirection);

            if (outputNetwork != null) {
                final float powerRequest = outputNetwork.getRequest(this);

                if (powerRequest > 0) {
                    final float toSend = Math.min(this.getOxygenStored(), provide);
                    final float rejectedPower = outputNetwork.produce(toSend, this);

                    this.provideOxygen(Math.max(toSend - rejectedPower, 0), true);
                    return true;
                }
            } else if (outputTile instanceof IOxygenReceiver) {
                final float requestedOxygen = ((IOxygenReceiver) outputTile)
                        .getOxygenRequest(outputDirection.getOpposite());

                if (requestedOxygen > 0) {
                    final float toSend = Math.min(this.getOxygenStored(), provide);
                    final float acceptedOxygen = ((IOxygenReceiver) outputTile)
                            .receiveOxygen(outputDirection.getOpposite(), toSend, true);
                    this.provideOxygen(acceptedOxygen, true);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean produceHydrogen(ForgeDirection outputDirection) {
        final float provide = this.getHydrogenProvide(outputDirection);

        if (provide > 0) {
            final TileEntity outputTile = new BlockVec3(this).modifyPositionFromSide(outputDirection)
                    .getTileEntity(this.worldObj);
            final IHydrogenNetwork outputNetwork = NetworkHelper
                    .getHydrogenNetworkFromTileEntity(outputTile, outputDirection);

            if (outputNetwork != null) {
                final float powerRequest = outputNetwork.getRequest(this);

                if (powerRequest > 0) {
                    final float toSend = Math.min(this.getHydrogenStored(), provide);
                    final float rejectedPower = outputNetwork.produce(toSend, this);

                    this.provideHydrogen((int) Math.max(toSend - rejectedPower, 0), true);
                    return true;
                }
            } else if (outputTile instanceof TileEntityMethaneSynthesizer) {
                final float requestedHydrogen = ((TileEntityMethaneSynthesizer) outputTile)
                        .getHydrogenRequest(outputDirection.getOpposite());

                if (requestedHydrogen > 0) {
                    final float toSend = Math.min(this.getHydrogenStored(), provide);
                    final float acceptedHydrogen = ((TileEntityMethaneSynthesizer) outputTile)
                            .receiveHydrogen(outputDirection.getOpposite(), toSend, true);
                    this.provideHydrogen((int) acceptedHydrogen, true);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public float provideOxygen(ForgeDirection from, float request, boolean doProvide) {
        if (this.getOxygenOutputDirection() == from.ordinal()) {
            return this.provideOxygen(request, doProvide);
        }

        return 0;
    }

    public float provideOxygen(float request, boolean doProvide) {
        if (request > 0) {
            final float requestedOxygen = Math.min(request, this.liquidTank.getFluidAmount());

            if (doProvide) {
                this.setOxygenStored(this.liquidTank.getFluidAmount() - requestedOxygen);
            }

            return requestedOxygen;
        }

        return 0;
    }

    public int provideHydrogen(int request, boolean doProvide) {
        if (request > 0) {
            final int currentHydrogen = this.liquidTank2.getFluidAmount();
            final int requestedHydrogen = Math.min(request, currentHydrogen);

            if (doProvide) {
                this.liquidTank2.setFluid(
                        new FluidStack(FluidRegistry.getFluid("hydrogen"), currentHydrogen - requestedHydrogen));
            }

            return requestedHydrogen;
        }

        return 0;
    }

    @Override
    public float getOxygenProvide(ForgeDirection direction) {
        return this.getOxygenOutputDirection() == direction.ordinal()
                ? Math.min(TileEntityOxygenStorageModule.OUTPUT_PER_TICK, this.getOxygenStored())
                : 0.0F;
    }

    public float getHydrogenProvide(ForgeDirection direction) {
        return this.getHydrogenOutputDirection() == direction.ordinal()
                ? Math.min(TileEntityOxygenStorageModule.OUTPUT_PER_TICK, this.getHydrogenStored())
                : 0.0F;
    }

    @Override
    public boolean shouldPullOxygen() {
        return false;
    }

    @Override
    public float receiveOxygen(ForgeDirection from, float receive, boolean doReceive) {
        return 0;
    }

    @Override
    public float getOxygenRequest(ForgeDirection direction) {
        return 0;
    }

    @Override
    public boolean canConnect(ForgeDirection direction, NetworkType type) {
        if (direction == null || direction.equals(ForgeDirection.UNKNOWN)) {
            return false;
        }

        if (type == NetworkType.OXYGEN) {
            return this.getOxygenOutputDirection() == direction.ordinal();
        }

        if (type == NetworkType.HYDROGEN) {
            return this.getHydrogenOutputDirection() == direction.ordinal();
        }

        if (type == NetworkType.POWER) {
            return direction == this.getElectricInputDirection();
        }

        return false;
    }
}
