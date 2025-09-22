package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.Opcodes;
import org.polyfrost.chattweaks.features.CompactChatHandler;
import org.polyfrost.chattweaks.features.GuiNewChatHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
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

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getLineCount()I"))
    private void patcher$processMessageQueue(int updateCounter, CallbackInfo ci) {
        GuiNewChatHook.processMessageQueue();
    }

    @Inject(method = "clearChatMessages", at = @At("HEAD"))
    private void patcher$clearMessageQueue(CallbackInfo ci) {
        GuiNewChatHook.messageQueue.clear();
    }

    @Inject(
            method = "drawChat",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 0)),
            at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 0) // Should hopefully match before the if (flag)
    )
    private void patcher$drawMessageQueue(CallbackInfo ci) {
        GuiNewChatHook.drawMessageQueue();
    }
}
