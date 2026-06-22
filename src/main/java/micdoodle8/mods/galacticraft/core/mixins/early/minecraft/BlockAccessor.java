package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@Mixin(Block.class)
public interface BlockAccessor {
	@Invoker
	ItemStack invokeCreateStackedBlock(int meta);
}
