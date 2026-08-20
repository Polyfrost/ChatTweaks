package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatInputAccess;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {

    @Shadow
    private int maxLength;

    @Shadow
    public abstract String getValue();

    @Inject(method = "insertText", at = @At("HEAD"))
    private void chattweaks$raiseLimitForInsert(String text, CallbackInfo ci) {
        String value = getValue();
        chattweaks$updateChatLimit(ChatUtils.isCommand(value) || (value.isEmpty() && ChatUtils.isCommand(text)));
    }

    @Inject(method = "setValue", at = @At("HEAD"))
    private void chattweaks$raiseLimitForValue(String value, CallbackInfo ci) {
        chattweaks$updateChatLimit(ChatUtils.isCommand(value));
    }

    @Unique
    private void chattweaks$updateChatLimit(boolean command) {
        if (ChatTweaks.config == null || !ChatTweaks.config.bypassCommandLimit) {
            return;
        }
        if (!(ChatCompat.getScreen() instanceof ChatScreen screen)) {
            return;
        }
        if (((ChatInputAccess) screen).chattweaks$getInput() != (Object) this) {
            return;
        }
        this.maxLength = command ? ChatUtils.COMMAND_LIMIT : ChatUtils.VANILLA_CHAT_LIMIT;
    }
}
