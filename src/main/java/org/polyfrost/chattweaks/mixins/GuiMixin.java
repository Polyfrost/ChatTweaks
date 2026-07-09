package org.polyfrost.chattweaks.mixins;

import net.minecraft.client.DeltaTracker;
//? if <26.2 {
import net.minecraft.client.gui.Gui;
//?} else {
/*import net.minecraft.client.gui.Hud;
*///?}
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import org.polyfrost.chattweaks.features.ImagePreview;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// <26.1: immediate GuiGraphics pipeline, hook Gui#render.
// 26.1:  deferred render-state extractor, hook Gui#extractRenderState.
// 26.2+: HUD split out of Gui, hook Hud#extractRenderState.
//? if <26.2 {
@Mixin(Gui.class)
//?} else {
/*@Mixin(Hud.class)
*///?}
public class GuiMixin {
    //? if <26.1 {
    @Inject(method = "render", at = @At("TAIL"))
    private void chattweaks$renderImagePreview(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ImagePreview.render(guiGraphics);
    }
    //?} else {
    /*@Inject(method = "extractRenderState", at = @At("TAIL"))
    private void chattweaks$renderImagePreview(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ImagePreview.render(guiGraphics);
    }
    *///?}
}
