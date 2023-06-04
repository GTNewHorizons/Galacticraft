package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;

@Mixin(Entity.class)
public abstract class EntityMixin {

    /**
     * Return whether this entity should be rendered as on fire.
     * 
     * @author micdoodle8
     * @author radfast
     * @author SinTh0r4s
     * @author glowredman
     * @reason enable custom Galacticraft dimension behaviour
     */
    @Overwrite
    public boolean canRenderOnFire() {
        return WorldUtil.shouldRenderFire((Entity) (Object) this);
    }
}
