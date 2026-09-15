package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.components.EditBox;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.client.gui.screens.ChatScreen;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.features.ImagePreview;
import org.polyfrost.chattweaks.util.ChatInputAccess;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.21.10 {
import net.minecraft.client.input.KeyEvent;
//?} else {
/*import net.minecraft.client.gui.screens.Screen;
*///?}

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin implements ChatInputAccess {
    @Shadow
    protected EditBox input;

    @Shadow
    public abstract void handleChatInput(String msg, boolean addToRecent);

    @Override
    public EditBox chattweaks$getInput() {
        return this.input;
    }

    //? if <26.1 {
    /*@Inject(method = "render", at = @At("TAIL"))
    private void chattweaks$renderImagePreview(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void chattweaks$renderImagePreview(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    //?}
        ImagePreview.render(graphics, mouseX, mouseY);
    }

    @Inject(method = "normalizeChatMessage", at = @At("HEAD"), cancellable = true)
    private void chattweaks$keepLongCommands(String message, CallbackInfoReturnable<String> cir) {
        if (!ChatTweaks.config.bypassCommandLimit) {
            return;
        }
        String normalized = ChatUtils.normalizeUntrimmed(message);
        if (ChatUtils.isCommand(normalized) && normalized.length() > ChatUtils.VANILLA_CHAT_LIMIT) {
            cir.setReturnValue(normalized);
        }
    }

    //? if >=1.21.10 {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void chattweaks$shiftChat(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        boolean shiftDown = event.hasShiftDown();
        boolean enter = event.isConfirmation();
    //?} else {
    /*@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void chattweaks$shiftChat(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        boolean shiftDown = Screen.hasShiftDown();
        boolean enter = keyCode == 257 || keyCode == 335;
    *///?}

        if (ChatTweaks.config.shiftChat && shiftDown && enter) {
            handleChatInput(input.getValue(), true);
            input.setValue("");
            cir.setReturnValue(true);
        }
    }
}
