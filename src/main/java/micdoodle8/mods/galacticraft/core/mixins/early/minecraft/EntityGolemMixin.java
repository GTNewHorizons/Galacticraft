package micdoodle8.mods.galacticraft.core.mixins.early.minecraft;

import net.minecraft.entity.monster.EntityGolem;

import org.spongepowered.asm.mixin.Mixin;

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;

@Mixin(EntityGolem.class)
public class EntityGolemMixin implements IEntityBreathable {

    @Override
    public boolean canBreath() {
        return true;
    }
}
