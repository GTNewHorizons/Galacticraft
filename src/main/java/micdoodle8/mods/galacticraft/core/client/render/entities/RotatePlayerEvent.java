package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Fired while a sleeping player is being rotated for rendering - used by the Cryogenic Chamber to lay the player out
 * along the machine instead of along a bed.
 * <p>
 * Note: Galacticraft posts {@link RenderPlayerGC.RotatePlayerEvent}, a deprecated subclass of this event, so that
 * listeners written against the old class keep working. Forge's event bus only notifies listeners registered for the
 * posted class or its supertypes, so listening for this class receives the posted subclass too.
 */
public class RotatePlayerEvent extends PlayerEvent {

    public Boolean shouldRotate = null;
    public boolean vanillaOverride = false;

    public RotatePlayerEvent(AbstractClientPlayer player) {
        super(player);
    }
}
