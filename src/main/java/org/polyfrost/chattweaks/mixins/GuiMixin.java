package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?}
import org.polyfrost.chattweaks.features.ImagePreview;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    //? if <26.1 {
    @Inject(method = "render", at = @At("TAIL"))
    private void chattweaks$renderOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ImagePreview.render(guiGraphics);
    }
    //?}
}
