package micdoodle8.mods.galacticraft.core.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemCanisterOxygenInfinite extends ItemOxygenTank implements IItemOxygenSupply {
    public ItemCanisterOxygenInfinite(String assetName) {
        super(1, assetName);
        this.setMaxDamage(Integer.MAX_VALUE);
        this.setContainerItem(GCItems.oilCanister);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "oxygenCanisterInfinite");
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (super.getContainerItem(stack) == null) return null;
        return stack;
    }

    @Override
    public float discharge(ItemStack stack, float amount) {
        return amount;
    }

    @Override
    public int getOxygenStored(ItemStack stack) {
        return stack.getMaxDamage();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return ClientProxyCore.galacticraftItem;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advancedItemTooltips) {
        tooltip.add(GCCoreUtil.translate("gui.tank.oxygenRemaining") + ": " + EnumChatFormatting.LIGHT_PURPLE
                + GCCoreUtil.translate("gui.tank.infinite"));
    }
}
