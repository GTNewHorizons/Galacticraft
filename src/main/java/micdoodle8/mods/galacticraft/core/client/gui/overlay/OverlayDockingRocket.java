package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OverlayDockingRocket extends Overlay {

    private static final Minecraft minecraft = FMLClientHandler.instance().getClient();

    private static long screenTicks;

    /**
     * Render the GUI when player is docking a vehicle
     */
    public static void renderDockingOverlay() {
        OverlayDockingRocket.screenTicks++;
        final ScaledResolution scaledresolution = ClientUtil.getScaledRes(
                OverlayDockingRocket.minecraft,
                OverlayDockingRocket.minecraft.displayWidth,
                OverlayDockingRocket.minecraft.displayHeight);
        final int width = scaledresolution.getScaledWidth();
        final int height = scaledresolution.getScaledHeight();
        OverlayDockingRocket.minecraft.entityRenderer.setupOverlayRendering();

        if (OverlayDockingRocket.minecraft.thePlayer.ridingEntity instanceof EntityAutoRocket rocket) {
            if (rocket.landing && rocket.targetVec != null) {
                final double dX = Math.round((rocket.posX - rocket.targetVec.x) * 100.0D) / 100.0D;
                final double dY = Math.round((rocket.posY - rocket.targetVec.y) * 100.0D) / 100.0D;
                final double dZ = Math.round((rocket.posZ - rocket.targetVec.z) * 100.0D) / 100.0D;
                final String dXStr = String.valueOf(dX);
                final String dYStr = String.valueOf(dY);
                final String dZStr = String.valueOf(dZ);

                final String warning = GCCoreUtil.translateWithFormat(
                        "gui.dockingRocket.warning.name.0",
                        GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode()));
                final String warning2 = GCCoreUtil.translateWithFormat(
                        "gui.dockingRocket.warning.name.1",
                        GameSettings.getKeyDisplayString(KeyHandlerClient.leftShiftKey.getKeyCode()));

                final double targetMotionY = Math
                        .round(Math.max((rocket.posY - rocket.targetVec.y) / -100.0D, -0.9D) * 100.0D) / 100.0D;
                final double currentMotionY = Math.round(rocket.motionY * 100.0D) / 100.0D;
                final double dMY = Math.floor((targetMotionY - currentMotionY) * 300);
                final int dMotionY = (int) Math.max(1, Math.min(255, dMY));
                final int dMotionYN = (int) Math.max(1, Math.min(255, -dMY));
                final String targetMotionYStr = GCCoreUtil.translate("gui.dockingRocket.targetVel.name") + ": "
                        + targetMotionY;
                final String currentMotionYStr = GCCoreUtil.translate("gui.dockingRocket.currentVel.name") + ": "
                        + currentMotionY;

                final int red = ColorUtil.to32BitColor(dMY > 0 ? 0 : dMotionYN, 255, 255, 255);
                final int green = ColorUtil.to32BitColor(dMY < 0 ? 0 : dMotionY, 255, 255, 255);
                final int grey = ColorUtil.to32BitColor(255, 220, 220, 220);

                if (dMY > 25) {
                    OverlayDockingRocket.minecraft.fontRenderer.drawString(
                            warning,
                            width / 2 - OverlayDockingRocket.minecraft.fontRenderer.getStringWidth(warning) / 2,
                            height / 3 - 50,
                            green);
                }

                if (dMY < -25) {
                    OverlayDockingRocket.minecraft.fontRenderer.drawString(
                            warning2,
                            width / 2 - OverlayDockingRocket.minecraft.fontRenderer.getStringWidth(warning2) / 2,
                            height / 3 - 35,
                            red);
                }

                OverlayDockingRocket.minecraft.fontRenderer.drawString(
                        targetMotionYStr,
                        width - OverlayDockingRocket.minecraft.fontRenderer.getStringWidth(targetMotionYStr) - 50,
                        height / 3 + 50,
                        grey);
                OverlayDockingRocket.minecraft.fontRenderer.drawString(
                        currentMotionYStr,
                        width - OverlayDockingRocket.minecraft.fontRenderer.getStringWidth(currentMotionYStr) - 50,
                        height / 3 + 35,
                        grey);

                OverlayDockingRocket.minecraft.fontRenderer.drawString(
                        GCCoreUtil.translate("gui.dockingRocket.distanceFrom.name"),
                        50,
                        height / 3 + 15,
                        grey);
                OverlayDockingRocket.minecraft.fontRenderer
                        .drawString("X: " + dXStr, 50, height / 3 + 35, Math.abs(dX) > 15 ? red : grey);
                OverlayDockingRocket.minecraft.fontRenderer.drawString(
                        "Y: " + dYStr,
                        50,
                        height / 3 + 45,
                        Math.abs(dY) > 50 || Math.abs(dY) < 1.9 ? grey
                                : OverlayDockingRocket.screenTicks / 10 % 2 == 0 ? red : grey);
                OverlayDockingRocket.minecraft.fontRenderer
                        .drawString("Z: " + dZStr, 50, height / 3 + 55, Math.abs(dZ) > 15 ? red : grey);
            }
        }
    }
}
