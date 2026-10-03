package org.polyfrost.chattweaks.compat;

//? if = 1.8.9 {
/*import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public record NativeImage(BufferedImage image) implements AutoCloseable {
    public static NativeImage read(InputStream stream) throws IOException {
        BufferedImage image = ImageIO.read(stream);
        if (image == null) {
            throw new IOException("Unsupported image format");
        }
        return new NativeImage(image);
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void close() {
        image.flush();
    }
}
*///?}
