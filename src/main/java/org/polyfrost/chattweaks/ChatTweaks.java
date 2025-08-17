package org.polyfrost.chattweaks;

//#if FABRIC
//$$ import net.fabricmc.api.ModInitializer;
//#elseif FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.lang3.StringUtils;
import org.polyfrost.chattweaks.config.ChatTweaksConfig;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.ChatEvent;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;
//#endif

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
    }

    @Subscribe
    public void onChatReceive(ChatEvent.Receive event) {
        if (StringUtils.isBlank(event.getFullyUnformattedMessage())) {
            event.cancelled = true;
        }
    }
}