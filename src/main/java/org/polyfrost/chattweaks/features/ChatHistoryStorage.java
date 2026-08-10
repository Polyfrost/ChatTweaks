package org.polyfrost.chattweaks.features;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.ComponentSerialization;
import org.polyfrost.chattweaks.ChatTweaks;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ChatHistoryStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final int FORMAT = 1;
    private static final int MAX_NAME_LENGTH = 48;
    private static final Pattern UNSAFE = Pattern.compile("[^a-z0-9._-]+");

    private ChatHistoryStorage() {
    }

    public static Path fileFor(UUID user, String serverKey) {
        String name = UNSAFE.matcher(serverKey.toLowerCase(Locale.ROOT)).replaceAll("_");
        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH);
        }
        return FabricLoader.getInstance().getGameDir()
                .resolve(ChatTweaks.ID)
                .resolve("chat-history")
                .resolve(user.toString())
                .resolve(name + "-" + digest(serverKey) + ".json");
    }

    private static String digest(String serverKey) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("SHA-1").digest(serverKey.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is required of every Java platform", e);
        }
        StringBuilder out = new StringBuilder(8);
        for (int i = 0; i < 4; i++) {
            out.append(String.format("%02x", hash[i]));
        }
        return out.toString();
    }

    public static List<ChatHistoryEntry> read(Path file, DynamicOps<JsonElement> ops) {
        if (!Files.isRegularFile(file)) {
            return List.of();
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                return List.of();
            }
            JsonObject object = root.getAsJsonObject();
            if (!object.has("version") || object.get("version").getAsInt() > FORMAT) {
                LOGGER.warn("Ignoring chat history {} written by a newer version of ChatTweaks", file);
                return List.of();
            }
            JsonElement entries = object.get("entries");
            if (entries == null || !entries.isJsonArray()) {
                return List.of();
            }

            List<ChatHistoryEntry> out = new ArrayList<>();
            for (JsonElement element : entries.getAsJsonArray()) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject entry = element.getAsJsonObject();
                JsonElement session = entry.get("session");
                if (session != null && session.isJsonPrimitive()) {
                    out.add(ChatHistoryEntry.session(session.getAsLong()));
                    continue;
                }
                JsonElement message = entry.get("message");
                if (message == null) {
                    continue;
                }
                ComponentSerialization.CODEC.parse(ops, message)
                        .result()
                        .ifPresent(component -> out.add(ChatHistoryEntry.message(component)));
            }
            return out;
        } catch (Exception e) {
            LOGGER.error("Failed to read chat history from {}", file, e);
            return List.of();
        }
    }

    public static void write(Path file, List<ChatHistoryEntry> entries, DynamicOps<JsonElement> ops) throws IOException {
        Files.createDirectories(file.getParent());
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(temp, StandardCharsets.UTF_8))) {
            writer.beginObject();
            writer.name("version").value(FORMAT);
            writer.name("entries").beginArray();
            for (ChatHistoryEntry entry : entries) {
                if (entry.isSession()) {
                    writer.beginObject().name("session").value(entry.sessionTime()).endObject();
                    continue;
                }
                JsonElement message = ComponentSerialization.CODEC.encodeStart(ops, entry.message())
                        .result()
                        .orElse(null);
                if (message == null) {
                    continue;
                }
                writer.beginObject().name("message");
                GSON.toJson(message, writer);
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
        }

        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
