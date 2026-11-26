package org.polyfrost.chattweaks.features;

//? if = 1.8.9 {
/*import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.Connection;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.ServerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
*///?}
import org.polyfrost.chattweaks.util.ChatUtils;

public final class CommandLengthLimit {
    //? if = 1.8.9 {
    /*private static final Logger LOGGER = LoggerFactory.getLogger("ChatTweaks");
    private static final int EXTENDED_CHAT_PROTOCOL = 315;
    private static final int EXTENDED_CHAT_LIMIT = 256;
    private static final int PROBE_TIMEOUT_MS = 5000;
    private static final int MAX_STATUS_LENGTH = 1 << 21;
    private static final AtomicReference<State> STATE = new AtomicReference<>(new State(null, ChatUtils.VANILLA_CHAT_LIMIT));

    private record State(Connection connection, int limit) {
    }
    *///?}

    private CommandLengthLimit() {
    }

    public static void init() {
        //? if = 1.8.9
        //EventManager.register(ServerJoinEvent.class, CommandLengthLimit::onJoin);
    }

    public static int get() {
        //? if = 1.8.9 {
        /*ClientPacketListener listener = Minecraft.getInstance().getConnection();
        State state = STATE.get();
        return listener != null && listener.getConnection() == state.connection() ? state.limit() : ChatUtils.VANILLA_CHAT_LIMIT;
        *///?} else
        return ChatUtils.COMMAND_LIMIT;
    }

    //? if = 1.8.9 {
    /*private static void onJoin() {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener listener = client.getConnection();
        if (listener == null) {
            return;
        }
        Connection connection = listener.getConnection();
        if (connection.isLocal()) {
            STATE.set(new State(connection, ChatUtils.COMMAND_LIMIT));
            return;
        }
        State pending = new State(connection, ChatUtils.VANILLA_CHAT_LIMIT);
        STATE.set(pending);
        ServerData server = client.getCurrentServer();
        SocketAddress remote = connection.getAddress();
        if (server == null || server.ip == null || !(remote instanceof InetSocketAddress socket)) {
            return;
        }
        String ip = server.ip;
        Thread probe = new Thread(() -> {
            try {
                ServerAddress address = ServerAddress.parse(ip);
                if (statusProtocol(socket, address.getAddress(), address.getPort()) >= EXTENDED_CHAT_PROTOCOL) {
                    STATE.compareAndSet(pending, new State(connection, EXTENDED_CHAT_LIMIT));
                }
            } catch (IOException | RuntimeException e) {
                LOGGER.debug("Failed to read the chat length limit of {}", ip, e);
            }
        }, "ChatTweaks Command Limit");
        probe.setDaemon(true);
        probe.start();
    }

    private static int statusProtocol(InetSocketAddress socketAddress, String host, int port) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, PROBE_TIMEOUT_MS);
            socket.setSoTimeout(PROBE_TIMEOUT_MS);

            ByteArrayOutputStream handshake = new ByteArrayOutputStream();
            DataOutputStream body = new DataOutputStream(handshake);
            writeVarInt(body, 0);
            writeVarInt(body, EXTENDED_CHAT_PROTOCOL);
            byte[] hostBytes = host.getBytes(StandardCharsets.UTF_8);
            writeVarInt(body, hostBytes.length);
            body.write(hostBytes);
            body.writeShort(port);
            writeVarInt(body, 1);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            writeVarInt(out, handshake.size());
            handshake.writeTo(out);
            writeVarInt(out, 1);
            writeVarInt(out, 0);
            out.flush();

            DataInputStream in = new DataInputStream(socket.getInputStream());
            readVarInt(in);
            if (readVarInt(in) != 0) {
                throw new IOException("Unexpected status packet");
            }
            int length = readVarInt(in);
            if (length < 0 || length > MAX_STATUS_LENGTH) {
                throw new IOException("Invalid status length " + length);
            }
            byte[] json = new byte[length];
            in.readFully(json);
            JsonObject status = new JsonParser().parse(new String(json, StandardCharsets.UTF_8)).getAsJsonObject();
            return status.getAsJsonObject("version").get("protocol").getAsInt();
        }
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        for (int shift = 0; shift < 35; shift += 7) {
            byte b = in.readByte();
            value |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return value;
            }
        }
        throw new IOException("VarInt too big");
    }
    *///?}
}
