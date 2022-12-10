package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity {

    public EntityLivingBaseMixin() {
        super(null);
    }

    @ModifyConstant(method = "moveEntityWithHeading(FF)V", constant = @Constant(doubleValue = 0.08D), require = 1)
    private double onMoveEntityWithHeading(double value) {
        return WorldUtil.getGravityForEntity(this);
    }
}
