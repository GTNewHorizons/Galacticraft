package micdoodle8.mods.galacticraft.planets.asteroids.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityBeamOutput;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class NetworkRenderer {
    static long t = -1;
    static List<TileEntityBeamOutput> nodes = new ArrayList<TileEntityBeamOutput>();

    public static void renderNetworks(World world, float partialTicks) {
        if (System.currentTimeMillis() > t + 1000) {
            t = System.currentTimeMillis();
            nodes.clear();
            for (final Object o : new ArrayList<TileEntity>(world.loadedTileEntityList)) {
                if (o instanceof TileEntityBeamOutput) {
                    nodes.add((TileEntityBeamOutput) o);
                }
            }
        }

        if (nodes.isEmpty()) {
            return;
        }

        final Tessellator tess = Tessellator.instance;
        final EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
        final double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        for (final TileEntityBeamOutput tileEntity : nodes) {
            if (tileEntity.getTarget() == null) {
                continue;
            }

            GL11.glPushMatrix();

            final Vector3 outputPoint = tileEntity.getOutputPoint(true);
            final Vector3 targetInputPoint = tileEntity.getTarget().getInputPoint();

            final Vector3 direction = Vector3.subtract(outputPoint, targetInputPoint);
            final float directionLength = (float) direction.getMagnitude();

            final float posX = (float) (tileEntity.xCoord - interpPosX);
            final float posY = (float) (tileEntity.yCoord - interpPosY);
            final float posZ = (float) (tileEntity.zCoord - interpPosZ);
            GL11.glTranslatef(posX, posY, posZ);

            GL11.glTranslatef(
                    outputPoint.floatX() - tileEntity.xCoord,
                    outputPoint.floatY() - tileEntity.yCoord,
                    outputPoint.floatZ() - tileEntity.zCoord);
            GL11.glRotatef(tileEntity.yaw + 180, 0, 1, 0);
            GL11.glRotatef(-tileEntity.pitch, 1, 0, 0);
            GL11.glRotatef(tileEntity.ticks * 10, 0, 0, 1);

            GL11.glColor4f(
                    tileEntity.getColor().floatX(),
                    tileEntity.getColor().floatY(),
                    tileEntity.getColor().floatZ(),
                    1.0F);
            tess.startDrawing(GL11.GL_LINES);

            for (final ForgeDirection dir : ForgeDirection.values()) {
                tess.addVertex(dir.offsetX / 40.0F, dir.offsetY / 40.0F, dir.offsetZ / 40.0F);
                tess.addVertex(dir.offsetX / 40.0F, dir.offsetY / 40.0F, directionLength + dir.offsetZ / 40.0F);
            }

            tess.draw();

            GL11.glPopMatrix();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glColor4f(1, 1, 1, 1);
    }
}
