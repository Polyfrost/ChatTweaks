package org.polyfrost.chattweaks.config;

import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class ChatTweaksConfig extends Config {
    public ChatTweaksConfig() {
        super(ChatTweaks.ID + ".json", ChatTweaks.NAME, Category.QOL);
    }

    @Switch(
            title = "Remove Blank Messages",
            description = "Stop messages with no content from showing up in chat."
    )
    public boolean removeBlankMessages = false;
}
