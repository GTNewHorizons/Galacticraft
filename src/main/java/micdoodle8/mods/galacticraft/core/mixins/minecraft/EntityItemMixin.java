package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import net.minecraft.entity.item.EntityItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin {

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 0.03999999910593033D), require = 1)
    private double galacticraft$onOnUpdate(double value) {
        return WorldUtil.getItemGravity((EntityItem) (Object) this);
    }
}
