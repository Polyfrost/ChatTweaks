package org.polyfrost.chattweaks.features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageTranscoderTest {
    private static final String WEBP = "UklGRhwAAABXRUJQVlA4TA8AAAAvAYAAAAcQ/Y/+ByKi/wEA";
    private static final String ANIMATED_WEBP = "UklGRoQAAABXRUJQVlA4WAoAAAACAAAAAgAAAQAAQU5JTQYAAAAAAAAAAABBTk1GKAAAAAAAAAAAAAIAAAEAAGQAAAJWUDhMDwAAAC8CQAAAB9D/iP4HIqL/AQBBTk1GKAAAAAAAAAAAAAIAAAEAAGQAAABWUDhMDwAAAC8CQAAABxDR//4HIqL/AQA=";
    private static final String ICO = "AAABAAEAAgIAAAAAIABOAAAAFgAAAIlQTkcNChoKAAAADUlIRFIAAAACAAAAAggGAAAAcrYNJAAAABVJREFUeJxjZGD4/5+BgYGBCUSAMAAdGQICsWeR9gAAAABJRU5ErkJggg==";

    @Test
    public void transcodesWebpAndIco() throws IOException {
        assertPng(WEBP, 2, 3, 0xFFFF0000);
        assertPng(ANIMATED_WEBP, 3, 2, 0xFF00FF00);
        assertPng(ICO, 2, 2, 0xFF0000FF);
    }

    private static void assertPng(String base64, int width, int height, int argb) throws IOException {
        byte[] png = ImageTranscoder.toPng(Base64.getDecoder().decode(base64));
        Assertions.assertNotNull(png);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
        Assertions.assertEquals(width, image.getWidth());
        Assertions.assertEquals(height, image.getHeight());
        Assertions.assertEquals(argb, image.getRGB(0, 0));
    }
}
