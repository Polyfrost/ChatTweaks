package org.polyfrost.chattweaks.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.polyfrost.chattweaks.ChatTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)
public class GuiChatMixin {

    @Shadow
    protected GuiTextField inputField;

    @Inject(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V", ordinal = 1), cancellable = true)
    void onKeyTyped$Inject(char typedChar, int keyCode, CallbackInfo ci) {
        if (GuiScreen.isShiftKeyDown() && ChatTweaks.config.shiftChat) {
            ci.cancel();
            inputField.setText("");
        }
    }

    //@WrapOperation(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V", ordinal = 1))
    //void onKeyTyped$WrapOperation(Minecraft instance, GuiScreen i, Operation<Void> original) {
    //    if (!GuiScreen.isShiftKeyDown()) {
    //        original.call(instance, i);
    //    }
    //}

}
