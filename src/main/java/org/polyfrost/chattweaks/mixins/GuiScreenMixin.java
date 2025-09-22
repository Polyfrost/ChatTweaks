package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.polyfrost.chattweaks.ChatTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin extends Gui {

    @ModifyArg(method = "handleComponentClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;sendChatMessage(Ljava/lang/String;Z)V"), index = 1)
    public boolean patcher$handleComponentClick(boolean addToChat) {
        return addToChat || (ChatTweaks.config.safeChatClicksHistory && ((Object) this) instanceof GuiChat);
    }

}
