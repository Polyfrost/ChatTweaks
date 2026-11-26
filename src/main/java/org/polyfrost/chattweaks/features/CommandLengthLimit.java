package org.polyfrost.chattweaks.features;

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
*///?}
import org.polyfrost.chattweaks.util.ChatUtils;

public final class CommandLengthLimit {
    private CommandLengthLimit() {
    }

    public static int get() {
        //? if = 1.8.9 {
        /*ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null && listener.getConnection().isLocal() ? ChatUtils.COMMAND_LIMIT : ChatUtils.VANILLA_CHAT_LIMIT;
        *///?} else
        return ChatUtils.COMMAND_LIMIT;
    }
}
