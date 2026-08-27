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
import org.polyfrost.chattweaks.features.CompactChat;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.polyfrost.chattweaks.util.Spacing;
import org.polyfrost.chattweaks.util.TimestampWidths;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
    private static String chattweaks$lastStamp = "";
    @Unique
    private static int chattweaks$stampWidth = 0;

    @Unique
    private static String chattweaks$widthStamp = null;
    @Unique
    private static int chattweaks$widthValue = 0;

    @Unique
    private static int chattweaks$styleRgb = -1;
    @Unique
    private static Style chattweaks$style = null;

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    private int chatScrollbarPos;

    //? if >=26.1 {

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
    private void chattweaks$onAddMessage(Component component, MessageSignature signature, net.minecraft.client.multiplayer.chat.GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        chattweaks$dropBlank(component, ci);
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component chattweaks$decorateMessage(Component component) {
        return chattweaks$decorate(component);
    }

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("RETURN"))
    private void chattweaks$afterAddMessage(Component component, MessageSignature signature, net.minecraft.client.multiplayer.chat.GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        CompactChat.settle(this.allMessages);
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

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("RETURN"))
    private void chattweaks$afterAddMessage(Component component, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        CompactChat.settle(this.allMessages);
    }
    *///?}

    @ModifyExpressionValue(method = {"addMessageToDisplayQueue", "addMessageToQueue"}, at = @At(value = "CONSTANT", args = "intValue=100"), require = 2, allow = 2)
    private int chattweaks$increaseChatHistoryLimit(int original) {
        return ChatTweaks.config.increaseChatHistoryLimit;
    }

    @Unique
    private void chattweaks$untrim(GuiMessage message, int index) {
        if (trimmedMessages.isEmpty()) {
            return;
        }

        int from = -1;
        int to = -1;

        //? if >=26.1 {
        for (int i = 0; i < trimmedMessages.size(); i++) {
            if (trimmedMessages.get(i).parent() == message) {
                if (from < 0) {
                    from = i;
                }
                to = i + 1;
            } else if (from >= 0) {
                break;
            }
        }
        //?} else {
        /*int run = -1;
        for (int i = 0; i < trimmedMessages.size(); i++) {
            if (!trimmedMessages.get(i).endOfEntry()) {
                continue;
            }
            run++;
            if (run == index) {
                from = i;
            } else if (run == index + 1) {
                to = i;
                break;
            }
        }
        if (from >= 0 && to < 0) {
            to = trimmedMessages.size();
        }
        *///?}

        if (from < 0) {
            return;
        }

        trimmedMessages.subList(from, to).clear();

        if (chatScrollbarPos > 0) {
            chatScrollbarPos -= Math.min(to - from, Math.max(0, chatScrollbarPos - from));
        }
    }

    @Unique
    private void chattweaks$dropBlank(Component component, CallbackInfo ci) {
        if (!ChatTweaks.config.removeBlankMessages) {
            return;
        }
        if (ChatUtils.isBlank(component.getString())) {
            ci.cancel();
        }
    }

    @Unique
    private Component chattweaks$decorate(Component component) {
        chattweaks$stampWidth = 0;
        String raw = component.getString();
        Component result = chattweaks$applyTimestamp(component, raw);
        result = chattweaks$applyCompact(result, raw);
        TimestampWidths.put(result, chattweaks$stampWidth);
        return result;
    }

    @Unique
    private Component chattweaks$applyTimestamp(Component component, String raw) {
        if (!ChatTweaks.config.timestamps) {
            return component;
        }
        if (ChatUtils.isBlank(raw)) {
            return component;
        }
        String time = ChatUtils.getCurrentTime();

        if (ChatTweaks.config.timestampsStyle == 0) {
            String stamp = ChatUtils.formatTimestamp(time) + " ";
            if (ChatTweaks.config.onlyNewTimestamps) {
                if (stamp.equals(chattweaks$lastStamp)) {
                    chattweaks$stampWidth = chattweaks$measure(stamp);
                    return Component.empty()
                            .append(Spacing.of(chattweaks$stampWidth))
                            .append(component);
                }
                chattweaks$lastStamp = stamp;
            }
            chattweaks$stampWidth = chattweaks$measure(stamp);
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
    private static int chattweaks$measure(String stamp) {
        if (!stamp.equals(chattweaks$widthStamp)) {
            chattweaks$widthStamp = stamp;
            chattweaks$widthValue = Minecraft.getInstance().font.width(stamp);
        }
        return chattweaks$widthValue;
    }

    @Unique
    private static Style chattweaks$timestampStyle() {
        int rgb = ChatTweaks.config.timestampsColor.getRGB() & 0xFFFFFF;
        if (rgb != chattweaks$styleRgb || chattweaks$style == null) {
            chattweaks$styleRgb = rgb;
            chattweaks$style = Style.EMPTY.withColor(TextColor.fromRgb(rgb));
        }
        return chattweaks$style;
    }

    @Unique
    private Component chattweaks$applyCompact(Component component, String raw) {
        if (!ChatTweaks.config.compactChat) {
            CompactChat.expect(null, null);
            return component;
        }
        String key = ChatUtils.compactKey(raw);
        if (key.isEmpty() || ChatUtils.isDivider(key)) {
            CompactChat.expect(null, null);
            return component;
        }

        long now = System.currentTimeMillis();
        long window = ChatTweaks.config.compactChatTime * 1000L;
        GuiMessage found = CompactChat.touch(key, now, window);
        CompactChat.expect(key, component);

        if (found == null) {
            return component;
        }
        if (ChatTweaks.config.consecutiveCompactChat && (allMessages.isEmpty() || allMessages.getFirst() != found)) {
            return component;
        }
        int dropped = CompactChat.drop(allMessages, found);
        if (dropped < 0) {
            return component;
        }

        int count = ChatUtils.extractCount(found.content().getString()) + 1;
        chattweaks$untrim(found, dropped);

        int rgb = ChatTweaks.config.compactChatColor.getRGB() & 0xFFFFFF;
        Component stacked = component.copy().append(Component.literal(ChatUtils.formatCount(count))
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb))));
        CompactChat.expect(key, stacked);
        return stacked;
    }
}
