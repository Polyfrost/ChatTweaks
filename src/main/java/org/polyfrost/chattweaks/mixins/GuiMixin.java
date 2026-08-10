package org.polyfrost.chattweaks.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.DeltaTracker;
//? if <26.2 {
/*import net.minecraft.client.gui.Gui;
*///?} else {
import net.minecraft.client.gui.Hud;
//?}
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.features.ChatHistory;
import org.polyfrost.chattweaks.features.ImagePreview;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <26.2 {
/*@Mixin(Gui.class)
*///?} else {
@Mixin(Hud.class)
//?}
public class GuiMixin {
    //? if <26.1 {
    /*@Inject(method = "render", at = @At("TAIL"))
    private void chattweaks$renderImagePreview(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ImagePreview.render(guiGraphics);
    }
    *///?} else {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void chattweaks$renderImagePreview(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ImagePreview.render(guiGraphics);
    }
    //?}

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void chattweaks$saveChatHistory(CallbackInfo ci) {
        ChatHistory.onDisconnected();
    }

    @WrapWithCondition(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"))
    private boolean chattweaks$clearChatHistory(ChatComponent instance, boolean bl) {
        return !ChatTweaks.config.dontClearChatHistory;
    }
}
