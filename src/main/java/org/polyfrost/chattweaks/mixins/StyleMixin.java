package org.polyfrost.chattweaks.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public abstract class StyleMixin {

    @Shadow
    @Final
    ClickEvent clickEvent;

    @Inject(method = "getHoverEvent", at = @At("RETURN"), cancellable = true)
    private void chattweaks$safeChatClicks(CallbackInfoReturnable<HoverEvent> cir) {
        if (!ChatTweaks.config.safeChatClicks) {
            return;
        }
        ClickEvent event = this.clickEvent;
        if (event == null) {
            return;
        }
        String value = ChatCompat.safeClickValue(event);
        if (value == null) {
            return;
        }

        String verb = ChatCompat.isRunCommand(event) ? "Runs " : "Opens ";
        MutableComponent hint = Component.literal(verb).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(value).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" on click.").withStyle(ChatFormatting.GRAY));

        HoverEvent current = cir.getReturnValue();
        if (current == null) {
            cir.setReturnValue(ChatCompat.showText(hint));
            return;
        }
        Component existing = ChatCompat.showTextValue(current);
        if (existing != null) {
            if (existing.getString().contains(" on click.")) {
                return;
            }
            cir.setReturnValue(ChatCompat.showText(existing.copy().append("\n").append(hint)));
        }
    }
}
