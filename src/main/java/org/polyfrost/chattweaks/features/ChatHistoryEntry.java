package org.polyfrost.chattweaks.features;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public record ChatHistoryEntry(@Nullable Component message, long sessionTime) {
    public static ChatHistoryEntry message(Component message) {
        return new ChatHistoryEntry(message, 0L);
    }

    public static ChatHistoryEntry session(long sessionTime) {
        return new ChatHistoryEntry(null, sessionTime);
    }

    public boolean isSession() {
        return message == null;
    }
}
