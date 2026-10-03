package org.polyfrost.chattweaks.compat;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.resources.Identifier;

public final class GuiGraphics extends GuiElement {
    private static final int TOOLTIP_BACKGROUND = 0xF0100010;
    private static final int TOOLTIP_BORDER_TOP = 0x505000FF;
    private static final int TOOLTIP_BORDER_BOTTOM = 0x5028007F;

    public int drawString(Font font, String text, int x, int y, int color, boolean shadow) {
        return font.draw(text, x, y, color, shadow);
    }

    public void blit(Identifier texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        Minecraft.getInstance().getTextureManager().bind(texture);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GuiElement.drawTexture(x, y, u, v, regionWidth, regionHeight, width, height, textureWidth, textureHeight);
    }

    public void renderTooltipBackground(int x, int y, int width, int height) {
        int right = x + width;
        int bottom = y + height;
        fillGradient(x - 3, y - 4, right + 3, y - 3, TOOLTIP_BACKGROUND, TOOLTIP_BACKGROUND);
        fillGradient(x - 3, bottom + 3, right + 3, bottom + 4, TOOLTIP_BACKGROUND, TOOLTIP_BACKGROUND);
        fillGradient(x - 3, y - 3, right + 3, bottom + 3, TOOLTIP_BACKGROUND, TOOLTIP_BACKGROUND);
        fillGradient(x - 4, y - 3, x - 3, bottom + 3, TOOLTIP_BACKGROUND, TOOLTIP_BACKGROUND);
        fillGradient(right + 3, y - 3, right + 4, bottom + 3, TOOLTIP_BACKGROUND, TOOLTIP_BACKGROUND);
        fillGradient(x - 3, y - 2, x - 2, bottom + 2, TOOLTIP_BORDER_TOP, TOOLTIP_BORDER_BOTTOM);
        fillGradient(right + 2, y - 2, right + 3, bottom + 2, TOOLTIP_BORDER_TOP, TOOLTIP_BORDER_BOTTOM);
        fillGradient(x - 3, y - 3, right + 3, y - 2, TOOLTIP_BORDER_TOP, TOOLTIP_BORDER_TOP);
        fillGradient(x - 3, bottom + 2, right + 3, bottom + 3, TOOLTIP_BORDER_BOTTOM, TOOLTIP_BORDER_BOTTOM);
    }
}
*///?}
