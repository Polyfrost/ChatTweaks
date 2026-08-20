package org.polyfrost.chattweaks.util;

import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class TimestampWidths {
    private static final Map<Component, Integer> WIDTHS = Collections.synchronizedMap(new WeakHashMap<>());

    private TimestampWidths() {
    }

    public static void put(Component message, int width) {
        if (width > 0) {
            WIDTHS.put(message, width);
        }
    }

    public static int get(Component message) {
        Integer width = WIDTHS.get(message);
        return width == null ? 0 : width;
    }
}
