package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(World.class)
public abstract class WorldMixin {

    /**
     * @author micdoodle8
     * @author SinTh0r4s
     * @author glowredman
     * @reason enable custom Galacticraft dimension behaviour
     */
    @Overwrite
    public float getRainStrength(float partialTicks) {
        return WorldUtil.getRainStrength((World) (Object) this, partialTicks);
    }
}
