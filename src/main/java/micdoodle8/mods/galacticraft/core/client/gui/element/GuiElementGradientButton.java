package micdoodle8.mods.galacticraft.core.client.gui.element;

import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GuiElementGradientButton extends GuiButton {
    public GuiElementGradientButton(int id, int x, int y, int width, int height, String buttonText) {
        super(id, x, y, width, height, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition
                    && mouseY >= this.yPosition
                    && mouseX < this.xPosition + this.width
                    && mouseY < this.yPosition + this.height;
            final int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            int color = ColorUtil.to32BitColor(150, 10, 10, 10);

            if (!this.enabled) {
                if (this.field_146123_n) {
                    color = ColorUtil.to32BitColor(150, 30, 30, 30);
                } else {
                    color = ColorUtil.to32BitColor(150, 32, 32, 32);
                }
            } else if (this.field_146123_n) {
                color = ColorUtil.to32BitColor(150, 30, 30, 30);
            }

            this.drawGradientRect(
                    this.xPosition,
                    this.yPosition,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    color,
                    color);
            this.mouseDragged(mc, mouseX, mouseY);
            int l = 14737632;

            if (this.packedFGColour != 0) {
                l = this.packedFGColour;
            } else if (!this.enabled) {
                l = 10526880;
            } else if (this.field_146123_n) {
                l = 16777120;
            }

            this.drawCenteredString(
                    fontrenderer,
                    this.displayString,
                    this.xPosition + this.width / 2,
                    this.yPosition + (this.height - 8) / 2,
                    l);
        }
    }
}
