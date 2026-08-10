package org.polyfrost.chattweaks.mixins;

//? if >=26.1 {
import net.minecraft.client.multiplayer.chat.GuiMessage;
//?} else {
/*import net.minecraft.client.GuiMessage;
*///?}
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
    /*@Shadow
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
    *///?} elif <26.1 {
    /*@Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    private double getScale() {
        return 0;
    }

    @Shadow
    private int getLineHeight() {
        return 0;
    }

    @Shadow
    private int getWidth() {
        return 0;
    }

    @Shadow
    public int getLinesPerPage() {
        return 0;
    }

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    public boolean isChatFocused() {
        return false;
    }

    @Shadow
    private boolean isChatHidden() {
        return false;
    }

    // 1.21.11 removed screenToChatX/Y and getMessageLineIndexAt from ChatComponent;
    // reimplement them from the still-present geometry primitives.
    @Unique
    private double chattweaks$screenToChatX(double d) {
        return d / this.getScale() - 4.0;
    }

    @Unique
    private double chattweaks$screenToChatY(double d) {
        double e = net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledHeight() - d - 40.0;
        return e / (this.getScale() * this.getLineHeight());
    }

    @Unique
    private int chattweaks$getMessageLineIndexAt(double d, double e) {
        if (this.isChatFocused() && !this.isChatHidden()) {
            if (!(d < -4.0) && !(d > net.minecraft.util.Mth.floor(this.getWidth() / this.getScale()))) {
                int i = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
                if (e >= 0.0 && e < i) {
                    int j = net.minecraft.util.Mth.floor(e + this.chatScrollbarPos);
                    if (j >= 0 && j < this.trimmedMessages.size()) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }
    *///?} else {
    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    private double getScale() {
        return 0;
    }

    @Shadow
    private int getLineHeight() {
        return 0;
    }

    @Shadow
    private int getWidth() {
        return 0;
    }

    @Shadow
    public int getLinesPerPage() {
        return 0;
    }

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    public boolean isChatFocused() {
        return false;
    }

    // 26.1+ removed screenToChatX/Y, getMessageLineIndexAt AND isChatHidden from
    // ChatComponent; reimplement hit-testing from the surviving geometry primitives.
    @Unique
    private double chattweaks$screenToChatX(double d) {
        return d / this.getScale() - 4.0;
    }

    @Unique
    private double chattweaks$screenToChatY(double d) {
        double e = net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledHeight() - d - 40.0;
        return e / (this.getScale() * this.getLineHeight());
    }

    @Unique
    private int chattweaks$getMessageLineIndexAt(double d, double e) {
        if (this.isChatFocused()) {
            if (!(d < -4.0) && !(d > net.minecraft.util.Mth.floor(this.getWidth() / this.getScale()))) {
                int i = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
                if (e >= 0.0 && e < i) {
                    int j = net.minecraft.util.Mth.floor(e + this.chatScrollbarPos);
                    if (j >= 0 && j < this.trimmedMessages.size()) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }
    //?}

    @Override
    @Nullable
    public String chattweaks$hoveredUrl(double mouseX, double mouseY) {
        //? if <1.21.11 {
        /*double chatX = screenToChatX(mouseX);
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
        *///?} elif <26.1 {
        /*double chatX = chattweaks$screenToChatX(mouseX);
        double chatY = chattweaks$screenToChatY(mouseY);
        int index = chattweaks$getMessageLineIndexAt(chatX, chatY);
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
        *///?} else {
        double chatX = chattweaks$screenToChatX(mouseX);
        double chatY = chattweaks$screenToChatY(mouseY);
        int index = chattweaks$getMessageLineIndexAt(chatX, chatY);
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
        //?}
    }
}
