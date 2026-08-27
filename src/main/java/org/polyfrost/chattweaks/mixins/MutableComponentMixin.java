package org.polyfrost.chattweaks.mixins;

import net.minecraft.network.chat.MutableComponent;
import org.polyfrost.chattweaks.util.MessageMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MutableComponent.class)
public class MutableComponentMixin implements MessageMeta {

    @Unique
    private int chattweaks$stampWidth;

    @Override
    public int chattweaks$getStampWidth() {
        return chattweaks$stampWidth;
    }

    @Override
    public void chattweaks$setStampWidth(int width) {
        this.chattweaks$stampWidth = width;
    }
}
