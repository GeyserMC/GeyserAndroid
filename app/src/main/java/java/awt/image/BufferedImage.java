/*
 * Copyright (c) 2020-2020 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/GeyserAndroid
 */

package java.awt.image;

import android.graphics.Bitmap;

import lombok.Getter;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * This class is a facade used by Geyser because
 * BufferedImage does not exist on Android.
 * So we implement it based on the Bitmap class.
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
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
