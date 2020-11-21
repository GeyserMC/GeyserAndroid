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

package java.util;

import lombok.Getter;

/**
 * This class is a facade used for support of earlier android versions.
 * java.util.Base64 wasn't included until API level 26, so this
 * class redirects the methods used to the android.util.Base64 class.
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class Base64 {

    @Getter
    public static Encoder encoder = new Encoder();
    @Getter
    public static Decoder decoder = new Decoder();

    public static class Encoder {
        public String encodeToString(byte[] src) {
            return android.util.Base64.encodeToString(src, android.util.Base64.NO_WRAP);
        }
    }

    public static class Decoder {
        public byte[] decode(String src) {
            return android.util.Base64.decode(src, android.util.Base64.NO_WRAP);
        }
    }

}
