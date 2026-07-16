package org.polyfrost.chattweaks.mixins;

//? if >=26.1 {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
*///?} else {
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Unique
    private static final Map<String, Long> chattweaks$lastSeen = new HashMap<>();
    @Unique
    private static boolean chattweaks$reentrant = false;
    @Unique
    private static String chattweaks$lastStamp = "";

    //? if >=26.1 {
    /*@Shadow
    private void addMessage(Component component, MessageSignature signature, net.minecraft.client.multiplayer.chat.GuiMessageSource source, GuiMessageTag tag) {
    }
    *///?}
    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    protected abstract void refreshTrimmedMessages();

    //? if >=26.1 {
    /*
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void chattweaks$onAddMessage(Component component, MessageSignature signature, net.minecraft.client.multiplayer.chat.GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        chattweaks$handleAdd(component, signature, source, tag, ci);
    }
    */
    //?} else {
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void chattweaks$onAddMessage(Component component, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        chattweaks$handleAdd(component, signature, null, tag, ci);
    }
    //?}

    @Unique
    private void chattweaks$handleAdd(Component component, MessageSignature signature, Object source, Object tag, CallbackInfo ci) {
        if (chattweaks$reentrant) {
            return;
        }

        String clean = ChatUtils.cleanColor(component.getString()).trim();

        if (ChatTweaks.config.removeBlankMessages && clean.isEmpty()) {
            ci.cancel();
            return;
        }

        Component result = component;
        result = chattweaks$applyTimestamp(result);
        result = chattweaks$applyCompact(result);

        if (result != component) {
            ci.cancel();
            chattweaks$reentrant = true;
            try {
                chattweaks$dispatch(result, signature, source, tag);
            } finally {
                chattweaks$reentrant = false;
            }
        }
    }

    @Unique
    private void chattweaks$dispatch(Component component, MessageSignature signature, Object source, Object tag) {
        //? if >=26.1 {
        /*this.addMessage(component, signature, (net.minecraft.client.multiplayer.chat.GuiMessageSource) source, (GuiMessageTag) tag);
         *///?} else {
        ((ChatComponent) (Object) this).addMessage(component, signature, (GuiMessageTag) tag);
        //?}
    }

    @Unique
    private Component chattweaks$applyTimestamp(Component component) {
        if (!ChatTweaks.config.timestamps) {
            return component;
        }
        String clean = ChatUtils.cleanColor(component.getString()).trim();
        if (clean.isEmpty()) {
            return component;
        }
        String time = ChatUtils.getCurrentTime();

        if (ChatTweaks.config.timestampsStyle == 0) {
            String stamp = ChatUtils.formatTimestamp(time) + " ";
            if (ChatTweaks.config.onlyNewTimestamps) {
                if (stamp.equals(chattweaks$lastStamp)) {
                    return Component.empty()
                            .append(Component.literal(chattweaks$padding(stamp)))
                            .append(component);
                }
                chattweaks$lastStamp = stamp;
            }
            return Component.empty()
                    .append(Component.literal(stamp).withStyle(chattweaks$timestampStyle()))
                    .append(component);
        }

        MutableComponent copy = component.copy();
        Style style = copy.getStyle();
        MutableComponent hoverText = Component.literal("Sent at " + time).withStyle(chattweaks$timestampStyle());
        Component existing = ChatCompat.showTextValue(style.getHoverEvent());
        if (existing != null) {
            hoverText = existing.copy().append("\n").append(hoverText);
        }
        return copy.setStyle(style.withHoverEvent(ChatCompat.showText(hoverText)));
    }

    @Unique
    private Style chattweaks$timestampStyle() {
        return Style.EMPTY.withColor(TextColor.fromRgb(ChatTweaks.config.timestampsColor.getRGB() & 0xFFFFFF));
    }

    @Unique
    private static String chattweaks$padding(String stamp) {
        Font font = Minecraft.getInstance().font;
        int spaceWidth = font.width(" ");
        if (spaceWidth <= 0) {
            return " ".repeat(stamp.length());
        }
        int spaces = Math.round((float) font.width(stamp) / spaceWidth);
        return " ".repeat(Math.max(1, spaces));
    }

    @Unique
    private Component chattweaks$applyCompact(Component component) {
        if (!ChatTweaks.config.compactChat) {
            return component;
        }
        String key = ChatUtils.compactKey(component.getString());
        if (key.isEmpty() || ChatUtils.isDivider(key)) {
            return component;
        }

        long now = System.currentTimeMillis();
        long window = ChatTweaks.config.compactChatTime * 1000L;
        Long last = chattweaks$lastSeen.get(key);
        boolean recent = last != null && (now - last) <= window;
        chattweaks$lastSeen.put(key, now);

        if (!recent) {
            return component;
        }

        GuiMessage found = null;
        if (ChatTweaks.config.consecutiveCompactChat) {
            if (!allMessages.isEmpty() && key.equals(ChatUtils.compactKey(allMessages.getFirst().content().getString()))) {
                found = allMessages.getFirst();
            }
        } else {
            for (GuiMessage message : allMessages) {
                if (key.equals(ChatUtils.compactKey(message.content().getString()))) {
                    found = message;
                    break;
                }
            }
        }

        if (found == null) {
            return component;
        }

        int count = ChatUtils.extractCount(found.content().getString()) + 1;
        allMessages.remove(found);
        refreshTrimmedMessages();

        int rgb = ChatTweaks.config.compactChatColor.getRGB() & 0xFFFFFF;
        return component.copy().append(Component.literal(ChatUtils.formatCount(count))
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
    }
}
