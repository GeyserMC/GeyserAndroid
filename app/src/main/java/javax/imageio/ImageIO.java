package javax.imageio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class is a facade used by Geyser because
 * ImageIO does not exist on Android.
 */
public class ImageIO {

    public static BufferedImage read(URL input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }

        InputStream inputStream = null;
        try {
            inputStream = input.openStream();
        } catch (IOException e) {
            throw new IOException("Can't get input stream from URL!", e);
        }

        return read(inputStream);
    }

    public static BufferedImage read(File input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }
        if (!input.canRead()) {
            throw new IOException("Can't read input file!");
        }

        return read(new FileInputStream(input));
    }

    public static BufferedImage read(InputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }

        if (input.available() == 0) {
            //return null;
        }

        return new BufferedImage(BitmapFactory.decodeStream(input));
    }

    public static boolean write(RenderedImage image, String format, File file) throws IOException {
        if (!format.equals("png")) {
            throw new IllegalArgumentException("ImageIO.write format must be png!");
        }
        if (!(image instanceof BufferedImage)) {
            throw new IllegalArgumentException("ImageIO.write image must be BufferedImage!");
        }

        FileOutputStream out = new FileOutputStream(file);
        ((BufferedImage) image).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
        out.close();

        return true;
    }
}
