package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Compatibility shim for the Galacticraft player renderer that used to live here.
 * <p>
 * Galacticraft no longer registers its own {@code RenderPlayer}: the thermal armor, the equipment and the player pose
 * are applied by mixins instead (see {@link GCPlayerRenderer}), which leaves the player renderer itself free for other
 * mods to replace. Everything here forwards to the new home and only exists so that addons compiled against the old
 * class keep working.
 *
 * @deprecated use {@link GCPlayerRenderer} and the top-level
 *             {@link micdoodle8.mods.galacticraft.core.client.render.entities.RotatePlayerEvent}
 */
@Deprecated
public class RenderPlayerGC {

    /** @deprecated use {@link GCPlayerRenderer#modelThermalPadding} */
    @Deprecated
    public static final ModelBiped modelThermalPadding = GCPlayerRenderer.modelThermalPadding;

    /** @deprecated use {@link GCPlayerRenderer#modelThermalPaddingHelmet} */
    @Deprecated
    public static final ModelBiped modelThermalPaddingHelmet = GCPlayerRenderer.modelThermalPaddingHelmet;

    /**
     * Still honoured by {@link GCPlayerRenderer}, in addition to its own flag, so that external callers setting this
     * keep working.
     *
     * @deprecated use {@link GCPlayerRenderer#flagRenderOverride}
     */
    @Deprecated
    public static boolean flagThermalOverride = false;

    private RenderPlayerGC() {}

    /** @deprecated use {@link GCPlayerRenderer#renderThermalPadding} */
    @Deprecated
    public static void renderModelS(RendererLivingEntity inst, EntityLivingBase entity, float limbSwing,
            float limbSwingAmount, float ticksExisted, float headYaw, float headPitch, float scale) {
        GCPlayerRenderer.renderThermalPadding(
                inst,
                entity,
                limbSwing,
                limbSwingAmount,
                ticksExisted,
                headYaw,
                headPitch,
                scale);
    }

    /**
     * The event Galacticraft actually posts. Forge's event bus only notifies listeners registered for the posted class
     * or one of its supertypes, so this subclass must remain the posted type for as long as addons may be listening for
     * it; new code should listen for the top-level
     * {@link micdoodle8.mods.galacticraft.core.client.render.entities.RotatePlayerEvent} instead, which receives this
     * event too.
     *
     * @deprecated use {@link micdoodle8.mods.galacticraft.core.client.render.entities.RotatePlayerEvent}
     */
    @Deprecated
    public static class RotatePlayerEvent
            extends micdoodle8.mods.galacticraft.core.client.render.entities.RotatePlayerEvent {

        public RotatePlayerEvent(AbstractClientPlayer player) {
            super(player);
        }
    }
}
