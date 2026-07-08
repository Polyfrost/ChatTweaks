package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.polyfrost.chattweaks.ChatTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Shadow
    protected EditBox input;

    @Shadow
    public abstract void handleChatInput(String message, boolean addToRecentChat);

    //? if >=1.21.10 {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void chattweaks$shiftChat(net.minecraft.client.input.KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    //?} else {
    /*@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void chattweaks$shiftChat(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        var shiftDown = /*?if >= 1.21.10 {*/ event.hasShiftDown(); /*?} else*/ //Screen.hasShiftDown();

        //? if >= 1.21.10
        int keyCode = event.key();

        if (ChatTweaks.config.shiftChat && shiftDown && (keyCode == 257 || keyCode == 335)) {
            handleChatInput(input.getValue(), true);
            input.setValue("");
            cir.setReturnValue(true);
        }
    }
}
