package org.polyfrost.chattweaks.util;

import org.polyfrost.chattweaks.features.ChatHistoryEntry;

import java.util.List;

public interface ChatHistoryAccess {
    boolean chattweaks$isEmpty();

    List<ChatHistoryEntry> chattweaks$captureHistory();

    void chattweaks$beginSession(List<ChatHistoryEntry> restored, boolean clear, long sessionTime);
}
