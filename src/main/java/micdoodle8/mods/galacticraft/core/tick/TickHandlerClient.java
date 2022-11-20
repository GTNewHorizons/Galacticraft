package micdoodle8.mods.galacticraft.core.tick;

import com.google.common.collect.Lists;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.entity.IEntityNoisy;
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.api.vector.BlockTuple;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.CloudRenderer;
import micdoodle8.mods.galacticraft.core.client.SkyProviderMoon;
import micdoodle8.mods.galacticraft.core.client.SkyProviderOverworld;
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayDockingRocket;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayLander;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayLaunchCountdown;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayOxygenTanks;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayOxygenWarning;
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlayRocket;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiNewSpaceRace;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient;
import micdoodle8.mods.galacticraft.core.items.ItemSensorGlasses;
import micdoodle8.mods.galacticraft.core.network.PacketRotateRocket;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer;
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen;
import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.MapUtil;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;
import micdoodle8.mods.galacticraft.core.wrappers.BlockMetaList;
import micdoodle8.mods.galacticraft.core.wrappers.Footprint;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class TickHandlerClient {
    public static int airRemaining;
    public static int airRemaining2;
    private static boolean lastInvKeyPressed;
    private static long tickCount;
    public static boolean spaceRaceGuiScheduled = false;

    public static HashSet<TileEntityScreen> screenConnectionsUpdateList = new HashSet<TileEntityScreen>();

    static {
        registerDetectableBlocks(true);
    }

    public static void registerDetectableBlocks(boolean logging) {
        ClientProxyCore.detectableBlocks.clear();

        for (final String s : ConfigManagerCore.detectableIDs) {
            final BlockTuple bt = ConfigManagerCore.stringToBlock(s, "External Detectable IDs", logging);
            if (bt == null) {
                continue;
            }

            int meta = bt.meta;
            if (meta == -1) {
                meta = 0;
            }

            boolean flag = false;
            for (final BlockMetaList blockMetaList : ClientProxyCore.detectableBlocks) {
                if (blockMetaList.getBlock() == bt.block) {
                    if (!blockMetaList.getMetaList().contains(meta)) {
                        blockMetaList.getMetaList().add(meta);
                    }
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                final List<Integer> metaList = Lists.newArrayList();
                metaList.add(meta);
                ClientProxyCore.detectableBlocks.add(new BlockMetaList(bt.block, metaList));
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityPlayerSP player = minecraft.thePlayer;
        final EntityClientPlayerMP playerBaseClient = PlayerUtil.getPlayerBaseClientFromPlayer(player, false);
        GCPlayerStatsClient stats = null;

        if (player != null) {
            stats = GCPlayerStatsClient.get(playerBaseClient);
        }

        if (event.phase == Phase.END) {
            if (minecraft.currentScreen instanceof GuiIngameMenu) {
                final int i = Mouse.getEventX() * minecraft.currentScreen.width / minecraft.displayWidth;
                final int j = minecraft.currentScreen.height
                        - Mouse.getEventY() * minecraft.currentScreen.height / minecraft.displayHeight
                        - 1;

                int k = Mouse.getEventButton();

                if (Minecraft.isRunningOnMac && k == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
                    k = 1;
                }

                int deltaColor = 0;

                if (i > minecraft.currentScreen.width - 100 && j > minecraft.currentScreen.height - 35) {
                    deltaColor = 20;

                    if (k == 0) {
                        if (Mouse.getEventButtonState()) {
                            minecraft.displayGuiScreen(new GuiNewSpaceRace(playerBaseClient));
                        }
                    }
                }

                this.drawGradientRect(
                        minecraft.currentScreen.width - 100,
                        minecraft.currentScreen.height - 35,
                        minecraft.currentScreen.width,
                        minecraft.currentScreen.height,
                        ColorUtil.to32BitColor(150, 10 + deltaColor, 10 + deltaColor, 10 + deltaColor),
                        ColorUtil.to32BitColor(250, 10 + deltaColor, 10 + deltaColor, 10 + deltaColor));
                minecraft.fontRenderer.drawString(
                        GCCoreUtil.translate("gui.spaceRace.create.title.name.0"),
                        minecraft.currentScreen.width
                                - 50
                                - minecraft.fontRenderer.getStringWidth(
                                                GCCoreUtil.translate("gui.spaceRace.create.title.name.0"))
                                        / 2,
                        minecraft.currentScreen.height - 26,
                        ColorUtil.to32BitColor(255, 240, 240, 240));
                minecraft.fontRenderer.drawString(
                        GCCoreUtil.translate("gui.spaceRace.create.title.name.1"),
                        minecraft.currentScreen.width
                                - 50
                                - minecraft.fontRenderer.getStringWidth(
                                                GCCoreUtil.translate("gui.spaceRace.create.title.name.1"))
                                        / 2,
                        minecraft.currentScreen.height - 16,
                        ColorUtil.to32BitColor(255, 240, 240, 240));
                Gui.drawRect(
                        minecraft.currentScreen.width - 100,
                        minecraft.currentScreen.height - 35,
                        minecraft.currentScreen.width - 99,
                        minecraft.currentScreen.height,
                        ColorUtil.to32BitColor(255, 0, 0, 0));
                Gui.drawRect(
                        minecraft.currentScreen.width - 100,
                        minecraft.currentScreen.height - 35,
                        minecraft.currentScreen.width,
                        minecraft.currentScreen.height - 34,
                        ColorUtil.to32BitColor(255, 0, 0, 0));
            }

            if (player != null) {
                ClientProxyCore.playerPosX = player.prevPosX + (player.posX - player.prevPosX) * event.renderTickTime;
                ClientProxyCore.playerPosY = player.prevPosY + (player.posY - player.prevPosY) * event.renderTickTime;
                ClientProxyCore.playerPosZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.renderTickTime;
                ClientProxyCore.playerRotationYaw =
                        player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.renderTickTime;
                ClientProxyCore.playerRotationPitch = player.prevRotationPitch
                        + (player.rotationPitch - player.prevRotationPitch) * event.renderTickTime;
            }

            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntityTier1Rocket) {
                float f = (((EntityTier1Rocket) player.ridingEntity).timeSinceLaunch - 250F) / 175F;

                if (f < 0) {
                    f = 0F;
                }

                if (f > 1) {
                    f = 1F;
                }

                final ScaledResolution scaledresolution =
                        ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight);
                scaledresolution.getScaledWidth();
                scaledresolution.getScaledHeight();
                minecraft.entityRenderer.setupOverlayRendering();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (minecraft.currentScreen == null
                    && player != null
                    && player.ridingEntity != null
                    && player.ridingEntity instanceof EntitySpaceshipBase
                    && minecraft.gameSettings.thirdPersonView != 0
                    && !minecraft.gameSettings.hideGUI) {
                OverlayRocket.renderSpaceshipOverlay(((EntitySpaceshipBase) player.ridingEntity).getSpaceshipGui());
            }

            if (minecraft.currentScreen == null
                    && player != null
                    && player.ridingEntity != null
                    && player.ridingEntity instanceof EntityLander
                    && minecraft.gameSettings.thirdPersonView != 0
                    && !minecraft.gameSettings.hideGUI) {
                OverlayLander.renderLanderOverlay();
            }

            if (minecraft.currentScreen == null
                    && player != null
                    && player.ridingEntity != null
                    && player.ridingEntity instanceof EntityAutoRocket
                    && minecraft.gameSettings.thirdPersonView != 0
                    && !minecraft.gameSettings.hideGUI) {
                OverlayDockingRocket.renderDockingOverlay();
            }

            if (minecraft.currentScreen == null
                    && player != null
                    && player.ridingEntity != null
                    && player.ridingEntity instanceof EntitySpaceshipBase
                    && minecraft.gameSettings.thirdPersonView != 0
                    && !minecraft.gameSettings.hideGUI
                    && ((EntitySpaceshipBase) minecraft.thePlayer.ridingEntity).launchPhase
                            != EnumLaunchPhase.LAUNCHED.ordinal()) {
                OverlayLaunchCountdown.renderCountdownOverlay();
            }

            if (player != null
                    && (ConfigManagerCore.alwaysDisplayOxygenHUD
                            || player.worldObj.provider instanceof IGalacticraftWorldProvider
                                    && OxygenUtil.shouldDisplayTankGui(minecraft.currentScreen)
                                    && OxygenUtil.noAtmosphericCombustion(player.worldObj.provider))) {
                int var6 = (TickHandlerClient.airRemaining - 90) * -1;

                if (TickHandlerClient.airRemaining <= 0) {
                    var6 = 90;
                }

                int var7 = (TickHandlerClient.airRemaining2 - 90) * -1;

                if (TickHandlerClient.airRemaining2 <= 0) {
                    var7 = 90;
                }

                final int thermalLevel = stats.thermalLevel + 22;
                OverlayOxygenTanks.renderOxygenTankIndicator(
                        thermalLevel,
                        var6,
                        var7,
                        !ConfigManagerCore.oxygenIndicatorLeft,
                        !ConfigManagerCore.oxygenIndicatorBottom,
                        Math.abs(thermalLevel - 22) >= 10 && !stats.thermalLevelNormalising);
            }

            if (playerBaseClient != null
                    && player.worldObj.provider instanceof IGalacticraftWorldProvider
                    && !stats.oxygenSetupValid
                    && OxygenUtil.noAtmosphericCombustion(player.worldObj.provider)
                    && minecraft.currentScreen == null
                    && !playerBaseClient.capabilities.isCreativeMode) {
                OverlayOxygenWarning.renderOxygenWarningOverlay();
            }
        }
    }

    @SubscribeEvent
    public void onPreGuiRender(RenderGameOverlayEvent.Pre event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final EntityClientPlayerMP player = minecraft.thePlayer;

        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            if (player != null
                    && player.ridingEntity != null
                    && player.ridingEntity instanceof IIgnoreShift
                    && ((IIgnoreShift) player.ridingEntity).shouldIgnoreShiftExit()) {
                // Remove "Press shift to dismount" message when shift-exiting is disabled (not
                // ideal, but the only
                // option)
                final String str = I18n.format("mount.onboard", new Object[] {
                    GameSettings.getKeyDisplayString(minecraft.gameSettings.keyBindSneak.getKeyCode())
                });
                if (minecraft.ingameGUI.recordPlaying.equals(str)) {
                    minecraft.ingameGUI.recordPlaying = "";
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final WorldClient world = minecraft.theWorld;
        final EntityClientPlayerMP player = minecraft.thePlayer;

        if (event.phase == Phase.START) {
            if (TickHandlerClient.tickCount >= Long.MAX_VALUE) {
                TickHandlerClient.tickCount = 0;
            }

            TickHandlerClient.tickCount++;

            if (TickHandlerClient.tickCount % 20 == 0) {
                for (final List<Footprint> fpList : ClientProxyCore.footprintRenderer.footprints.values()) {
                    final Iterator<Footprint> fpIt = fpList.iterator();
                    while (fpIt.hasNext()) {
                        final Footprint fp = fpIt.next();
                        fp.age += 20;

                        if (fp.age >= Footprint.MAX_AGE) {
                            fpIt.remove();
                        }
                    }
                }

                if (player != null
                        && player.inventory.armorItemInSlot(3) != null
                        && player.inventory.armorItemInSlot(3).getItem() instanceof ItemSensorGlasses) {
                    ClientProxyCore.valueableBlocks.clear();

                    for (int i = -4; i < 5; i++) {
                        final int x = MathHelper.floor_double(player.posX + i);
                        for (int j = -4; j < 5; j++) {
                            final int y = MathHelper.floor_double(player.posY + j);
                            for (int k = -4; k < 5; k++) {
                                final int z = MathHelper.floor_double(player.posZ + k);

                                final Block block = player.worldObj.getBlock(x, y, z);

                                if (block.getMaterial() != Material.air) {
                                    final int metadata = world.getBlockMetadata(x, y, z);
                                    boolean isDetectable = false;

                                    for (final BlockMetaList blockMetaList : ClientProxyCore.detectableBlocks) {
                                        if (blockMetaList.getBlock() == block
                                                && blockMetaList.getMetaList().contains(metadata)) {
                                            isDetectable = true;
                                            break;
                                        }
                                    }

                                    if (isDetectable
                                            || block instanceof IDetectableResource
                                                    && ((IDetectableResource) block).isValueable(metadata)) {
                                        ClientProxyCore.valueableBlocks.add(new BlockVec3(x, y, z));
                                    }
                                }
                            }
                        }
                    }
                    final TileEntityOxygenSealer nearestSealer = TileEntityOxygenSealer.getNearestSealer(
                            world,
                            MathHelper.floor_double(player.posX),
                            MathHelper.floor_double(player.posY),
                            MathHelper.floor_double(player.posZ));
                    // TODO: revert. Correct code is temporarily commented out for testing render
                    if (nearestSealer != null) // && nearestSealer.threadSeal != null)
                    {
                        ClientProxyCore.leakTrace = new ArrayList(); // nearestSealer.threadSeal.leakTrace;
                        // TODO: revert. Temporarily for testing purposes any sealer should show a leak
                        // block directly
                        // above itself
                        ClientProxyCore.leakTrace.add(new BlockVec3(nearestSealer).translate(0, 1, 0));
                    }
                }

                if (MapUtil.resetClientFlag.getAndSet(false)) {
                    MapUtil.resetClientBody();
                }
            }

            if (minecraft.currentScreen instanceof GuiMainMenu) {
                ClientProxyCore.reset();
            }

            if (world != null
                    && TickHandlerClient.spaceRaceGuiScheduled
                    && minecraft.currentScreen == null
                    && ConfigManagerCore.enableSpaceRaceManagerPopup) {
                player.openGui(
                        GalacticraftCore.instance,
                        GuiIdsCore.SPACE_RACE_START,
                        player.worldObj,
                        (int) player.posX,
                        (int) player.posY,
                        (int) player.posZ);
                TickHandlerClient.spaceRaceGuiScheduled = false;
            }

            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySpaceshipBase) {
                final EntitySpaceshipBase rocket = (EntitySpaceshipBase) player.ridingEntity;
                if (rocket.prevRotationPitch != rocket.rotationPitch || rocket.prevRotationYaw != rocket.rotationYaw) {
                    GalacticraftCore.packetPipeline.sendToServer(new PacketRotateRocket(player.ridingEntity));
                }
            }

            if (world != null) {
                if (world.provider instanceof WorldProviderSurface) {
                    if (world.provider.getSkyRenderer() == null
                            && player.ridingEntity instanceof EntitySpaceshipBase
                            && player.ridingEntity.posY > Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                        world.provider.setSkyRenderer(new SkyProviderOverworld());
                    } else if (world.provider.getSkyRenderer() instanceof SkyProviderOverworld
                            && player.posY <= Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                        world.provider.setSkyRenderer(null);
                    }
                } else if (world.provider instanceof WorldProviderSpaceStation) {
                    if (world.provider.getSkyRenderer() == null) {
                        ((WorldProviderSpaceStation) world.provider).createSkyProvider();
                        GCPlayerStatsClient.get(player).inFreefallFirstCheck = false;
                    }
                } else if (world.provider instanceof WorldProviderMoon) {
                    if (world.provider.getSkyRenderer() == null) {
                        world.provider.setSkyRenderer(new SkyProviderMoon());
                    }

                    if (world.provider.getCloudRenderer() == null) {
                        world.provider.setCloudRenderer(new CloudRenderer());
                    }
                }
            }

            if (player != null && player.ridingEntity != null && player.ridingEntity instanceof EntitySpaceshipBase) {
                final EntitySpaceshipBase ship = (EntitySpaceshipBase) player.ridingEntity;
                boolean hasChanged = false;

                if (minecraft.gameSettings.keyBindLeft.getIsKeyPressed()) {
                    ship.turnYaw(-1.0F);
                    hasChanged = true;
                }

                if (minecraft.gameSettings.keyBindRight.getIsKeyPressed()) {
                    ship.turnYaw(1.0F);
                    hasChanged = true;
                }

                if (minecraft.gameSettings.keyBindForward.getIsKeyPressed()) {
                    if (ship.getLaunched()) {
                        ship.turnPitch(-0.7F);
                        hasChanged = true;
                    }
                }

                if (minecraft.gameSettings.keyBindBack.getIsKeyPressed()) {
                    if (ship.getLaunched()) {
                        ship.turnPitch(0.7F);
                        hasChanged = true;
                    }
                }

                if (hasChanged) {
                    GalacticraftCore.packetPipeline.sendToServer(new PacketRotateRocket(ship));
                }
            }

            if (world != null) {
                final List entityList = world.loadedEntityList;
                for (final Object e : entityList) {
                    if (e instanceof IEntityNoisy) {
                        final IEntityNoisy vehicle = (IEntityNoisy) e;
                        if (vehicle.getSoundUpdater() == null) {
                            final ISound noise = vehicle.setSoundUpdater(
                                    FMLClientHandler.instance().getClient().thePlayer);
                            FMLClientHandler.instance()
                                    .getClient()
                                    .getSoundHandler()
                                    .playSound(noise);
                        }
                    }
                }
            }

            if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection) {
                player.motionY = 0;
            }

            if (world != null
                    && world.provider instanceof IGalacticraftWorldProvider
                    && OxygenUtil.noAtmosphericCombustion(world.provider)) {
                world.setRainStrength(0.0F);
            }

            if (!KeyHandlerClient.spaceKey.getIsKeyPressed()) {
                ClientProxyCore.lastSpacebarDown = false;
            }

            if (player != null
                    && player.ridingEntity != null
                    && KeyHandlerClient.spaceKey.getIsKeyPressed()
                    && !ClientProxyCore.lastSpacebarDown) {
                GalacticraftCore.packetPipeline.sendToServer(
                        new PacketSimple(EnumSimplePacket.S_IGNITE_ROCKET, new Object[] {}));
                ClientProxyCore.lastSpacebarDown = true;
            }

            if (!this.screenConnectionsUpdateList.isEmpty()) {
                final HashSet<TileEntityScreen> updateListCopy =
                        (HashSet<TileEntityScreen>) screenConnectionsUpdateList.clone();
                screenConnectionsUpdateList.clear();
                for (final TileEntityScreen te : updateListCopy) {
                    if (te.refreshOnUpdate) {
                        te.refreshConnections(true);
                    }
                }
            }
        }
    }

    private boolean alreadyContainsBlock(int x1, int y1, int z1) {
        return ClientProxyCore.valueableBlocks.contains(new BlockVec3(x1, y1, z1));
    }

    public static void zoom(float value) {
        FMLClientHandler.instance().getClient().entityRenderer.thirdPersonDistance = value;
        FMLClientHandler.instance().getClient().entityRenderer.thirdPersonDistanceTemp = value;
    }

    private void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
        final float f = (par5 >> 24 & 255) / 255.0F;
        final float f1 = (par5 >> 16 & 255) / 255.0F;
        final float f2 = (par5 >> 8 & 255) / 255.0F;
        final float f3 = (par5 & 255) / 255.0F;
        final float f4 = (par6 >> 24 & 255) / 255.0F;
        final float f5 = (par6 >> 16 & 255) / 255.0F;
        final float f6 = (par6 >> 8 & 255) / 255.0F;
        final float f7 = (par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(par3, par2, 0.0D);
        tessellator.addVertex(par1, par2, 0.0D);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(par1, par4, 0.0D);
        tessellator.addVertex(par3, par4, 0.0D);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
