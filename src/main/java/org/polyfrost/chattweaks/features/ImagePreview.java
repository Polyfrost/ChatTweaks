package org.polyfrost.chattweaks.features;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
//? if > 1.8.9 {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
//? if > 1.8.9 {
import net.minecraft.client.gui.screens.ConfirmScreen;
//?} else
//import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
//? if > 1.8.9
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
//? if >=1.21.8 {
import net.minecraft.client.renderer.RenderPipelines;
//?} elif >=1.21.4 {
/*import net.minecraft.client.renderer.RenderType;
*///?}
import net.minecraft.client.renderer.texture.DynamicTexture;
//? if > 1.8.9
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.ChatInputAccess;
import org.polyfrost.chattweaks.util.HoveredUrl;
//? if = 1.8.9 {
/*import org.lwjgl.input.Keyboard;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent;
*///?}

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImagePreview {
    private static final Pattern META_TAG_REGEX = Pattern.compile("<meta\\s[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMG_TAG_REGEX = Pattern.compile("<img\\s[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern OG_IMAGE_PROPERTY_REGEX =
            Pattern.compile("(?:property|name)\\s*=\\s*[\"']?(?:og:image|twitter:image)[\"']?[\\s/>]", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONTENT_ATTRIBUTE_REGEX = attribute("content");
    private static final Pattern SRC_ATTRIBUTE_REGEX = attribute("src");
    private static final Pattern HOST_SEPARATOR = Pattern.compile("[,\\s]+");

    private static final int MAX_HOPS = 5;
    private static final int MAX_CACHED = 8;
    private static final String[] LOCAL_FOLDERS = {"screenshots"};

    private static final int STATE_LOADING = 0;
    private static final int STATE_READY = 1;
    private static final int STATE_FAILED = 2;
    private static final int STATE_BLOCKED = 3;

    private static final int PADDING = 3;
    private static final int MOUSE_OFFSET = 12;
    private static final int LINE_HEIGHT = 10;
    private static final int GAP = 4;
    private static final int MARGIN = 6;
    private static final int COMPACT_WIDTH = 160;

    private static final int COLOR_TITLE = 0xFFFFFFFF;
    private static final int COLOR_DETAIL = 0xFFAAAAAA;
    private static final int COLOR_HINT = 0xFF808080;
    private static final int COLOR_ERROR = 0xFFFF5555;
    private static final int COLOR_WARNING = 0xFFFFFF55;

    private static final int PROMPT_DELAY_TICKS = 40;

    private static final LinkedHashMap<String, Preview> CACHE = new LinkedHashMap<>(16, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Preview> eldest) {
            if (size() <= MAX_CACHED) {
                return false;
            }
            discard(eldest.getValue());
            return true;
        }
    };

    private static int textureCounter;
    private static boolean controlWasDown;
    private static Preview promptFor;

    private static final class Preview {
        final String url;
        volatile int state = STATE_LOADING;
        volatile String failure;
        volatile NativeImage pending;
        volatile String blockedUrl;
        volatile String blockedHost;
        volatile int blockedHop;
        Identifier texture;
        int imageWidth = 100;
        int imageHeight = 100;
        String host = "link";
        String title = "";

        Preview(String url) {
            this.url = url;
        }
    }

    //? if <26.1 {
    /*public static void render(GuiGraphics graphics, int mouseX, int mouseY) {
    *///?} else {
    public static void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    //?}
        if (!ChatTweaks.config.modEnabled || !ChatTweaks.config.imagePreview) {
            return;
        }
        boolean controlDown = ChatCompat.isControlDown();
        boolean controlPressed = controlDown && !controlWasDown;
        controlWasDown = controlDown;
        Minecraft mc = Minecraft.getInstance();
        String url = ((HoveredUrl) ChatCompat.getChat()).chattweaks$hoveredUrl(mouseX, mouseY);
        if (url == null) {
            return;
        }
        handle(mc, graphics, url, mouseX, mouseY, controlDown, controlPressed);
    }

    public static void init() {
        //? if > 1.8.9 {
        ClientTickEvents.END_CLIENT_TICK.register(client -> openPrompt());
        //?} else
        //EventManager.register(TickEvent.End.class, ImagePreview::openPrompt);
    }

    //? if <26.1 {
    /*private static void handle(Minecraft mc, GuiGraphics graphics, String value, int mouseX, int mouseY, boolean controlDown, boolean controlPressed) {
    *///?} else {
    private static void handle(Minecraft mc, GuiGraphicsExtractor graphics, String value, int mouseX, int mouseY, boolean controlDown, boolean controlPressed) {
    //?}
        boolean remote = value.startsWith("http");
        if (remote) {
            value = resolveImgur(value);
        } else {
            Path local = resolveLocal(mc, value);
            if (local == null) {
                return;
            }
            value = local.toString();
        }

        Preview entry = CACHE.get(value);
        if (entry == null) {
            entry = new Preview(value);
            if (remote) {
                describe(entry, value);
            } else {
                entry.host = "local";
                entry.title = Path.of(value).getFileName().toString();
            }
            CACHE.put(value, entry);
            if (remote) {
                request(entry, value, 0, null);
            } else {
                final Preview target = entry;
                CompletableFuture.runAsync(() -> loadFile(target));
            }
        } else if (entry.state == STATE_BLOCKED) {
            if (isTrusted(entry.blockedUrl)) {
                request(entry, entry.blockedUrl, entry.blockedHop, null);
            } else if (!ChatTweaks.config.imagePreviewWarningAccepted) {
                if (controlPressed) {
                    promptFor = entry;
                }
            } else if (controlDown) {
                request(entry, entry.blockedUrl, entry.blockedHop, entry.blockedHost);
            }
        }

        consumePending(mc, entry);
        draw(mc, graphics, entry, mouseX, mouseY);
    }

    private static void openPrompt() {
        Preview entry = promptFor;
        if (entry == null) {
            return;
        }
        promptFor = null;
        Screen previous = ChatCompat.getScreen();
        if (!(previous instanceof ChatScreen)) {
            return;
        }
        String draft = ((ChatInputAccess) previous).chattweaks$getInput().getValue();
        String title = "Load image from " + entry.blockedHost + "?";
        String message = "This site is not on your trusted list. Loading the image sends your IP address to it, and whoever sent the link may run it." +
                " If you agree, untrusted images load while you hold " + ChatCompat.controlKeyName() + ". You will not be asked again.";
        //? if > 1.8.9 {
        ConfirmScreen screen = new ConfirmScreen(agreed -> answerPrompt(entry, agreed, previous, draft),
                Component.literal(title), Component.literal(message), Component.literal("Agree and load"), Component.literal("Cancel"));
        //?} else {
        /*ConfirmScreen screen = new ConfirmScreen((agreed, id) -> answerPrompt(entry, agreed, previous, draft),
                title, message, "Agree and load", "Cancel", 0) {
            @Override
            protected void keyPressed(char typedChar, int keyCode) {
                if (keyCode == Keyboard.KEY_ESCAPE) {
                    answerPrompt(entry, false, previous, draft);
                }
            }
        };
        *///?}
        ChatCompat.setScreen(screen);
        //? if > 1.8.9 {
        screen.setDelay(PROMPT_DELAY_TICKS);
        //?} else
        //screen.disableButtons(PROMPT_DELAY_TICKS);
    }

    private static void answerPrompt(Preview entry, boolean agreed, Screen previous, String draft) {
        if (agreed) {
            ChatTweaks.config.imagePreviewWarningAccepted = true;
            ChatTweaks.config.save();
            if (entry.state == STATE_BLOCKED) {
                request(entry, entry.blockedUrl, entry.blockedHop, entry.blockedHost);
            }
        }
        ChatCompat.setScreen(previous);
        EditBox input = ((ChatInputAccess) previous).chattweaks$getInput();
        input.setValue(draft);
        //? if >=26.3
        previous.setFocused(input);
    }

    private static void consumePending(Minecraft mc, Preview entry) {
        NativeImage image = entry.pending;
        if (image == null) {
            return;
        }
        entry.pending = null;

        //? if >=1.21.5 {
        DynamicTexture dynamicTexture = new DynamicTexture(() -> "chattweaks/preview", image);
        //?} elif > 1.8.9 {
        /*DynamicTexture dynamicTexture = new DynamicTexture(image);
        *///?} else {
        /*DynamicTexture dynamicTexture = new DynamicTexture(image.image());
        *///?}
        //? if > 1.8.9 {
        entry.texture = Identifier.fromNamespaceAndPath("chattweaks", "preview_" + (textureCounter++));
        mc.getTextureManager().register(entry.texture, dynamicTexture);
        //?} else
        //entry.texture = mc.getTextureManager().register("chattweaks_preview", dynamicTexture);
        entry.imageWidth = image.getWidth();
        entry.imageHeight = image.getHeight();
    }

    private static NativeImage decode(byte[] bytes) {
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            return NativeImage.read(stream);
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    private static Path resolveLocal(Minecraft mc, String name) {
        Path root = mc.gameDirectory.toPath().toAbsolutePath().normalize();
        for (String folder : LOCAL_FOLDERS) {
            Path directory = root.resolve(folder).normalize();
            Path file;
            try {
                file = directory.resolve(name).normalize();
            } catch (InvalidPathException e) {
                return null;
            }
            if (file.startsWith(directory) && Files.isRegularFile(file)) {
                return file;
            }
        }
        return null;
    }

    private static void loadFile(Preview entry) {
        try {
            NativeImage image = decode(Files.readAllBytes(Path.of(entry.url)));
            if (image == null) {
                fail(entry, "Unsupported image format");
                return;
            }
            entry.pending = image;
            entry.state = STATE_READY;
        } catch (IOException | InvalidPathException | OutOfMemoryError e) {
            fail(entry, "Could not load image");
        }
    }

    private static String resolveImgur(String url) {
        int cut = url.indexOf('?');
        if (cut < 0) {
            cut = url.indexOf('#');
        }
        String trimmed = cut < 0 ? url : url.substring(0, cut);
        if (!trimmed.contains("imgur.com/") || trimmed.contains("i.imgur.com/")) {
            return url;
        }
        if (trimmed.contains("/a/") || trimmed.contains("/gallery/") || trimmed.contains("/t/")) {
            return url;
        }
        String id = trimmed.substring(trimmed.lastIndexOf('/') + 1);
        if (id.isEmpty() || id.indexOf('.') >= 0) {
            return url;
        }
        return "https://i.imgur.com/" + id + ".png";
    }

    //? if <26.1 {
    /*private static void draw(Minecraft mc, GuiGraphics graphics, Preview entry, int mouseX, int mouseY) {
    *///?} else {
    private static void draw(Minecraft mc, GuiGraphicsExtractor graphics, Preview entry, int mouseX, int mouseY) {
    //?}
        Font font = mc.font;
        Window window = mc.getWindow();
        int guiWidth = window.getGuiScaledWidth();
        int guiHeight = window.getGuiScaledHeight();
        boolean expanded = ChatCompat.isShiftDown();

        int maxContentWidth = Math.max(48, guiWidth - (MARGIN + PADDING) * 2);

        int drawWidth = 0;
        int drawHeight = 0;
        boolean ready = entry.texture != null && entry.state == STATE_READY;
        if (ready) {
            float aspectRatio = entry.imageWidth / (float) entry.imageHeight;
            float width = expanded ? guiWidth : Math.min(guiWidth * (ChatTweaks.config.imagePreviewWidth / 100F), COMPACT_WIDTH);
            width = Math.min(width, maxContentWidth);
            float height = width / aspectRatio;
            int maxImageHeight = Math.max(16, guiHeight - (MARGIN + PADDING) * 2 - LINE_HEIGHT * 2 - GAP);
            if (height > maxImageHeight) {
                height = maxImageHeight;
                width = height * aspectRatio;
            }
            drawWidth = Math.max(1, (int) width);
            drawHeight = Math.max(1, (int) height);
        }

        int spaceWidth = font.width(" ");
        String name = fit(font, entry.title, maxContentWidth);
        String source = fit(font, entry.host, Math.max(0, maxContentWidth - font.width(name) - spaceWidth));
        int sourceX = font.width(name) + spaceWidth;
        int headerWidth = source.isEmpty() ? font.width(name) : sourceX + font.width(source);

        String warning = null;
        String footer;
        int footerColor;
        if (entry.state == STATE_FAILED) {
            String message = entry.failure;
            footer = message == null ? "Could not load image" : message;
            footerColor = COLOR_ERROR;
        } else if (entry.state == STATE_BLOCKED) {
            String prefix = "Loading shares your IP with ";
            warning = prefix + fitStart(font, entry.blockedHost, maxContentWidth - font.width(prefix));
            footer = ChatTweaks.config.imagePreviewWarningAccepted
                    ? "Hold " + ChatCompat.controlKeyName() + " to load"
                    : "Press " + ChatCompat.controlKeyName() + " to review and load";
            footerColor = COLOR_HINT;
        } else if (ready) {
            footer = expanded ? "Release Shift to shrink" : "Press Shift to enlarge";
            footerColor = COLOR_HINT;
        } else {
            footer = "Loading\u2026";
            footerColor = COLOR_DETAIL;
        }
        footer = fit(font, footer, maxContentWidth);

        int contentWidth = Math.max(drawWidth, Math.max(headerWidth, font.width(footer)));
        if (warning != null) {
            contentWidth = Math.max(contentWidth, font.width(warning));
        }
        int contentHeight = LINE_HEIGHT * 2 - 2 + (drawHeight > 0 ? drawHeight + GAP : 0) + (warning != null ? LINE_HEIGHT : 0);

        int x = mouseX + MOUSE_OFFSET;
        if (x + contentWidth + PADDING > guiWidth - MARGIN) {
            x = mouseX - MOUSE_OFFSET - contentWidth;
        }
        x = clamp(x, MARGIN + PADDING, Math.max(MARGIN + PADDING, guiWidth - MARGIN - PADDING - contentWidth));

        int y = mouseY - MOUSE_OFFSET - contentHeight;
        if (y < MARGIN + PADDING) {
            y = mouseY + MOUSE_OFFSET;
        }
        y = clamp(y, MARGIN + PADDING, Math.max(MARGIN + PADDING, guiHeight - MARGIN - PADDING - contentHeight));

        //? if >=26.1 {
        graphics.nextStratum();
        //?}

        background(graphics, x, y, contentWidth, contentHeight);

        text(graphics, font, name, x, y, COLOR_TITLE);
        if (!source.isEmpty()) {
            text(graphics, font, source, x + sourceX, y, COLOR_DETAIL);
        }

        int footerY = y + LINE_HEIGHT;
        if (drawHeight > 0) {
            int imageY = y + LINE_HEIGHT;
            //? if >=1.21.8 {
            graphics.blit(RenderPipelines.GUI_TEXTURED, entry.texture, x, imageY, 0F, 0F, drawWidth, drawHeight, entry.imageWidth, entry.imageHeight, entry.imageWidth, entry.imageHeight);
            //?} elif >=1.21.4 {
            /*graphics.blit(RenderType::guiTextured, entry.texture, x, imageY, 0F, 0F, drawWidth, drawHeight, entry.imageWidth, entry.imageHeight, entry.imageWidth, entry.imageHeight);
            *///?} else {
            /*graphics.blit(entry.texture, x, imageY, drawWidth, drawHeight, 0F, 0F, entry.imageWidth, entry.imageHeight, entry.imageWidth, entry.imageHeight);
            *///?}
            footerY = imageY + drawHeight + GAP;
        }

        if (warning != null) {
            text(graphics, font, warning, x, footerY, COLOR_WARNING);
            footerY += LINE_HEIGHT;
        }
        text(graphics, font, footer, x, footerY, footerColor);
    }

    private static void describe(Preview entry, String url) {
        String path = url;
        try {
            URI uri = new URI(url);
            entry.host = uri.getHost() == null ? "link" : uri.getHost();
            if (uri.getPath() != null && !uri.getPath().isEmpty()) {
                path = uri.getPath();
            }
        } catch (URISyntaxException e) {
            entry.host = "link";
        }
        int slash = path.lastIndexOf('/');
        String name = slash >= 0 && slash + 1 < path.length() ? path.substring(slash + 1) : path;
        try {
            name = URLDecoder.decode(name, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
        }
        entry.title = name.isEmpty() ? url : name;
    }

    private static String fit(Font font, String value, int maxWidth) {
        if (font.width(value) <= maxWidth) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value);
        while (builder.length() > 1 && font.width(builder + "…") > maxWidth) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder + "…";
    }

    private static String fitStart(Font font, String value, int maxWidth) {
        if (font.width(value) <= maxWidth) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value);
        while (builder.length() > 1 && font.width("…" + builder) > maxWidth) {
            builder.deleteCharAt(0);
        }
        return "…" + builder;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    //? if <26.1 {
    /*private static void text(GuiGraphics graphics, Font font, String value, int x, int y, int color) {
        graphics.drawString(font, value, x, y, color, true);
    }

    private static void background(GuiGraphics graphics, int x, int y, int width, int height) {
    *///?} else {
    private static void text(GuiGraphicsExtractor graphics, Font font, String value, int x, int y, int color) {
        graphics.text(font, value, x, y, color, true);
    }

    private static void background(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
    //?}
        //? if >=26.1 {
        TooltipRenderUtil.extractTooltipBackground(graphics, x, y, width, height, null);
        //?} elif >=1.21.8 {
        /*TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, height, null);
        *///?} elif >=1.21.4 {
        /*TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, height, 400, null);
        *///?} elif > 1.8.9 {
        /*TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, height, 400);
        *///?} else {
        /*graphics.renderTooltipBackground(x, y, width, height);
        *///?}
    }

    private static void discard(Preview entry) {
        NativeImage pending = entry.pending;
        entry.pending = null;
        if (pending != null) {
            pending.close();
        }
        if (entry.texture != null) {
            Minecraft.getInstance().getTextureManager().release(entry.texture);
            entry.texture = null;
        }
    }

    private static void request(Preview entry, String url, int hop, @Nullable String approvedHost) {
        if (!allowed(url, approvedHost)) {
            block(entry, url, hop);
            return;
        }
        entry.state = STATE_LOADING;
        CompletableFuture.runAsync(() -> loadUrl(entry, url, hop, approvedHost));
    }

    private static boolean allowed(String url, @Nullable String approvedHost) {
        return isTrusted(url) || (approvedHost != null && approvedHost.equals(host(url)));
    }

    @Nullable
    private static String host(String url) {
        try {
            String host = new URI(url).getHost();
            return host == null || host.isEmpty() ? null : host.toLowerCase(Locale.ROOT);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static boolean isTrusted(String url) {
        String host = host(url);
        if (host == null) {
            return false;
        }
        String list = ChatTweaks.config.imagePreviewTrustedHosts;
        if (list == null) {
            return false;
        }
        for (String trusted : HOST_SEPARATOR.split(list.toLowerCase(Locale.ROOT))) {
            while (trusted.startsWith("*") || trusted.startsWith(".")) {
                trusted = trusted.substring(1);
            }
            if (!trusted.isEmpty() && (host.equals(trusted) || host.endsWith("." + trusted))) {
                return true;
            }
        }
        return false;
    }

    private static void block(Preview entry, String url, int hop) {
        String host = host(url);
        if (host == null) {
            fail(entry, "Could not load image");
            return;
        }
        entry.blockedUrl = url;
        entry.blockedHop = hop;
        entry.blockedHost = host;
        entry.state = STATE_BLOCKED;
    }

    private static void loadUrl(Preview entry, String url, int hop, @Nullable String approvedHost) {
        if (hop > MAX_HOPS) {
            fail(entry, "Too many redirects");
            return;
        }

        HttpURLConnection connection = null;
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                fail(entry, "Could not load image");
                return;
            }
            if (!allowed(url, approvedHost)) {
                if (approvedHost != null) {
                    fail(entry, "Could not load image");
                } else {
                    block(entry, url, hop);
                }
                return;
            }
            URL u = uri.toURL();
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.setInstanceFollowRedirects(false);
            connection.addRequestProperty("User-Agent", "ChatTweaks Image Previewer");
            connection.addRequestProperty("Accept", "image/*,text/html;q=0.8,*/*;q=0.5");
            if (url.contains("imgur")) {
                connection.addRequestProperty("Referer", "https://imgur.com/");
            }
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            int code = connection.getResponseCode();
            if (code >= 300 && code < 400) {
                String location = connection.getHeaderField("Location");
                if (location == null || location.isEmpty()) {
                    fail(entry, "Could not load image");
                    return;
                }
                String target = new URI(url).resolve(location).toString();
                connection.disconnect();
                loadUrl(entry, target, hop + 1, approvedHost);
                return;
            }
            if (code >= 400) {
                fail(entry, "Server returned " + code);
                return;
            }

            try (InputStream stream = connection.getInputStream()) {
                String contentType = connection.getHeaderField("Content-Type");
                if (contentType != null && contentType.contains("text/html")) {
                    String imageURL = findImage(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                    if (imageURL != null) {
                        String target = new URI(url).resolve(imageURL).toString();
                        connection.disconnect();
                        loadUrl(entry, target, hop + 1, approvedHost);
                        return;
                    }
                    fail(entry, "No image on that page");
                    return;
                }

                byte[] body = stream.readAllBytes();
                NativeImage image = decode(body);
                if (image == null) {
                    byte[] transcoded = ImageTranscoder.toPng(body);
                    image = transcoded == null ? null : decode(transcoded);
                }
                if (image == null) {
                    fail(entry, "Unsupported image format");
                    return;
                }
                entry.pending = image;
                entry.state = STATE_READY;
            }
        } catch (IOException | URISyntaxException | IllegalArgumentException e) {
            fail(entry, "Could not load image");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String findImage(String body) {
        Matcher tags = META_TAG_REGEX.matcher(body);
        while (tags.find()) {
            String tag = tags.group();
            if (!OG_IMAGE_PROPERTY_REGEX.matcher(tag).find()) {
                continue;
            }
            String content = value(CONTENT_ATTRIBUTE_REGEX, tag);
            if (content != null) {
                return content;
            }
        }
        tags = IMG_TAG_REGEX.matcher(body);
        while (tags.find()) {
            String src = value(SRC_ATTRIBUTE_REGEX, tags.group());
            if (src != null && !src.startsWith("data:")) {
                return src;
            }
        }
        return null;
    }

    private static Pattern attribute(String name) {
        return Pattern.compile(name + "\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s\"'>]+))", Pattern.CASE_INSENSITIVE);
    }

    private static String value(Pattern pattern, String tag) {
        Matcher matcher = pattern.matcher(tag);
        if (!matcher.find()) {
            return null;
        }
        for (int group = 1; group <= 3; group++) {
            String found = matcher.group(group);
            if (found != null && !found.isEmpty()) {
                return found;
            }
        }
        return null;
    }

    private static void fail(Preview entry, String message) {
        entry.failure = message;
        entry.state = STATE_FAILED;
    }
}
