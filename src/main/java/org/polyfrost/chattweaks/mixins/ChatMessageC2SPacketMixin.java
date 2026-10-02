package org.polyfrost.chattweaks.mixins;

//? if = 1.8.9 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SPacketMixin {
    @ModifyExpressionValue(method = "<init>(Ljava/lang/String;)V", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int chattweaks$keepLongCommands(int original, @Local(argsOnly = true) String message) {
        if (ChatTweaks.config == null || !ChatTweaks.config.bypassCommandLimit || !ChatUtils.isCommand(message)) {
            return original;
        }
        return ChatUtils.COMMAND_LIMIT;
    }
}
*///?}
