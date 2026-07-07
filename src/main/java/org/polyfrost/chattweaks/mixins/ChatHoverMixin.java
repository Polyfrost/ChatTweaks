package org.polyfrost.chattweaks.mixins;

//? if >=26.1 {
/*import net.minecraft.client.multiplayer.chat.GuiMessage;
*///?} else {
import net.minecraft.client.GuiMessage;
//?}
import net.minecraft.client.gui.components.ChatComponent;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chattweaks.util.HoveredUrl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatComponent.class)
public abstract class ChatHoverMixin implements HoveredUrl {

    @Unique
    private static final Pattern chattweaks$URL = Pattern.compile("https?://\\S+");

    //? if <1.21.11 {
    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    private double screenToChatX(double d) {
        return 0;
    }

    @Shadow
    private double screenToChatY(double d) {
        return 0;
    }

    @Shadow
    private int getMessageLineIndexAt(double d, double e) {
        return 0;
    }
    //?}

    @Override
    @Nullable
    public String chattweaks$hoveredUrl(double mouseX, double mouseY) {
        //? if <1.21.11 {
        double chatX = screenToChatX(mouseX);
        double chatY = screenToChatY(mouseY);
        int index = getMessageLineIndexAt(chatX, chatY);
        if (index < 0 || index >= trimmedMessages.size()) {
            return null;
        }

        StringBuilder line = new StringBuilder();
        trimmedMessages.get(index).content().accept((idx, style, codePoint) -> {
            line.appendCodePoint(codePoint);
            return true;
        });

        Matcher matcher = chattweaks$URL.matcher(line.toString());
        return matcher.find() ? matcher.group() : null;
        //?} else {
        /*return null;
        *///?}
    }
}
