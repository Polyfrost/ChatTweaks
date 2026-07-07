package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    //? if >=26.1 {
    //@Inject(method = "defaultHandleClickEvent", at = @At("HEAD"))
    //private static void chattweaks$safeChatClicksHistory(ClickEvent clickEvent, Minecraft minecraft, Screen screen, CallbackInfo ci) {
    //    if (!ChatTweaks.config.safeChatClicksHistory || !(screen instanceof ChatScreen) || clickEvent == null) {
    //        return;
    //    }
    //    if (ChatCompat.isRunCommand(clickEvent)) {
    //        ChatCompat.addRecentChat(ChatCompat.safeClickValue(clickEvent));
    //    }
    //}
    //?} else {
    @Inject(method = "handleComponentClicked", at = @At("HEAD"))
    private void chattweaks$safeChatClicksHistory(@Nullable Style style, CallbackInfoReturnable<Boolean> cir) {
        if (!ChatTweaks.config.safeChatClicksHistory || style == null || !((Object) this instanceof ChatScreen)) {
            return;
        }
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent != null && ChatCompat.isRunCommand(clickEvent)) {
            ChatCompat.addRecentChat(ChatCompat.safeClickValue(clickEvent));
        }
    }
    //?}
}
