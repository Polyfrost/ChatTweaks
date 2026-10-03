package org.polyfrost.chattweaks.mixins;

//? if = 1.8.9 {
/*import net.minecraft.client.GuiMessage;
import net.minecraft.network.chat.Component;
import org.polyfrost.chattweaks.util.ChatLineParent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.class)
public class GuiMessageMixin implements ChatLineParent {
    @Unique
    private Component chattweaks$parent;

    @Override
    public Component chattweaks$getParent() {
        return chattweaks$parent;
    }

    @Override
    public void chattweaks$setParent(Component parent) {
        this.chattweaks$parent = parent;
    }
}
*///?}
