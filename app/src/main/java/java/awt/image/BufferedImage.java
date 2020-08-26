package java.awt.image;

import android.graphics.Bitmap;

import lombok.Getter;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * This class is a facade used by Geyser because
 * BufferedImage does not exist on Android.
 * So we implement it based on the Bitmap class.
 */
public class BufferedImage extends RenderedImage {

    public static final int TYPE_INT_ARGB = 2;

    @Getter
    private Bitmap bitmap;

    public BufferedImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BufferedImage(int width, int height, int format) {
        if (format != TYPE_INT_ARGB) {
            throw new IllegalArgumentException("BufferedImage format must be TYPE_INT_ARGB!");
        }

        this.bitmap = Bitmap.createBitmap(width, height, ARGB_8888);
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public void setRGB(int x, int y, int color) {
        bitmap.setPixel(x, y, color);
    }

    public int getRGB(int x, int y) {
        return bitmap.getPixel(x, y);
    }

    public void flush() {
        bitmap.recycle();
    }
}
