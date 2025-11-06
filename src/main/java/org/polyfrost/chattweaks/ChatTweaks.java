package org.polyfrost.chattweaks;

//#if FABRIC
//$$ import net.fabricmc.api.ModInitializer;
//#elseif FORGE

import dev.deftu.omnicore.api.loader.ModInfo;
import dev.deftu.omnicore.api.loader.OmniLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
// HERE
//#endif

import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import org.apache.commons.lang3.StringUtils;
import org.polyfrost.chattweaks.config.ChatTweaksConfig;
import org.polyfrost.chattweaks.features.CompactChatHandler;
import org.polyfrost.chattweaks.features.ImagePreview;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.ChatEvent;
import org.polyfrost.oneconfig.api.event.v1.events.ResourceFinishedLoading;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;
import org.polyfrost.oneconfig.api.ui.v1.Notifications;

import java.util.List;
import java.util.Set;

//#if FORGE-LIKE
@Mod(modid = ChatTweaks.ID, name = ChatTweaks.NAME, version = ChatTweaks.VERSION)
//#endif
public class ChatTweaks
        //#if FABRIC
        //$$ implements ModInitializer
        //#endif
{
    public static final String ID = "@MOD_ID@";
    public static final String NAME = "@MOD_NAME@";
    public static final String VERSION = "@MOD_VERSION@";

    public static ChatTweaksConfig config;

    //#if FABRIC
    //$$ @Override
    //#elseif FORGE
    @Mod.EventHandler
    //#endif
    public void onInitialize(
            //#if FORGE
            FMLInitializationEvent event
            //#endif
    ) {
        config = new ChatTweaksConfig();
        EventManager.INSTANCE.register(this);
        EventManager.INSTANCE.register(new CompactChatHandler());
        EventManager.INSTANCE.register(new ImagePreview());
    }

    @Subscribe
    public void onLoadComplete(ResourceFinishedLoading event) {
        Set<ModInfo> activeModList = OmniLoader.getMods();
        this.detectIncompat(activeModList);
    }

    void detectIncompat(Set<ModInfo> activeModList) {
        for (ModInfo container : activeModList) {
            String modId = container.getId();
            String baseMessage = container.getName() + " has been detected. ";
            if ((modId.equals("labymod") || modId.equals("enhancements")) || modId.equals("hychat")) {
                if (config.compactChat) {
                    Notifications.enqueue(Notifications.Type.Error, "ChatTweaks", baseMessage + "Compact Chat is now disabled.");
                    config.compactChat = false;
                }
            }
        }
    }

    @Subscribe
    public void onChatReceive(ChatEvent.Receive event) {
        if (config.removeBlankMessages && StringUtils.isBlank(event.getFullyUnformattedMessage())) {
            event.cancelled = true;
        }
    }
}