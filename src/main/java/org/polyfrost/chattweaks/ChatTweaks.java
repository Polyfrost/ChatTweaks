package org.polyfrost.chattweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.polyfrost.chattweaks.config.ChatTweaksConfig;
import org.polyfrost.chattweaks.features.ChatHistory;

public class ChatTweaks implements ClientModInitializer {
    public static final String ID = "@MOD_ID@";
    public static final String NAME = "@MOD_NAME@";
    public static final String VERSION = "@MOD_VERSION@";

    public static ChatTweaksConfig config;

    @Override
    public void onInitializeClient() {
        config = new ChatTweaksConfig();
        ChatHistory.init();
    }
}
