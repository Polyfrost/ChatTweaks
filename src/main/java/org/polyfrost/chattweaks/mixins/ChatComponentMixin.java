package org.polyfrost.chattweaks.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.polyfrost.chattweaks.util.Spacing;
import org.polyfrost.chattweaks.util.TimestampWidths;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if >=26.1 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
//?} else {
/*import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
*///?}

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Unique
    private static final Map<String, Long> chattweaks$lastSeen = new HashMap<>();
    @Unique
    private static String chattweaks$lastStamp = "";
    @Unique
    private static int chattweaks$stampWidth = 0;

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    protected abstract void refreshTrimmedMessages();

    //? if >=26.1 {

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void chattweaks$onAddMessage(Component component, MessageSignature signature, net.minecraft.client.multiplayer.chat.GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        chattweaks$dropBlank(component, ci);
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component chattweaks$decorateMessage(Component component) {
        return chattweaks$decorate(component);
    }

    //?} else {
    /*@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void chattweaks$onAddMessage(Component component, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        chattweaks$dropBlank(component, ci);
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component chattweaks$decorateMessage(Component component) {
        return chattweaks$decorate(component);
    }
    *///?}

    @ModifyExpressionValue(method = {"addMessageToDisplayQueue", "addMessageToQueue"}, at = @At(value = "CONSTANT", args = "intValue=100"), require = 2, allow = 2)
    private int chattweaks$increaseChatHistoryLimit(int original) {
        return ChatTweaks.config.increaseChatHistoryLimit;
    }

    @Unique
    private void chattweaks$dropBlank(Component component, CallbackInfo ci) {
        if (!ChatTweaks.config.removeBlankMessages) {
            return;
        }
        if (ChatUtils.cleanColor(component.getString()).trim().isEmpty()) {
            ci.cancel();
        }
    }

    @Unique
    private Component chattweaks$decorate(Component component) {
        chattweaks$stampWidth = 0;
        Component result = chattweaks$applyTimestamp(component);
        result = chattweaks$applyCompact(result);
        TimestampWidths.put(result, chattweaks$stampWidth);
        return result;
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
                    chattweaks$stampWidth = Minecraft.getInstance().font.width(stamp);
                    return Component.empty()
                            .append(Spacing.of(chattweaks$stampWidth))
                            .append(component);
                }
                chattweaks$lastStamp = stamp;
            }
            chattweaks$stampWidth = Minecraft.getInstance().font.width(stamp);
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
