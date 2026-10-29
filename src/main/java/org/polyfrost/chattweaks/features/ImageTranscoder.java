package org.polyfrost.chattweaks.features;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

final class ImageTranscoder {
    private ImageTranscoder() {
    }

    static byte[] toPng(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            return ImageIO.write(image, "png", out) ? out.toByteArray() : null;
        } catch (Throwable t) {
            return null;
        }
    }
}
