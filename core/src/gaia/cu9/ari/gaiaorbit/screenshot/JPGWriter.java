package gaia.cu9.ari.gaiaorbit.screenshot;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

import gaia.cu9.ari.gaiaorbit.util.Logger;

/**
 * Writes JPG images
 * 
 * @author tsagrista
 *
 */
public class JPGWriter {

    /**
     * Quality setting, from 0 to 1
     */
    private static float QUALITY = 0.93f;

    /**
     * JPEG parameters
     */
    private static JPEGImageWriteParam jpegParams;

    static {
        // Initialise
        updateJPEGParams();
    }

    public static void updateJPEGParams() {
        jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(JPGWriter.QUALITY);
    }

    public static void setQuality(float quality) {
        if (quality != JPGWriter.QUALITY) {
            JPGWriter.QUALITY = quality;
            updateJPEGParams();
        }
    }

    public static void write(FileHandle file, Pixmap pix) {
        FileImageOutputStream fios = null;
        try {
            final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            fios = new FileImageOutputStream(file.file());
            writer.setOutput(fios);
            writer.write(null, new IIOImage(pixmapToBufferedImage(pix), null, null), jpegParams);

        } catch (IOException e) {
            Logger.error(e, JPGWriter.class.getSimpleName());
        } finally {
            try {
                fios.close();
            } catch (IOException e) {
                Logger.error(e, JPGWriter.class.getSimpleName());
            }

        }
    }

    static BufferedImage pixmapToBufferedImage(Pixmap p) {
        int w = p.getWidth();
        int h = p.getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //convert RGBA to RGB
                int value = p.getPixel(x, y);
                int R = ((value & 0xff000000) >>> 24);
                int G = ((value & 0x00ff0000) >>> 16);
                int B = ((value & 0x0000ff00) >>> 8);

                int i = x + (y * w);
                pixels[i] = (R << 16) | (G << 8) | B;
            }
        }
        img.setRGB(0, 0, w, h, pixels, 0, w);
        return img;
    }
}
