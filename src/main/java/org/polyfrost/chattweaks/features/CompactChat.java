package org.polyfrost.chattweaks.features;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chattweaks.ChatTweaks;

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

    public static void expect(@Nullable String key, @Nullable Component content) {
        pendingKey = key;
        pendingContent = content;
    }

    public static void settle(List<GuiMessage> allMessages) {
        String key = pendingKey;
        Component content = pendingContent;
        pendingKey = null;
        pendingContent = null;
        if (key == null || allMessages.isEmpty()) {
            return;
        }
        GuiMessage added = allMessages.getFirst();
        if (added.content() == content) {
            track(key, added);
        }
    }

    public static void track(String key, GuiMessage message) {
        Tracked entry = INDEX.get(key);
        if (entry == null) {
            entry = new Tracked();
            INDEX.put(key, entry);
        }
        entry.message = message;
    }

    public static void clear() {
        INDEX.clear();
        pendingKey = null;
        pendingContent = null;
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
