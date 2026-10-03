package org.polyfrost.chattweaks.mixins;

//? if > 1.8.9 {
import net.minecraft.ChatFormatting;
import net.minecraft.client.ComponentCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.chattweaks.util.MessageMeta;
import org.polyfrost.chattweaks.util.Spacing;
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
        if (!(message instanceof MessageMeta meta)) {
            return;
        }
        int stampWidth = meta.chattweaks$getStampWidth();
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
//?} else {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import org.polyfrost.chattweaks.util.MessageMeta;
import org.polyfrost.chattweaks.util.Spacing;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ComponentRenderUtils.class)
public class ComponentRenderUtilsMixin {
    @WrapMethod(method = "wrapText")
    private static List<Component> chattweaks$indentWrappedLines(Component message, int maxWidth, Font font, boolean keepFormatting, boolean forceColor, Operation<List<Component>> original) {
        int stampWidth = message instanceof MessageMeta meta ? meta.chattweaks$getStampWidth() : 0;
        if (stampWidth <= 0 || stampWidth >= maxWidth) {
            return original.call(message, maxWidth, font, keepFormatting, forceColor);
        }

        List<Component> lines = original.call(message, maxWidth - stampWidth, font, keepFormatting, forceColor);
        for (int i = 1; i < lines.size(); i++) {
            lines.set(i, Spacing.of(stampWidth).append(lines.get(i)));
        }
        return lines;
    }
}
*///?}
