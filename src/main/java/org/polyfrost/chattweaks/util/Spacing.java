package org.polyfrost.chattweaks.util;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.text.LiteralText;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
//? if > 1.8.9
import net.minecraft.resources.Identifier;

public final class Spacing {
    //? if > 1.8.9
    public static final Identifier FONT = Identifier.fromNamespaceAndPath("chattweaks", "space");

    private static final char FIRST = '\uE000';
    private static final int BITS = 8;
    private static final int MAX_WIDTH = (1 << BITS) - 1;

    //? if >=1.21.10 {
    private static final Style STYLE = Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(FONT));
    //?} elif > 1.8.9 {
    /*private static final Style STYLE = Style.EMPTY.withFont(FONT);
    *///?}

    private Spacing() {
    }

    //? if > 1.8.9 {
    public static Style style() {
        return STYLE;
    }

    public static String text(int width) {
        int clamped = Math.min(Math.max(width, 0), MAX_WIDTH);
        StringBuilder builder = new StringBuilder(BITS);
        for (int bit = 0; bit < BITS; bit++) {
            if ((clamped & (1 << bit)) != 0) {
                builder.append((char) (FIRST + bit));
            }
        }
        return builder.toString();
    }

    public static Component of(int width) {
        return Component.literal(text(width)).withStyle(STYLE);
    }
    //?} else {
    /*public static Component of(int width) {
        int space = Minecraft.getInstance().font.width(" ");
        int bold = width % space;
        int plain = (width - bold * (space + 1)) / space;
        if (plain < 0) {
            plain = Math.round(width / (float) space);
            bold = 0;
        }
        Component spacing = new LiteralText(" ".repeat(plain));
        if (bold > 0) {
            spacing.append(new LiteralText(" ".repeat(bold)).setStyle(new Style().setBold(true)));
        }
        return spacing;
    }
    *///?}

    public static String strip(String in) {
        if (in.isEmpty()) {
            return in;
        }
        StringBuilder builder = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c >= FIRST && c < FIRST + BITS) {
                if (builder == null) {
                    builder = new StringBuilder(in.length()).append(in, 0, i);
                }
                continue;
            }
            if (builder != null) {
                builder.append(c);
            }
        }
        return builder == null ? in : builder.toString();
    }
}
