package org.polyfrost.chattweaks.mixins;

//? if > 1.8.9 {
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
//? if <26.2 {
/*import net.minecraft.client.gui.Gui;
*///?} else {
import net.minecraft.client.gui.Hud;
//?}
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.features.ChatHistory;
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
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    private void chattweaks$saveChatHistory(CallbackInfo ci) {
        ChatHistory.onDisconnected();
    }

    @WrapWithCondition(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"))
    private boolean chattweaks$clearChatHistory(ChatComponent instance, boolean bl) {
        return !ChatTweaks.config.dontClearChatHistory;
    }
}
//?} else {
/*import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.world.ClientWorld;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.features.ChatHistory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class GuiMixin {
    @Inject(method = "setWorld(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At("HEAD"))
    private void chattweaks$saveChatHistory(ClientWorld world, String message, CallbackInfo ci) {
        if (world == null) {
            ChatHistory.onDisconnected();
        }
    }

    @WrapWithCondition(method = "openScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clear()V"))
    private boolean chattweaks$clearChatHistory(ChatComponent instance) {
        return !ChatTweaks.config.dontClearChatHistory;
    }
}
*///?}
