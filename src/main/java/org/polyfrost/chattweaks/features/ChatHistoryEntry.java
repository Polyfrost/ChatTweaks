package org.polyfrost.chattweaks.features;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public record ChatHistoryEntry(@Nullable Component message, long sessionTime, int count) {
    public static ChatHistoryEntry message(Component message, int count) {
        return new ChatHistoryEntry(message, 0L, count);
    }

    public static ChatHistoryEntry session(long sessionTime) {
        return new ChatHistoryEntry(null, sessionTime, 1);
    }

    public boolean isSession() {
        return message == null;
    }
}
