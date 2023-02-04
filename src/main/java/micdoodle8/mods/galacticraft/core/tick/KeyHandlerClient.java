package micdoodle8.mods.galacticraft.core.tick;

import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.KeyHandler;
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore;
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy;
import micdoodle8.mods.galacticraft.core.entities.IControllableEntity;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class KeyHandlerClient extends KeyHandler {

    public static KeyBinding galaxyMap = new KeyBinding(
            GCCoreUtil.translate("keybind.map.name"),
            Keyboard.KEY_NONE,
            Constants.MOD_NAME_SIMPLE);
    public static KeyBinding openFuelGui = new KeyBinding(
            GCCoreUtil.translate("keybind.spaceshipinv.name"),
            Keyboard.KEY_NONE,
            Constants.MOD_NAME_SIMPLE);
    public static KeyBinding toggleAdvGoggles = new KeyBinding(
            GCCoreUtil.translate("keybind.sensortoggle.name"),
            Keyboard.KEY_NONE,
            Constants.MOD_NAME_SIMPLE);
    public static KeyBinding accelerateKey;
    public static KeyBinding decelerateKey;
    public static KeyBinding leftKey;
    public static KeyBinding rightKey;
    public static KeyBinding upKey;
    public static KeyBinding downKey;
    public static KeyBinding spaceKey;
    public static KeyBinding leftShiftKey;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public KeyHandlerClient() {
        super(
                new KeyBinding[] { galaxyMap, openFuelGui, toggleAdvGoggles },
                new boolean[] { false, false, false },
                getVanillaKeyBindings(),
                new boolean[] { false, true, true, true, true, true, true });
    }

    private static KeyBinding[] getVanillaKeyBindings() {
        final KeyBinding invKey = mc.gameSettings.keyBindInventory;
        accelerateKey = mc.gameSettings.keyBindForward;
        decelerateKey = mc.gameSettings.keyBindBack;
        leftKey = mc.gameSettings.keyBindLeft;
        rightKey = mc.gameSettings.keyBindRight;
        upKey = mc.gameSettings.keyBindForward;
        downKey = mc.gameSettings.keyBindBack;
        spaceKey = mc.gameSettings.keyBindJump;
        leftShiftKey = mc.gameSettings.keyBindSneak;
        return new KeyBinding[] { invKey, accelerateKey, decelerateKey, leftKey, rightKey, spaceKey, leftShiftKey };
    }

    @Override
    public void keyDown(Type types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
        if (mc.thePlayer != null && tickEnd) {
            final EntityClientPlayerMP playerBase = PlayerUtil.getPlayerBaseClientFromPlayer(mc.thePlayer, false);

            if (playerBase == null) {
                return;
            }

            final GCPlayerStatsClient stats = GCPlayerStatsClient.get(playerBase);

            if (kb.getKeyCode() == galaxyMap.getKeyCode()) {
                if (mc.currentScreen == null) {
                    mc.thePlayer.openGui(
                            GalacticraftCore.instance,
                            GuiIdsCore.GALAXY_MAP,
                            mc.theWorld,
                            (int) mc.thePlayer.posX,
                            (int) mc.thePlayer.posY,
                            (int) mc.thePlayer.posZ);
                }
            } else if (kb.getKeyCode() == openFuelGui.getKeyCode()) {
                if (playerBase.ridingEntity instanceof EntitySpaceshipBase
                        || playerBase.ridingEntity instanceof EntityBuggy) {
                    GalacticraftCore.packetPipeline.sendToServer(
                            new PacketSimple(
                                    EnumSimplePacket.S_OPEN_FUEL_GUI,
                                    new Object[] { playerBase.getGameProfile().getName() }));
                }
            } else if (kb.getKeyCode() == toggleAdvGoggles.getKeyCode()) {
                stats.usingAdvancedGoggles = !stats.usingAdvancedGoggles;
            }
        }

        if (mc.thePlayer != null && mc.currentScreen == null) {
            int keyNum = -1;

            if (kb == accelerateKey) {
                keyNum = 0;
            } else if (kb == decelerateKey) {
                keyNum = 1;
            } else if (kb == leftKey) {
                keyNum = 2;
            } else if (kb == rightKey) {
                keyNum = 3;
            } else if (kb == spaceKey) {
                keyNum = 4;
            } else if (kb == leftShiftKey) {
                keyNum = 5;
            }

            final Entity entityTest = mc.thePlayer.ridingEntity;

            if (entityTest instanceof IControllableEntity && keyNum != -1) {
                final IControllableEntity entity = (IControllableEntity) entityTest;
                if (kb.getKeyCode() == mc.gameSettings.keyBindInventory.getKeyCode()) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindInventory.getKeyCode(), false);
                }
                entity.pressKey(keyNum);
            } else if (entityTest instanceof EntityAutoRocket) {
                final EntityAutoRocket autoRocket = (EntityAutoRocket) entityTest;
                if (autoRocket.landing) {
                    if (kb == leftShiftKey) {
                        autoRocket.motionY -= 0.02D;
                        GalacticraftCore.packetPipeline.sendToServer(
                                new PacketSimple(
                                        EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y,
                                        new Object[] { autoRocket.getEntityId(), false }));
                    }
                    if (kb == spaceKey) {
                        autoRocket.motionY += 0.02D;
                        GalacticraftCore.packetPipeline.sendToServer(
                                new PacketSimple(
                                        EnumSimplePacket.S_UPDATE_SHIP_MOTION_Y,
                                        new Object[] { autoRocket.getEntityId(), true }));
                    }
                }
            }
        }
    }

    @Override
    public void keyUp(Type types, KeyBinding kb, boolean tickEnd) {}
}
