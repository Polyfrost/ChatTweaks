package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chattweaks.features.GuiNewChatHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
    @Redirect(
            method = "handleChat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/IChatComponent;)V")
    )
    private void patcher$handleChatDelay(GuiNewChat instance, IChatComponent message, S02PacketChat packetIn) {
        GuiNewChatHook.processChatMessage(packetIn, message);
    }
}
