package micdoodle8.mods.galacticraft.core.energy.item;

import java.util.List;

import mekanism.api.energy.IEnergizedItem;
import micdoodle8.mods.galacticraft.api.item.ElectricItemHelper;
import micdoodle8.mods.galacticraft.api.item.IItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.items.ItemBatteryInfinite;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.world.World;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.relauncher.FMLInjectionData;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

@InterfaceList({ @Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyContainerItem"),
        @Interface(modid = "IC2API", iface = "ic2.api.item.ISpecialElectricItem"),
        @Interface(modid = "MekanismAPI|energy", iface = "mekanism.api.energy.IEnergizedItem"), })
public abstract class ItemElectricBase extends Item
        implements IItemElectricBase, IEnergyContainerItem, ISpecialElectricItem, IEnergizedItem {

    private static Object itemManagerIC2;
    public float transferMax;
    private final DefaultArtifactVersion mcVersion;

    public ItemElectricBase() {
        super();
        this.setMaxStackSize(1);
        this.setMaxDamage(100);
        this.setNoRepair();
        this.setMaxTransfer();

        this.mcVersion = new DefaultArtifactVersion((String) FMLInjectionData.data()[4]);

        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            itemManagerIC2 = new ElectricItemManagerIC2_1710();
        }
    }

    protected void setMaxTransfer() {
        this.transferMax = 200;
    }

    @Override
    public float getMaxTransferGC(ItemStack itemStack) {
        return this.transferMax;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean par4) {
        String color;
        final float joules = this.getElectricityStored(itemStack);

        if (joules <= this.getMaxElectricityStored(itemStack) / 3) {
            color = "\u00a74";
        } else if (joules > this.getMaxElectricityStored(itemStack) * 2 / 3) {
            color = "\u00a72";
        } else {
            color = "\u00a76";
        }

        list.add(
                color + EnergyDisplayHelper.getEnergyDisplayS(joules)
                        + "/"
                        + EnergyDisplayHelper.getEnergyDisplayS(this.getMaxElectricityStored(itemStack)));
    }

    /**
     * Makes sure the item is uncharged when it is crafted and not charged. Change this if you do not want this to
     * happen!
     */
    @Override
    public void onCreated(ItemStack itemStack, World par2World, EntityPlayer par3EntityPlayer) {
        this.setElectricity(itemStack, 0);
    }

    @Override
    public float recharge(ItemStack itemStack, float energy, boolean doReceive) {
        final float rejectedElectricity = Math
                .max(this.getElectricityStored(itemStack) + energy - this.getMaxElectricityStored(itemStack), 0);
        float energyToReceive = energy - rejectedElectricity;
        if (energyToReceive > this.transferMax) {
            energyToReceive = this.transferMax;
        }

        if (doReceive) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) + energyToReceive);
        }

        return energyToReceive;
    }

    @Override
    public float discharge(ItemStack itemStack, float energy, boolean doTransfer) {
        final float energyToTransfer = Math
                .min(Math.min(this.getElectricityStored(itemStack), energy), this.transferMax);

        if (doTransfer) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) - energyToTransfer);
        }

        return energyToTransfer;
    }

    @Override
    public int getTierGC(ItemStack itemStack) {
        return 1;
    }

    @Override
    public void setElectricity(ItemStack itemStack, float joules) {
        // Saves the frequency in the ItemStack
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        final float electricityStored = Math.max(Math.min(joules, this.getMaxElectricityStored(itemStack)), 0);
        itemStack.getTagCompound().setFloat("electricity", electricityStored);

        /* Sets the damage as a percentage to render the bar properly. */
        itemStack.setItemDamage((int) (100 - electricityStored / this.getMaxElectricityStored(itemStack) * 100));
    }

    @Override
    public float getTransfer(ItemStack itemStack) {
        return Math
                .min(this.transferMax, this.getMaxElectricityStored(itemStack) - this.getElectricityStored(itemStack));
    }

    /**
     * Gets the energy stored in the item. Energy is stored using item NBT
     */
    @Override
    public float getElectricityStored(ItemStack itemStack) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        float energyStored = 0f;
        if (itemStack.getTagCompound().hasKey("electricity")) {
            final NBTBase obj = itemStack.getTagCompound().getTag("electricity");
            if (obj instanceof NBTTagDouble) {
                energyStored = ((NBTTagDouble) obj).func_150288_h();
            } else if (obj instanceof NBTTagFloat) {
                energyStored = ((NBTTagFloat) obj).func_150288_h();
            }
        }

        /* Sets the damage as a percentage to render the bar properly. */
        itemStack.setItemDamage((int) (100 - energyStored / this.getMaxElectricityStored(itemStack) * 100));
        return energyStored;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
        par3List.add(ElectricItemHelper.getUncharged(new ItemStack(this)));
        par3List.add(
                ElectricItemHelper
                        .getWithCharge(new ItemStack(this), this.getMaxElectricityStored(new ItemStack(this))));
    }

    public static boolean isElectricItem(Item item) {
        if (item instanceof IItemElectricBase) {
            return true;
        }

        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            return item instanceof ISpecialElectricItem;
        }

        return false;
    }

    public static boolean isElectricItemEmpty(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        final Item item = itemstack.getItem();

        if (item instanceof IItemElectricBase) {
            return ((IItemElectricBase) item).getElectricityStored(itemstack) <= 0;
        }

        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            if (item instanceof ic2.api.item.ISpecialElectricItem) {
                return !((ic2.api.item.ISpecialElectricItem) item).canProvideEnergy(itemstack);
            }
        }

        return false;
    }

    public static boolean isElectricItemCharged(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        final Item item = itemstack.getItem();

        if (item instanceof IItemElectricBase) {
            return ((IItemElectricBase) item).getElectricityStored(itemstack) > 0;
        }

        if (EnergyConfigHandler.isIndustrialCraft2Loaded()) {
            if (item instanceof ic2.api.item.ISpecialElectricItem) {
                return ((ic2.api.item.ISpecialElectricItem) item).canProvideEnergy(itemstack);
            }
        }

        return false;
    }

    // For RF compatibility

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return (int) (this.recharge(container, maxReceive * EnergyConfigHandler.RF_RATIO, !simulate)
                / EnergyConfigHandler.RF_RATIO);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return (int) (this.discharge(container, maxExtract / EnergyConfigHandler.TO_RF_RATIO, !simulate)
                * EnergyConfigHandler.TO_RF_RATIO);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return (int) (this.getElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return (int) (this.getMaxElectricityStored(container) * EnergyConfigHandler.TO_RF_RATIO);
    }

    // The following seven methods are for Mekanism compatibility

    @Override
    public double getEnergy(ItemStack itemStack) {
        return this.getElectricityStored(itemStack) * EnergyConfigHandler.TO_MEKANISM_RATIO;
    }

    @Override
    public void setEnergy(ItemStack itemStack, double amount) {
        this.setElectricity(itemStack, (float) amount * EnergyConfigHandler.MEKANISM_RATIO);
    }

    @Override
    public double getMaxEnergy(ItemStack itemStack) {
        return this.getMaxElectricityStored(itemStack) * EnergyConfigHandler.TO_MEKANISM_RATIO;
    }

    @Override
    public double getMaxTransfer(ItemStack itemStack) {
        return this.transferMax * EnergyConfigHandler.TO_MEKANISM_RATIO;
    }

    @Override
    public boolean canReceive(ItemStack itemStack) {
        return itemStack != null && !(itemStack.getItem() instanceof ItemBatteryInfinite);
    }

    @Override
    public boolean canSend(ItemStack itemStack) {
        return true;
    }

    public boolean isMetadataSpecific(ItemStack itemStack) {
        return false;
    }

    // All the following methods are for IC2 compatibility

    @Override
    @Method(modid = "IC2API")
    public IElectricItemManager getManager(ItemStack itemstack) {
        return (IElectricItemManager) ItemElectricBase.itemManagerIC2;
    }

    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return true;
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return itemStack.getItem();
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return itemStack.getItem();
    }

    @Override
    public int getTier(ItemStack itemStack) {
        return 1;
    }

    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return this.getMaxElectricityStored(itemStack) / EnergyConfigHandler.IC2_RATIO;
    }

    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return this.transferMax * EnergyConfigHandler.TO_IC2_RATIO;
    }
}
