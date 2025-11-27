package org.polyfrost.chattweaks.features;

import dev.deftu.omnicore.api.loader.OmniLoader;
import dev.deftu.textile.Text;
import dev.deftu.textile.minecraft.MCText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.mixins.GuiNewChatAccessor;
import org.polyfrost.oneconfig.api.event.v1.events.ChatEvent;
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent;
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CompactChatHandler {
    private static final Map<Integer, ChatEntry> chatMessageMap = new HashMap<>();
    private static final Map<Integer, Set<ChatLine>> messagesForHash = new HashMap<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final String chatTimestampRegex = "^(?:\\[\\d\\d:\\d\\d(:\\d\\d)?(?: AM| PM|)]|<\\d\\d:\\d\\d>) ";
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public static int currentMessageHash = -1;
    private int ticks;

    @Subscribe(priority = Integer.MIN_VALUE)
    public void onChatMessage(ChatEvent.Receive event) {
        if (!ChatTweaks.config.timestamps || event.getFullyUnformattedMessage().trim().isEmpty()) {
            return;
        }

        String time = getCurrentTime();
        if (ChatTweaks.config.timestampsStyle == 0) {
            ChatComponentIgnored component = new ChatComponentIgnored("§7[" + time + "] §r");
            component.appendSibling(MCText.convert(event.getMessage()));
            event.setMessage(MCText.wrap(component));
        } else if (ChatTweaks.config.timestampsStyle == 1) {
            LinkedList<Text> queue = new LinkedList<>();
            queue.add(event.getMessage());

            while (!queue.isEmpty()) {
                Text textHolder = queue.remove();
                List<Text> siblings = textHolder.getSiblings();

                if (siblings.isEmpty()) {
                    IChatComponent component = MCText.convert(textHolder);
                    HoverEvent hoverEvent = component.getChatStyle().getChatHoverEvent();
                    if (hoverEvent == null) {
                        component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentIgnored("§7Sent at §e" + time + "§7.")));
                    } else {
                        IChatComponent value = hoverEvent.getValue();
                        value.appendText("\n");
                        value.appendText("§7Sent at §e" + time + "§7.");
                    }
                } else {
                    queue.addAll(textHolder.getSiblings());
                }
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent.Start event) {
        if (ticks++ >= 12000) {
            long time = System.currentTimeMillis();
            for (Map.Entry<Integer, ChatEntry> entry : chatMessageMap.entrySet()) {
                if ((time - entry.getValue().lastSeenMessageMillis) > (ChatTweaks.config.compactChatTime * 1000L)) {
                    messagesForHash.remove(entry.getKey());
                }
            }

            ticks = 0;
        }
    }

    @Subscribe
    public void setChatMessageMap(ChatEvent.Receive event) {
        String clearMessage = event.getFullyUnformattedMessage();
        if (clearMessage.isEmpty() && ChatTweaks.config.removeBlankMessages) {
            event.cancelled = true;
        }
    }

    @Subscribe
    public void changeWorld(WorldEvent.Load event) {
        ticks = 0;
    }

    public static void appendMessageCounter(IChatComponent chatComponent, boolean refresh) {
        if ((OmniLoader.isLoaded("hychat") || OmniLoader.isLoaded("labymod")) || !ChatTweaks.config.compactChat || refresh) {
            return;
        }

        String message = cleanColor(chatComponent.getFormattedText()).trim();
        if (message.isEmpty() || isDivider(message)) {
            return;
        }

        currentMessageHash = getChatComponentHash(chatComponent);
        long currentTime = System.currentTimeMillis();

        if (!chatMessageMap.containsKey(currentMessageHash)) {
            chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
        } else {
            ChatEntry entry = chatMessageMap.get(currentMessageHash);
            if ((currentTime - entry.lastSeenMessageMillis) > (ChatTweaks.config.compactChatTime * 1000L)) {
                chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
            } else {
                boolean deleted = deleteMessageByHash(currentMessageHash);
                if (!deleted) {
                    chatMessageMap.put(currentMessageHash, new ChatEntry(1, currentTime));
                } else {
                    entry.messageCount++;
                    entry.lastSeenMessageMillis = currentTime;
                    chatComponent.appendSibling(new ChatComponentIgnored("§7 (" + decimalFormat.format(entry.messageCount) + ")"));
                }
            }
        }
    }

    public static void setChatLine_addToList(ChatLine line) {
        if (currentMessageHash != -1) {
            messagesForHash.computeIfAbsent(currentMessageHash, k -> new HashSet<>()).add(line);
        }
    }

    public static void resetMessageHash() {
        currentMessageHash = -1;
    }

    private static boolean deleteMessageByHash(int hashCode) {
        if (!messagesForHash.containsKey(hashCode) || messagesForHash.get(hashCode).isEmpty()) {
            return false;
        }

        final Set<ChatLine> toRemove = messagesForHash.get(hashCode);
        messagesForHash.remove(hashCode);

        final int normalSearchLength = 100;
        final int wrappedSearchLength = 300;

        boolean removedMessage = false;
        {
            List<ChatLine> chatLines = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getChatLines();
            for (int index = 0; index < chatLines.size() && index < normalSearchLength; index++) {
                final ChatLine chatLine = chatLines.get(index);

                if (toRemove.contains(chatLine)) {
                    removedMessage = true;
                    chatLines.remove(index);
                    index--;

                    if (index < 0 || index >= chatLines.size()) {
                        continue;
                    }

                    index = getMessageIndex(chatLines, index, chatLine);
                } else if (ChatTweaks.config.consecutiveCompactChat) {
                    break;
                }
            }
        }

        if (!removedMessage) {
            return false;
        }

        final List<ChatLine> chatLinesWrapped = ((GuiNewChatAccessor) mc.ingameGUI.getChatGUI()).getDrawnChatLines();
        for (int index = 0; index < chatLinesWrapped.size() && index < wrappedSearchLength; index++) {
            final ChatLine chatLine = chatLinesWrapped.get(index);
            if (toRemove.contains(chatLine)) {
                chatLinesWrapped.remove(index);
                index--;
                if (mc.ingameGUI.getChatGUI().getChatOpen()) {
                    mc.ingameGUI.getChatGUI().scroll(-1);
                }

                if (index <= 0 || index >= chatLinesWrapped.size()) {
                    continue;
                }

                index = getMessageIndex(chatLinesWrapped, index, chatLine);
            } else if (ChatTweaks.config.consecutiveCompactChat) {
                break;
            }
        }

        return true;
    }

    private static int getMessageIndex(List<ChatLine> chatMessageList, int index, ChatLine chatLine) {
        final ChatLine prevLine = chatMessageList.get(index);
        if (isDivider(cleanColor(prevLine.getChatComponent().getUnformattedText())) &&
                Math.abs(chatLine.getUpdatedCounter() - prevLine.getUpdatedCounter()) <= 2) {
            chatMessageList.remove(index);
        }

        if (index >= chatMessageList.size()) {
            return index;
        }

        final ChatLine nextLine = chatMessageList.get(index);
        if (isDivider(cleanColor(nextLine.getChatComponent().getUnformattedText())) &&
                Math.abs(chatLine.getUpdatedCounter() - nextLine.getUpdatedCounter()) <= 2) {
            chatMessageList.remove(index);
        }

        index--;

        return index;
    }

    private static int getChatStyleHash(ChatStyle style) {
        final HoverEvent hoverEvent = style.getChatHoverEvent();
        HoverEvent.Action hoverAction = null;
        int hoverChatHash = 0;

        if (hoverEvent != null) {
            hoverAction = hoverEvent.getAction();
            hoverChatHash = getChatComponentHash(hoverEvent.getValue());
        }

        return Objects.hash(style.getColor(),
                style.getBold(),
                style.getItalic(),
                style.getUnderlined(),
                style.getStrikethrough(),
                style.getObfuscated(),
                hoverAction, hoverChatHash,
                style.getChatClickEvent(),
                style.getInsertion());
    }

    private static int getChatComponentHash(IChatComponent chatComponent) {
        List<Integer> siblingHashes = new ArrayList<>();
        for (IChatComponent sibling : chatComponent.getSiblings()) {
            if (!(sibling instanceof ChatComponentIgnored) && sibling instanceof ChatComponentStyle) {
                siblingHashes.add(getChatComponentHash(sibling));
            }
        }

        if (chatComponent instanceof ChatComponentIgnored) {
            return Objects.hash(siblingHashes);
        }

        String unformattedText = chatComponent.getUnformattedText();
        String cleanedMessage = unformattedText.replaceAll(chatTimestampRegex, "").trim();
        return Objects.hash(cleanedMessage, siblingHashes, getChatStyleHash(chatComponent.getChatStyle()));
    }

    private static boolean isDivider(String clean) {
        clean = clean.replaceAll(chatTimestampRegex, "").trim();
        boolean divider = true;
        if (clean.length() < 5) {
            divider = false;
        } else {
            for (int i = 0; i < clean.length(); i++) {
                final char c = clean.charAt(i);
                if (c != '-' && c != '=' && c != '▬') {
                    divider = false;
                    break;
                }
            }
        }

        return divider;
    }

    private static String cleanColor(String in) {
        return in.replaceAll("(?i)\\u00A7.", "");
    }

    public static String getCurrentTime() {
        String timestampsPattern = "[hh:mm a]";
        if (ChatTweaks.config.secondsOnTimestamps) timestampsPattern = "[hh:mm:ss a]";
        if (ChatTweaks.config.timestampsFormat == 1) {
            timestampsPattern = "[HH:mm]";
            if (ChatTweaks.config.secondsOnTimestamps) timestampsPattern = "[HH:mm:ss]";
        }

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(timestampsPattern));
    }

    static class ChatEntry {
        int messageCount;
        long lastSeenMessageMillis;

        ChatEntry(int messageCount, long lastSeenMessageMillis) {
            this.messageCount = messageCount;
            this.lastSeenMessageMillis = lastSeenMessageMillis;
        }
    }
}
