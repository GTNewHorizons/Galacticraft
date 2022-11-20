package micdoodle8.mods.galacticraft.core.network;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;

public interface IPacketReceiver {
    /**
     * Note this can be called during the init constructor of the entity's
     * superclass, if this is a subclass of the IPacketReceiver entity. So make sure
     * any fields referenced in getNetworkedData() are either in the superclass, or
     * add some null checks!!
     */
    void getNetworkedData(ArrayList<Object> sendData);

    void decodePacketdata(ByteBuf buffer);

    void handlePacketData(Side side, EntityPlayer player);
}
