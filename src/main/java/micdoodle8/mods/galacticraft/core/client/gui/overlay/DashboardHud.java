package micdoodle8.mods.galacticraft.core.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.GameSettings;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.dashboard.api.HudBounds;
import com.gtnewhorizons.dashboard.api.HudElement;
import com.gtnewhorizons.dashboard.api.HudRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

/**
 * Lets players move, resize and hide the overlays in Dashboard's HUD editor. The only class using Dashboard, so only
 * call it while Dashboard is loaded. The docking overlay is left out, since its text is spread over the whole screen.
 */
@SideOnly(Side.CLIENT)
public final class DashboardHud {

    private static final int OXYGEN_WIDTH = 54;
    private static final int OXYGEN_HEIGHT = 47;
    private static final int OXYGEN_EDGE_MARGIN = 10;
    private static final int COUNTDOWN_WIDTH = 64;
    private static final int COUNTDOWN_HEIGHT = 32;
    private static final int ROCKET_ALTITUDE_WIDTH = 14;
    private static final int ROCKET_ALTITUDE_HEIGHT = 158;

    private static final HudElement OXYGEN = new HudElement("galacticraft:oxygen", DashboardHud::getOxygenBounds);

    private static final HudElement OXYGEN_WARNING = new HudElement(
            "galacticraft:oxygen_warning",
            DashboardHud::getOxygenWarningBounds);

    private static final HudElement LAUNCH_COUNTDOWN = new HudElement(
            "galacticraft:launch_countdown",
            (screenWidth, screenHeight) -> new HudBounds(
                    screenWidth / 2 - COUNTDOWN_WIDTH / 2,
                    screenHeight / 20 * 4,
                    COUNTDOWN_WIDTH,
                    COUNTDOWN_HEIGHT));

    private static final HudElement LANDER = new HudElement("galacticraft:lander", DashboardHud::getLanderBounds);

    private static final HudElement ROCKET_ALTITUDE = new HudElement(
            "galacticraft:rocket_altitude",
            (screenWidth, screenHeight) -> new HudBounds(
                    0,
                    screenHeight / 2 - 85,
                    ROCKET_ALTITUDE_WIDTH,
                    ROCKET_ALTITUDE_HEIGHT));

    private DashboardHud() {}

    /**
     * The oxygen gauge and warning previews are the real overlays with sample values, which move themselves like in
     * game. The others need a rocket or lander to draw, so they are imitated.
     */
    public static void register() {
        // Normal temperature (22), tanks partly empty. The overlay takes "right" and "top", not left and bottom
        OXYGEN.setPreview(
                (screenWidth, screenHeight) -> OverlayOxygenTanks.renderOxygenTankIndicator(
                        22,
                        30,
                        60,
                        !ConfigManagerCore.oxygenIndicatorLeft,
                        !ConfigManagerCore.oxygenIndicatorBottom,
                        false));
        OXYGEN_WARNING.setPreview((screenWidth, screenHeight) -> OverlayOxygenWarning.renderOxygenWarningOverlay());
        LAUNCH_COUNTDOWN.setPreview(
                (screenWidth, screenHeight) -> HudRegistry
                        .render(LAUNCH_COUNTDOWN, () -> drawCountdownPreview(screenWidth, screenHeight)));
        LANDER.setPreview(
                (screenWidth, screenHeight) -> HudRegistry
                        .render(LANDER, () -> drawLanderPreview(screenWidth, screenHeight)));
        ROCKET_ALTITUDE.setPreview(
                (screenWidth, screenHeight) -> HudRegistry
                        .render(ROCKET_ALTITUDE, () -> drawRocketAltitudePreview(screenWidth, screenHeight)));

        for (HudElement element : new HudElement[] { OXYGEN, OXYGEN_WARNING, LAUNCH_COUNTDOWN, LANDER,
                ROCKET_ALTITUDE }) {
            HudRegistry.register(element.setModId(Constants.MOD_ID_CORE));
        }
    }

