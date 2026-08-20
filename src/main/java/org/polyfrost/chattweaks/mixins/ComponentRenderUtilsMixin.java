package org.polyfrost.chattweaks.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.chattweaks.util.Spacing;
import org.polyfrost.chattweaks.util.TimestampWidths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ComponentRenderUtils.class)
public class ComponentRenderUtilsMixin {
    @Inject(method = "wrapComponents", at = @At("HEAD"), cancellable = true)
    private static void chattweaks$indentWrappedLines(FormattedText message, int maxWidth, Font font, CallbackInfoReturnable<List<FormattedCharSequence>> cir) {
        if (!(message instanceof Component component)) {
            return;
        }
        int stampWidth = TimestampWidths.get(component);
        if (stampWidth <= 0) {
            return;
        }
        if (stampWidth >= maxWidth) {
            return;
        }

        FormattedCharSequence indent = FormattedCharSequence.forward(Spacing.text(stampWidth), Spacing.style());
        List<FormattedCharSequence> result = new ArrayList<>();
        font.getSplitter().splitLines(chattweaks$collect(message), maxWidth - stampWidth, Style.EMPTY, (text, wrapped) -> {
            FormattedCharSequence line = Language.getInstance().getVisualOrder(text);
            result.add(wrapped ? FormattedCharSequence.composite(indent, line) : line);
        });
        if (result.isEmpty()) {
            result.add(FormattedCharSequence.EMPTY);
        }
        cir.setReturnValue(result);
    }

    @Unique
    private static FormattedText chattweaks$collect(FormattedText message) {
        boolean colors = Minecraft.getInstance().options.chatColors().get();
        ComponentCollector collector = new ComponentCollector();
        message.visit((style, contents) -> {
            collector.append(FormattedText.of(colors ? contents : ChatFormatting.stripFormatting(contents), style));
            return Optional.empty();
        }, Style.EMPTY);
        return collector.getResultOrEmpty();
    }
}
