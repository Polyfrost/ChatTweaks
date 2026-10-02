package org.polyfrost.chattweaks.features;

//? if > 1.8.9 {
import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.storage.LevelResource;
//?} else {
/*import net.minecraft.text.LiteralText;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.ServerJoinEvent;
import org.polyfrost.oneconfig.api.event.v1.events.ShutdownEvent;
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatHistoryAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ChatHistory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatHistory.class);
    private static final DateTimeFormatter MARKER_TIME =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
    private static final int MARKER_RULE_LENGTH = 12;
    private static final String DEFAULT_PORT_SUFFIX = ":25565";
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;

    private static final ExecutorService IO = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "ChatTweaks Chat History");
        thread.setDaemon(true);
        return thread;
    });
    private static final AtomicBoolean WRITING = new AtomicBoolean();

    @Nullable
    private static WeakReference<Connection> lastConnection;

    @Nullable
    private static String loadedKey;
    @Nullable
    private static String activeKey;
    @Nullable
    private static Path activeFile;
    //? if > 1.8.9 {
    @Nullable
    private static DynamicOps<JsonElement> activeOps;
    //?}
    private static long lastSave;

    private ChatHistory() {
    }

    public static void init() {
        //? if > 1.8.9 {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onJoin(handler, client));
        ClientTickEvents.END_CLIENT_TICK.register(ChatHistory::onTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> onStopping());
        //?} else {
        /*EventManager.register(ServerJoinEvent.class, () -> onJoin(Minecraft.getInstance().getConnection(), Minecraft.getInstance()));
        EventManager.register(TickEvent.End.class, () -> onTick(Minecraft.getInstance()));
        EventManager.register(ShutdownEvent.class, ChatHistory::onStopping);
        *///?}
    }

    public static void onDisconnected() {
        if (activeKey == null) {
            return;
        }
        save();
        activeKey = null;
    }

    private static void onJoin(ClientPacketListener handler, Minecraft client) {
        Connection connection = handler.getConnection();
        boolean reconnected = lastConnection == null || lastConnection.get() != connection;
        lastConnection = new WeakReference<>(connection);

        String key = ChatTweaks.config.saveChatHistory ? serverKey(client) : null;
        UUID user = client.getUser().getProfileId();
        if (key == null || user == null) {
            activeKey = null;
            return;
        }

        activeKey = key;
        activeFile = ChatHistoryStorage.fileFor(user, key);
        //? if > 1.8.9
        activeOps = RegistryOps.create(JsonOps.INSTANCE, handler.registryAccess());
        lastSave = System.currentTimeMillis();

        ChatHistoryAccess chat = (ChatHistoryAccess) ChatCompat.getChat();
        boolean alreadyShown = key.equals(loadedKey) && !chat.chattweaks$isEmpty();
        if (alreadyShown && !reconnected) {
            return;
        }

        long sessionTime = reconnected ? Instant.now().getEpochSecond() : 0L;
        if (alreadyShown) {
            chat.chattweaks$beginSession(List.of(), false, sessionTime);
            return;
        }

        boolean clear = !chat.chattweaks$isEmpty();
        Path file = activeFile;
        //? if > 1.8.9
        DynamicOps<JsonElement> ops = activeOps;
        loadedKey = key;
        IO.execute(() -> {
            //~ if =1.8.9 'read(file, ops)' -> 'read(file)'
            List<ChatHistoryEntry> entries = ChatHistoryStorage.read(file, ops);
            client.execute(() -> {
                if (!key.equals(activeKey)) {
                    return;
                }
                ((ChatHistoryAccess) ChatCompat.getChat()).chattweaks$beginSession(entries, clear, sessionTime);
            });
        });
    }

    private static void onTick(Minecraft client) {
        if (activeKey == null) {
            return;
        }
        if (!ChatTweaks.config.saveChatHistory) {
            activeKey = null;
            return;
        }
        long interval = Math.max(5, Math.min(60, ChatTweaks.config.saveChatHistoryInterval)) * 1000L;
        long now = System.currentTimeMillis();
        if (now - lastSave < interval || WRITING.get()) {
            return;
        }
        lastSave = now;
        save();
    }

    private static void onStopping() {
        if (activeKey != null) {
            save();
            activeKey = null;
        }
        IO.shutdown();
        try {
            if (!IO.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOGGER.warn("Timed out waiting for the chat history to be written");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void save() {
        Path file = activeFile;
        //? if > 1.8.9 {
        DynamicOps<JsonElement> ops = activeOps;
        if (file == null || ops == null) {
            return;
        }
        //?} else {
        /*if (file == null) {
            return;
        }
        *///?}
        List<ChatHistoryEntry> entries = ((ChatHistoryAccess) ChatCompat.getChat()).chattweaks$captureHistory();
        WRITING.set(true);
        IO.execute(() -> {
            try {
                //~ if =1.8.9 'write(file, entries, ops)' -> 'write(file, entries)'
                ChatHistoryStorage.write(file, entries, ops);
            } catch (Exception e) {
                LOGGER.error("Failed to write chat history to {}", file, e);
            } finally {
                WRITING.set(false);
            }
        });
    }

    @Nullable
    private static String serverKey(Minecraft client) {
        ServerData server = client.getCurrentServer();
        if (server != null && server.ip != null && !server.ip.isBlank()) {
            String address = server.ip.trim().toLowerCase(Locale.ROOT);
            return address.endsWith(DEFAULT_PORT_SUFFIX)
                    ? address.substring(0, address.length() - DEFAULT_PORT_SUFFIX.length())
                    : address;
        }
        IntegratedServer singleplayer = client.getSingleplayerServer();
        if (singleplayer != null) {
            //? if > 1.8.9 {
            Path world = singleplayer.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize().getFileName();
            //?} else
            //Path world = Path.of(singleplayer.getWorldSaveName());
            if (world != null) {
                return "singleplayer/" + world;
            }
        }
        return null;
    }

    public static Component sessionMarker(long sessionTime) {
        String stamp = MARKER_TIME
                .withLocale(Locale.getDefault(Locale.Category.FORMAT))
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(sessionTime));
        String spaces = " ".repeat(MARKER_RULE_LENGTH);
        //? if > 1.8.9 {
        Style rule = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withStrikethrough(true);
        return Component.empty()
                .append(Component.literal(spaces).withStyle(rule))
                .append(Component.literal(" " + stamp + " ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(spaces).withStyle(rule));
        //?} else {
        /*return new LiteralText("")
                .append(new LiteralText(spaces).setStyle(new Style().setColor(ChatFormatting.DARK_GRAY).setStrikethrough(true)))
                .append(new LiteralText(" " + stamp + " ").setStyle(new Style().setColor(ChatFormatting.GRAY)))
                .append(new LiteralText(spaces).setStyle(new Style().setColor(ChatFormatting.DARK_GRAY).setStrikethrough(true)));
        *///?}
    }
}
