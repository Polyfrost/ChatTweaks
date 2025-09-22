package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.polyfrost.chattweaks.features.CompactChatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class GuiNewChatMixin {
    @Inject(method = "setChatLine", at = @At("HEAD"))
    private void chattweaks$appendMessageCounter(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        CompactChatHandler.appendMessageCounter(chatComponent, displayOnly);
    }

    @Inject(method = "setChatLine", at = @At("TAIL"))
    private void chattweaks$resetMessageHash(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        CompactChatHandler.resetMessageHash();
    }

    @ModifyArg(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", remap = false))
    private Object chattweaks$addMessageHash(Object chatLine) {
        if (chatLine instanceof ChatLine) {
            CompactChatHandler.setChatLine_addToList((ChatLine) chatLine);
        }
        return chatLine;
    }
}
