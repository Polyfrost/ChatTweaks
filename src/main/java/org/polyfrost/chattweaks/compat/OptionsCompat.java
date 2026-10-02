package org.polyfrost.chattweaks.compat;

//? if = 1.8.9 {
/*import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;

public final class OptionsCompat {
    private OptionsCompat() {
    }

    public static OptionInstance<Player.ChatVisibility> chatVisibility(Options options) {
        return new OptionInstance<>(() -> options.chatVisibility, value -> options.chatVisibility = value);
    }

    public static OptionInstance<Boolean> chatColors(Options options) {
        return new OptionInstance<>(() -> options.chatColors, value -> options.chatColors = value);
    }

    public static OptionInstance<Boolean> chatLinks(Options options) {
        return new OptionInstance<>(() -> options.chatLinks, value -> options.chatLinks = value);
    }

    public static OptionInstance<Boolean> chatLinksPrompt(Options options) {
        return new OptionInstance<>(() -> options.chatLinksPrompt, value -> options.chatLinksPrompt = value);
    }
}
*///?}