    /** Like the real one: a red number at 4 times the normal text size. */
    private static void drawCountdownPreview(int screenWidth, int screenHeight) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String count = "10";
        GL11.glPushMatrix();
        GL11.glScalef(4, 4, 1);
        font.drawString(
                count,
                screenWidth / 8 - font.getStringWidth(count) / 2,
                screenHeight / 20,
                ColorUtil.to32BitColor(255, 255, 0, 0));
        GL11.glPopMatrix();
    }

    /** Like the real one: the double size warning and the speed line below it. */
    private static void drawLanderPreview(int screenWidth, int screenHeight) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        String warning = GCCoreUtil.translate("gui.warning");
        String pressKey = getPressKeyLine();
        String speed = getSpeedLine();

        GL11.glPushMatrix();
        GL11.glScalef(2, 2, 1);
        font.drawString(
                warning,
                screenWidth / 4 - font.getStringWidth(warning) / 2,
                screenHeight / 8 - 20,
                ColorUtil.to32BitColor(255, 255, 0, 0));
        font.drawString(pressKey, screenWidth / 4 - font.getStringWidth(pressKey) / 2, screenHeight / 8, 0xFFFFFF);
        GL11.glPopMatrix();
        font.drawString(speed, screenWidth / 2 - font.getStringWidth(speed) / 2, screenHeight / 3, 0xFFFF00);
    }

    /** A simple stand-in: the bar with the rocket marker halfway up. */
    private static void drawRocketAltitudePreview(int screenWidth, int screenHeight) {
        HudBounds bounds = ROCKET_ALTITUDE.getDefaultBounds(screenWidth, screenHeight);
        Gui.drawRect(bounds.x + 2, bounds.y, bounds.getRight() - 2, bounds.getBottom(), 0xA0303030);
        int markerY = bounds.y + bounds.height / 2;
        Gui.drawRect(bounds.x, markerY - 3, bounds.getRight(), markerY + 3, 0xFFE0E0E0);
    }

    /** The gauge's warning arrow sticks out 5 pixels on the left. */
    private static HudBounds getOxygenBounds(int screenWidth, int screenHeight) {
        int x = ConfigManagerCore.oxygenIndicatorLeft ? OXYGEN_EDGE_MARGIN - 5
                : screenWidth - OXYGEN_EDGE_MARGIN - OXYGEN_WIDTH;
        int y = ConfigManagerCore.oxygenIndicatorBottom ? screenHeight - OXYGEN_HEIGHT - OXYGEN_EDGE_MARGIN
                : OXYGEN_EDGE_MARGIN;
        return new HudBounds(x, y, OXYGEN_WIDTH, OXYGEN_HEIGHT);
    }

    /** "Warning!" and the oxygen setup message, both double size and centered. */
    private static HudBounds getOxygenWarningBounds(int screenWidth, int screenHeight) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int width = 2 * Math.max(
                font.getStringWidth(GCCoreUtil.translate("gui.warning")),
                font.getStringWidth(GCCoreUtil.translate("gui.oxygen.warning")));
        int top = getWarningTop(screenHeight);
        int bottom = screenHeight / 8 * 2 + font.FONT_HEIGHT * 2;
        return new HudBounds(screenWidth / 2 - width / 2, top, width, bottom - top);
    }

    /** The two double size warning lines, plus the normal size speed line at a third of the screen height. */
    private static HudBounds getLanderBounds(int screenWidth, int screenHeight) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int width = Math.max(2 * font.getStringWidth(getPressKeyLine()), font.getStringWidth(getSpeedLine()));
        int top = getWarningTop(screenHeight);
        int bottom = Math.max(screenHeight / 3 + font.FONT_HEIGHT, top + font.FONT_HEIGHT * 2);
        return new HudBounds(screenWidth / 2 - width / 2, top, width, bottom - top);
    }

    private static String getPressKeyLine() {
        return GCCoreUtil.translate("gui.lander.warning2")
                + GameSettings.getKeyDisplayString(KeyHandlerClient.spaceKey.getKeyCode())
                + GCCoreUtil.translate("gui.lander.warning3");
    }

    private static String getSpeedLine() {
        return GCCoreUtil.translate("gui.lander.velocity") + ": -10.0 " + GCCoreUtil.translate("gui.lander.velocityu");
    }

    /** The warnings are drawn at double size, at {@code height / 8 - 20} in their scaled coordinates. */
    private static int getWarningTop(int screenHeight) {
        return (screenHeight / 8 - 20) * 2;
    }
}
