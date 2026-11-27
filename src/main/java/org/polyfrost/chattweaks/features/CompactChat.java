package org.polyfrost.chattweaks.features;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chattweaks.ChatTweaks;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//? if >=26.1 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}

public final class CompactChat {
    private static final int MIN_TRACKED = 1024;

    private static final class Tracked {
        long time;
        @Nullable
        GuiMessage message;
        int count = 1;
    }

    private static final Map<String, Tracked> INDEX = new LinkedHashMap<>(256, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Tracked> eldest) {
            return size() > Math.max(MIN_TRACKED, ChatTweaks.config.increaseChatHistoryLimit);
        }
    };

    @Nullable
    private static String pendingKey;
    @Nullable
    private static Component pendingContent;
    private static int pendingCount = 1;

    private CompactChat() {
    }

    @Nullable
    public static GuiMessage touch(String key, long now, long window) {
        Tracked entry = INDEX.get(key);
        if (entry == null) {
            entry = new Tracked();
            INDEX.put(key, entry);
        }
        GuiMessage previous = (now - entry.time) <= window ? entry.message : null;
        entry.time = now;
        return previous;
    }

    public static int count(String key) {
        Tracked entry = INDEX.get(key);
        return entry == null ? 1 : entry.count;
    }

    public static Map<GuiMessage, Integer> counts() {
        Map<GuiMessage, Integer> counts = new IdentityHashMap<>();
        for (Tracked entry : INDEX.values()) {
            if (entry.message != null && entry.count > 1) {
                counts.put(entry.message, entry.count);
            }
        }
        return counts;
    }

    public static void expect(@Nullable String key, @Nullable Component content, int count) {
        pendingKey = key;
        pendingContent = content;
        pendingCount = count;
    }

    public static void settle(List<GuiMessage> allMessages) {
        String key = pendingKey;
        Component content = pendingContent;
        int count = pendingCount;
        pendingKey = null;
        pendingContent = null;
        pendingCount = 1;
        if (key == null || allMessages.isEmpty()) {
            return;
        }
        GuiMessage added = allMessages.getFirst();
        if (added.content() == content) {
            track(key, added, count);
        }
    }

    public static void track(String key, GuiMessage message, int count) {
        Tracked entry = INDEX.get(key);
        if (entry == null) {
            entry = new Tracked();
            INDEX.put(key, entry);
        }
        entry.message = message;
        entry.count = count;
    }

    public static void clear() {
        INDEX.clear();
        pendingKey = null;
        pendingContent = null;
        pendingCount = 1;
    }

    public static int drop(List<GuiMessage> allMessages, GuiMessage message) {
        for (int i = 0; i < allMessages.size(); i++) {
            if (allMessages.get(i) == message) {
                allMessages.remove(i);
                return i;
            }
        }
        return -1;
    }
}
