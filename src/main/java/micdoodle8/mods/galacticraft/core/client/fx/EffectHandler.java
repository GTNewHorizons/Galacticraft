package micdoodle8.mods.galacticraft.core.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.EntityLivingBase;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

@SideOnly(Side.CLIENT)
public class EffectHandler {

    public static void spawnParticle(String particleID, Vector3 position, Vector3 motion, Object... otherInfo) {
        final Minecraft mc = FMLClientHandler.instance().getClient();

        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {
            final double dX = mc.renderViewEntity.posX - position.x;
            final double dY = mc.renderViewEntity.posY - position.y;
            final double dZ = mc.renderViewEntity.posZ - position.z;
            EntityFX particle = null;
            final double viewDistance = 64.0D;

            if ("whiteSmokeIdle".equals(particleID)) {
                particle = new EntityFXLaunchSmoke(mc.theWorld, position, motion, 1.0F, false);
            } else if ("whiteSmokeLaunched".equals(particleID)) {
                particle = new EntityFXLaunchSmoke(mc.theWorld, position, motion, 1.0F, true);
            } else if ("whiteSmokeLargeIdle".equals(particleID)) {
                particle = new EntityFXLaunchSmoke(mc.theWorld, position, motion, 2.5F, false);
            } else if ("whiteSmokeLargeLaunched".equals(particleID)) {
                particle = new EntityFXLaunchSmoke(mc.theWorld, position, motion, 2.5F, true);
            } else if ("launchFlameIdle".equals(particleID)) {
                particle = new EntityFXLaunchFlame(
                        mc.theWorld,
                        position,
                        motion,
                        false,
                        (EntityLivingBase) otherInfo[0]);
            } else if ("launchFlameLaunched".equals(particleID)) {
                particle = new EntityFXLaunchFlame(
                        mc.theWorld,
                        position,
                        motion,
                        true,
                        (EntityLivingBase) otherInfo[0]);
            } else if ("whiteSmokeTiny".equals(particleID)) {
                particle = new EntityFXSmokeSmall(mc.theWorld, position, motion);
            } else if ("distanceSmoke".equals(particleID)
                    && dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance * 1.7) {
                        particle = new EntitySmokeFX(
                                mc.theWorld,
                                position.x,
                                position.y,
                                position.z,
                                motion.x,
                                motion.y,
                                motion.z,
                                2.5F);
                    } else
                if ("oilDrip".equals(particleID)) {
                    particle = new EntityFXOilDrip(mc.theWorld, position.x, position.y, position.z);
                }

            if (dX * dX + dY * dY + dZ * dZ < viewDistance * viewDistance && "oxygen".equals(particleID)) {
                particle = new EntityFXEntityOxygen(mc.theWorld, position, motion, (Vector3) otherInfo[0]);
            }

            if (particle != null) {
                particle.prevPosX = particle.posX;
                particle.prevPosY = particle.posY;
                particle.prevPosZ = particle.posZ;
                mc.effectRenderer.addEffect(particle);
            }
        }
    }
}
