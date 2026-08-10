package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.features.ChatHistory;
import org.polyfrost.chattweaks.features.ChatHistoryEntry;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatHistoryAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//? if >=26.1 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}

@Mixin(ChatComponent.class)
public abstract class ChatHistoryMixin implements ChatHistoryAccess {

    @Unique
    private static final int chattweaks$RESTORED_AGE = 1000;

    @Unique
    private static final Set<GuiMessage> chattweaks$markers = Collections.newSetFromMap(new IdentityHashMap<>());

    @Unique
    private static final Map<GuiMessage, Long> chattweaks$sessions = new IdentityHashMap<>();

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    protected abstract void refreshTrimmedMessages();

    @Override
    public boolean chattweaks$isEmpty() {
        return this.allMessages.isEmpty();
    }

    @Override
    public List<ChatHistoryEntry> chattweaks$captureHistory() {
        List<ChatHistoryEntry> entries = new ArrayList<>(this.allMessages.size());
        Set<GuiMessage> markers = Collections.newSetFromMap(new IdentityHashMap<>());
        Map<GuiMessage, Long> sessions = new IdentityHashMap<>();

        for (int i = this.allMessages.size() - 1; i >= 0; i--) {
            GuiMessage message = this.allMessages.get(i);
            if (chattweaks$markers.contains(message)) {
                markers.add(message);
                continue;
            }
            entries.add(ChatHistoryEntry.message(message.content()));
            Long session = chattweaks$sessions.get(message);
            if (session != null) {
                entries.add(ChatHistoryEntry.session(session));
                sessions.put(message, session);
            }
        }

        chattweaks$markers.clear();
        chattweaks$markers.addAll(markers);
        chattweaks$sessions.clear();
        chattweaks$sessions.putAll(sessions);
        return entries;
    }

    @Override
    public void chattweaks$beginSession(List<ChatHistoryEntry> restored, boolean clear, long sessionTime) {
        if (clear) {
            this.allMessages.clear();
            chattweaks$markers.clear();
            chattweaks$sessions.clear();
        }
        if (!restored.isEmpty()) {
            chattweaks$restore(restored);
        }
        if (sessionTime > 0L) {
            chattweaks$startSession(sessionTime);
        }
        this.refreshTrimmedMessages();
    }

    @Unique
    private void chattweaks$restore(List<ChatHistoryEntry> restored) {
        boolean showMarkers = ChatTweaks.config.sessionMarkers;
        int addedTime = ChatCompat.getGuiTicks() - chattweaks$RESTORED_AGE;
        List<GuiMessage> lines = new ArrayList<>(restored.size());
        Set<GuiMessage> markers = Collections.newSetFromMap(new IdentityHashMap<>());
        Map<GuiMessage, Long> sessions = new IdentityHashMap<>();
        GuiMessage previous = null;

        for (ChatHistoryEntry entry : restored) {
            if (!entry.isSession()) {
                previous = chattweaks$line(entry.message(), addedTime);
                lines.add(previous);
                continue;
            }
            if (previous == null) {
                continue;
            }
            sessions.put(previous, entry.sessionTime());
            if (showMarkers) {
                GuiMessage marker = chattweaks$line(ChatHistory.sessionMarker(entry.sessionTime()), addedTime);
                markers.add(marker);
                lines.add(marker);
            }
            previous = null;
        }

        Collections.reverse(lines);
        int room = Math.max(0, ChatTweaks.config.increaseChatHistoryLimit - this.allMessages.size());
        if (lines.size() > room) {
            lines = lines.subList(0, room);
        }
        for (GuiMessage line : lines) {
            if (markers.contains(line)) {
                chattweaks$markers.add(line);
            }
            Long session = sessions.get(line);
            if (session != null) {
                chattweaks$sessions.put(line, session);
            }
        }
        this.allMessages.addAll(lines);
    }

    @Unique
    private void chattweaks$startSession(long sessionTime) {
        while (!this.allMessages.isEmpty() && chattweaks$markers.contains(this.allMessages.getFirst())) {
            chattweaks$markers.remove(this.allMessages.removeFirst());
        }
        if (this.allMessages.isEmpty()) {
            return;
        }
        chattweaks$sessions.put(this.allMessages.getFirst(), sessionTime);
        if (ChatTweaks.config.sessionMarkers) {
            GuiMessage marker = chattweaks$line(ChatHistory.sessionMarker(sessionTime), ChatCompat.getGuiTicks());
            chattweaks$markers.add(marker);
            this.allMessages.add(0, marker);
        }
    }

    @Unique
    private static GuiMessage chattweaks$line(Component content, int addedTime) {
        //? if >=26.1 {
        return new GuiMessage(addedTime, content, null, GuiMessageSource.SYSTEM_CLIENT, null);
        //?} else {
        /*return new GuiMessage(addedTime, content, null, null);
        *///?}
    }
}
