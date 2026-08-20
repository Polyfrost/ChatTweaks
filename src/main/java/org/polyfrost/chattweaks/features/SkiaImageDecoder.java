package org.polyfrost.chattweaks.features;

import org.jetbrains.skia.Data;
import org.jetbrains.skia.EncodedImageFormat;
import org.jetbrains.skia.Image;

final class SkiaImageDecoder {
    private static boolean unavailable;

    private SkiaImageDecoder() {
    }

    static byte[] toPng(byte[] bytes) {
        if (unavailable) {
            return null;
        }
        try {
            try (Image image = Image.Companion.makeFromEncoded(bytes)) {
                Data data = image.encodeToData(EncodedImageFormat.PNG, 100);
                try (data) {
                    if (data == null) {
                        return null;
                    }
                    return data.getBytes();
                }
            }
        } catch (LinkageError e) {
            unavailable = true;
            return null;
        } catch (Throwable t) {
            return null;
        }
    }
}
