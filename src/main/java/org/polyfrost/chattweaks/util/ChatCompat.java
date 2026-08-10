package org.polyfrost.chattweaks.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

public final class ChatCompat {
    @Nullable
    public static String safeClickValue(ClickEvent event) {
        //? if >=1.21.5 {
        if (event instanceof ClickEvent.OpenUrl u) return u.uri().toString();
        if (event instanceof ClickEvent.OpenFile f) return f.path();
        if (event instanceof ClickEvent.RunCommand r) return r.command();
        return null;
        //?} else {
        /*ClickEvent.Action action = event.getAction();
        if (action == ClickEvent.Action.OPEN_URL || action == ClickEvent.Action.OPEN_FILE || action == ClickEvent.Action.RUN_COMMAND) {
            return event.getValue();
        }
        return null;
        *///?}
    }

    public static boolean isRunCommand(ClickEvent event) {
        //? if >=1.21.5 {
        return event instanceof ClickEvent.RunCommand;
        //?} else {
        /*return event.getAction() == ClickEvent.Action.RUN_COMMAND;
        *///?}
    }

    public static boolean isOpenUrl(ClickEvent event) {
        //? if >=1.21.5 {
        return event instanceof ClickEvent.OpenUrl;
        //?} else {
        /*return event.getAction() == ClickEvent.Action.OPEN_URL;
        *///?}
    }

    public static HoverEvent showText(Component text) {
        //? if >=1.21.5 {
        return new HoverEvent.ShowText(text);
        //?} else {
        /*return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
        *///?}
    }

    @Nullable
    public static Component showTextValue(@Nullable HoverEvent hover) {
        if (hover == null) {
            return null;
        }
        //? if >=1.21.5 {
        return hover instanceof HoverEvent.ShowText st ? st.value() : null;
        //?} else {
        /*return hover.getAction() == HoverEvent.Action.SHOW_TEXT ? hover.getValue(HoverEvent.Action.SHOW_TEXT) : null;
        *///?}
    }

    public static boolean isKeyDown(int glfwKey) {
        Window window = Minecraft.getInstance().getWindow();
        //? if >=1.21.10 {
        return InputConstants.isKeyDown(window, glfwKey);
        //?} else {
        /*return InputConstants.isKeyDown(window.getWindow(), glfwKey);
        *///?}
    }

    public static ChatComponent getChat() {
        Minecraft mc = Minecraft.getInstance();
        //? if >=26.2 {
        return mc.gui.hud.getChat();
        //?} else {
        /*return mc.gui.getChat();
        *///?}
    }

    public static int getGuiTicks() {
        Minecraft mc = Minecraft.getInstance();
        //? if >=26.2 {
        return mc.gui.hud.getGuiTicks();
        //?} else {
        /*return mc.gui.getGuiTicks();
        *///?}
    }

    public static void addRecentChat(String command) {
        getChat().addRecentChat(command);
    }

    @Nullable
    public static Style hoveredChatStyle(double mouseX, double mouseY) {
        //? if >=1.21.11 {
        return null;
        //?} else {
        /*return getChat().getClickedComponentStyleAt(mouseX, mouseY);
        *///?}
    }
}
