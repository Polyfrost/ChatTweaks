package org.polyfrost.chattweaks.features;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
//? if <26.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.polyfrost.chattweaks.ChatTweaks;
import org.polyfrost.chattweaks.util.ChatCompat;
import org.polyfrost.chattweaks.util.HoveredUrl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImagePreview {
    private static final Pattern OGP_IMAGE_REGEX =
            Pattern.compile("<meta property=\"(?:og:image|twitter:image)\" content=\"(?<url>.+?)\".*?/?>");
    private static final Pattern IMG_TAG_REGEX = Pattern.compile("<img.*?src=\"(?<url>.+?)\".*?>");

    private static volatile NativeImage pendingImage;
    private static String loaded;
    private static Identifier texture;
    private static int imageWidth = 100;
    private static int imageHeight = 100;

    // <26.1 gets the immediate GuiGraphics and 26.1+ gets the deferred GuiGraphicsExtractor
    // both expose the same blit overload so only the parameter type differs
    //? if <26.1 {
    /*public static void render(GuiGraphics graphics) {
    *///?} else {
    public static void render(GuiGraphicsExtractor graphics) {
    //?}
        if (!ChatTweaks.config.imagePreview) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double mouseX = mc.mouseHandler.xpos() * window.getGuiScaledWidth() / Math.max(1, window.getScreenWidth());
        double mouseY = mc.mouseHandler.ypos() * window.getGuiScaledHeight() / Math.max(1, window.getScreenHeight());

        //? if <26.2 {
        /*ChatComponent chat = mc.gui.getChat();
        *///?} else {
        ChatComponent chat = mc.gui.hud.getChat();
        //?}
        String url = ((HoveredUrl) chat).chattweaks$hoveredUrl(mouseX, mouseY);
        if (url == null) {
            releaseTexture(mc);
            loaded = null;
            return;
        }
        handle(mc, graphics, url);
    }

    //? if <26.1 {
    /*private static void handle(Minecraft mc, GuiGraphics graphics, String value) {
    *///?} else {
    private static void handle(Minecraft mc, GuiGraphicsExtractor graphics, String value) {
    //?}
        if (!value.startsWith("http")) {
            releaseTexture(mc);
            return;
        }

        if (value.contains("imgur.com/") && !value.contains("i.imgur")) {
            final String[] split = value.split("/");
            value = String.format("https://i.imgur.com/%s.png", split[split.length - 1]);
        }

        if (!value.equals(loaded)) {
            loaded = value;
            releaseTexture(mc);
            pendingImage = null;
            final String finalValue = value;
            CompletableFuture.runAsync(() -> loadUrl(finalValue));
        }

        NativeImage image = pendingImage;
        if (image != null) {
            pendingImage = null;
            //? if >=1.21.5 {
            DynamicTexture dynamicTexture = new DynamicTexture(() -> "chattweaks/preview", image);
            //?} else {
            /*DynamicTexture dynamicTexture = new DynamicTexture(image);
            *///?}
            texture = Identifier.fromNamespaceAndPath("chattweaks", "preview");
            mc.getTextureManager().register(texture, dynamicTexture);
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
        }

        if (texture == null) {
            return;
        }

        Window window = mc.getWindow();
        int guiWidth = window.getGuiScaledWidth();
        int guiHeight = window.getGuiScaledHeight();
        float aspectRatio = imageWidth / (float) imageHeight;

        float drawWidth = guiWidth * (ChatTweaks.config.imagePreviewWidth / 100);
        if (ChatCompat.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            drawWidth = guiWidth;
        }
        if (ChatCompat.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            drawWidth = imageWidth;
        }
        float drawHeight = drawWidth / aspectRatio;
        if (drawHeight > guiHeight) {
            drawHeight = guiHeight;
            drawWidth = drawHeight * aspectRatio;
        }

        // the 26.1+ extractor batches draws per stratum so a fresh one puts the preview above the HUD
        //? if >=26.1 {
        graphics.nextStratum();
        //?}

        //? if >=1.21.8 {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0F, 0F, (int) drawWidth, (int) drawHeight, imageWidth, imageHeight, imageWidth, imageHeight);
        //?} elif >=1.21.4 {
        /*graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, texture, 0, 0, 0F, 0F, (int) drawWidth, (int) drawHeight, imageWidth, imageHeight, imageWidth, imageHeight);
        *///?} else {
        /*graphics.blit(texture, 0, 0, (int) drawWidth, (int) drawHeight, 0F, 0F, imageWidth, imageHeight, imageWidth, imageHeight);
        *///?}
    }

    private static void releaseTexture(Minecraft mc) {
        if (texture != null) {
            mc.getTextureManager().release(texture);
            texture = null;
        }
    }

    private static void loadUrl(String url) {
        HttpURLConnection connection = null;
        try {
            URL u = new URI(url).toURL();
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(true);
            connection.setInstanceFollowRedirects(true);
            connection.addRequestProperty("User-Agent", "ChatTweaks Image Previewer");
            if (url.contains("imgur")) {
                connection.addRequestProperty("Referer", "https://imgur.com/");
            }
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            try (InputStream stream = connection.getInputStream()) {
                String contentType = connection.getHeaderField("Content-Type");
                if (contentType != null && contentType.contains("text/html")) {
                    String body = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    String imageURL = "";
                    Matcher matcher;
                    if ((matcher = OGP_IMAGE_REGEX.matcher(body)).find()) {
                        imageURL = matcher.group("url");
                    } else if ((matcher = IMG_TAG_REGEX.matcher(body)).find()) {
                        imageURL = matcher.group("url");
                    }
                    if (imageURL.startsWith("/")) {
                        URL urlObj = new URI(url).toURL();
                        imageURL = urlObj.getProtocol() + "://" + urlObj.getHost() + imageURL;
                    }

                    if (!imageURL.isEmpty()) {
                        connection.disconnect();
                        loadUrl(imageURL);
                        return;
                    }
                }

                pendingImage = NativeImage.read(stream);
            }
        } catch (IOException | URISyntaxException ignored) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
