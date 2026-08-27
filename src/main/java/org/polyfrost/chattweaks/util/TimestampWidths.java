package org.polyfrost.chattweaks.util;

import net.minecraft.network.chat.Component;

public final class TimestampWidths {
    private TimestampWidths() {
    }

    public static void put(Component message, int width) {
        if (width > 0 && message instanceof MessageMeta meta) {
            meta.chattweaks$setStampWidth(width);
        }
    }

    public static int get(Component message) {
        return message instanceof MessageMeta meta ? meta.chattweaks$getStampWidth() : 0;
    }
}
