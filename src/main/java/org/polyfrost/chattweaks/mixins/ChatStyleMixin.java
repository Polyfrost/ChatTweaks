package org.polyfrost.chattweaks.mixins;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.features.CompactChatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatStyle.class)
abstract class ChatStyleMixin {

    @Shadow
    private HoverEvent chatHoverEvent;
    @Shadow protected abstract ChatStyle getParent();
    @Shadow private ClickEvent chatClickEvent;

    /**
     * @author asbyth
     * @reason Modify hover components with a click action to append what will happen on click
     */
    @Overwrite
    public HoverEvent getChatHoverEvent() {
        HoverEvent hoverEvent = this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
        if (!ChatTweaks.config.safeChatClicks) {
            return hoverEvent;
        }

        ClickEvent chatClickEvent = this.chatClickEvent;
        if (chatClickEvent == null) {
            return hoverEvent;
        }

        ClickEvent.Action action = chatClickEvent.getAction();

        if (!(action.equals(ClickEvent.Action.OPEN_FILE) || action.equals(ClickEvent.Action.OPEN_URL) || action.equals(ClickEvent.Action.RUN_COMMAND))) {
            return hoverEvent;
        }

        String actionMessage = action == ClickEvent.Action.RUN_COMMAND ? "Runs " : "Opens ";
        String msg = "§7" + actionMessage + "§e" + chatClickEvent.getValue() + " §7on click.";
        if (hoverEvent == null) {
            ChatComponentText textComponent = new ChatComponentText(msg);
            this.patcher$appendTimestamp(textComponent);
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent);
        }

        if (hoverEvent.getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
            ChatComponentText textComponent = new ChatComponentText(msg);
            IChatComponent value = hoverEvent.getValue();

            if (value.getSiblings().contains(textComponent) || value.getFormattedText().contains(msg)) {
                return hoverEvent;
            }

            IChatComponent componentCopy = value.createCopy();
            componentCopy.appendText("\n");
            componentCopy.appendText(msg);
            this.patcher$appendTimestamp(componentCopy);

            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentCopy);
        }

        return hoverEvent;
    }

    @Unique
    private void patcher$appendTimestamp(IChatComponent textComponent) {
        if (ChatTweaks.config.timestamps && ChatTweaks.config.timestampsStyle == 1) {
            textComponent.appendText("\n");
            textComponent.appendText("§7Sent at §e" + CompactChatHandler.getCurrentTime() + "§7.");
        }
    }

}
