package org.polyfrost.chattweaks.mixins;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(value = LiteralCommandNode.class, remap = false)
public abstract class LiteralCommandNodeMixin {
    @Shadow
    @Final
    private String literal;

    @Shadow
    @Final
    private String literalLowerCase;

    @Inject(method = "listSuggestions", at = @At("HEAD"), cancellable = true)
    private void chattweaks$fuzzySuggestions(CommandContext<?> context, SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
        if (ChatTweaks.config == null || !ChatTweaks.config.fuzzyCommandSuggestions) {
            return;
        }
        String typed = builder.getRemainingLowerCase();
        if (this.literalLowerCase.startsWith(typed)) {
            return;
        }
        if (ChatUtils.fuzzyMatches(this.literalLowerCase, typed)) {
            cir.setReturnValue(builder.suggest(this.literal).buildFuture());
        }
    }
}
