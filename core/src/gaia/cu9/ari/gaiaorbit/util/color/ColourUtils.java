package gaia.cu9.ari.gaiaorbit.util.color;

import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;

public class ColourUtils {

    public enum ColorMap {
        GRAYSCALE, SHORT_RAINBOW, LONG_RAINBOW, YELLOW_TO_RED, BLUE_TO_MAGENTA;
    }

    public static float normalize(float value, float min, float max) {
        if (value > max)
            return max;
        if (value < min)
            return min;
        return (value - min) / (max - min);
    }

    /**
     * Converts a scalar value that is normalized to [0:1] into a grayscale
     * color vector rgba. See: http://www.particleincell.com/blog/2014/colormap/
     * 
     * @param value
     */
    public static void grayscale(float value, float rgba[]) {
        rgba[0] = value;
        rgba[1] = value;
        rgba[2] = value;
        rgba[3] = 1;
    }

    /**
     * Converts a scalar normalized to de range [0:1] into a short rainbow of
     * rgba values. See: http://www.particleincell.com/blog/2014/colormap/
     * 
     * @param value
     */
    public static void short_rainbow(float value, float[] rgba) {
        /* plot short rainbow RGB */
        float a = (1 - value) / 0.25f; //invert and group
        final int X = (int) Math.floor(a); //this is the integer part
        float Y = (a - X); //fractional part from 0 to 1

        rgba[3] = 1;

        switch (X) {
        case 0:
            rgba[0] = 1;
            rgba[1] = Y;
            rgba[2] = 0;
            break;
        case 1:
            rgba[0] = 1 - Y;
            rgba[1] = 1;
            rgba[2] = 0;
            break;
        case 2:
            rgba[0] = 0;
            rgba[1] = 1;
            rgba[2] = Y;
            break;
        case 3:
            rgba[0] = 0;
            rgba[1] = 1 - Y;
            rgba[2] = 1;
            break;
        case 4:
            rgba[0] = 0;
            rgba[1] = 0;
            rgba[2] = 1;
            break;
        }
    }

    /**
     * Converts a scalar in [0..1] to a long rainbow of rgba values. See
     * <a href="http://www.particleincell.com/blog/2014/colormap/">here</a>
     * 
     * @param value
     *            The value in [0..1]
     */
    public static void long_rainbow(float value, float[] rgba) {
        if (rgba == null)
            return;
        /* plot long rainbow RGB */
        float a = (1 - value) / 0.2f; //invert and group
        final int X = (int) Math.floor(a); //this is the integer part
        float Y = (a - X); //fractional part from 0 to 1

        rgba[3] = 1;

        switch (X) {
        case 0:
            rgba[0] = 1;
            rgba[1] = Y;
            rgba[2] = 0;
            break;
        case 1:
            rgba[0] = 1 - Y;
            rgba[1] = 1;
            rgba[2] = 0;
            break;
        case 2:
            rgba[0] = 0;
            rgba[1] = 1;
            rgba[2] = Y;
            break;
        case 3:
            rgba[0] = 0;
            rgba[1] = 1 - Y;
            rgba[2] = 1;
            break;
        case 4:
            rgba[0] = Y;
            rgba[1] = 0;
            rgba[2] = 1;
            break;
        case 5:
            rgba[0] = 1;
            rgba[1] = 0;
            rgba[2] = 1;
            break;
        }
    }

    /**
     * Converts a scalar in [0..1] to a yellow to red map. See
     * <a href="http://www.particleincell.com/blog/2014/colormap/">here</a>
     * 
     * @param value
     *            The value to convert
     */
    public static void yellow_to_red(float value, float[] rgba) {
        rgba[0] = 1;
        rgba[1] = value;
        rgba[2] = 0;
    }

    public static void blue_to_magenta(float value, float[] rgba) {
        rgba[0] = value;
        rgba[1] = 0;
        rgba[2] = 1;
    }

    /**
     * Converts effective temperature in Kelvin (1000-40000) to RGB
     * 
     * @see http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
     * @see http://www.zombieprototypes.com/?p=210
     * 
     * @param teff
     * @return
     */
    public static float[] teffToRGB(double teff) {
        double r, g, b;

        double temp = teff / 100;

        // Red
        if (temp <= 66) {
            r = 255;
        } else {
            double x = temp - 55;
            r = 351.97690566805693 + 0.114206453784165 * x - 40.25366309332127 * Math.log(x);
            r = MathUtilsd.clamp(r, 0, 255);
        }

        // Green
        if (temp <= 66) {
            double x = temp - 2;
            g = -155.25485562709179 - 0.44596950469579133 * x + 104.49216199393888 * Math.log(x);
            g = MathUtilsd.clamp(g, 0, 255);
        } else {
            double x = temp - 50;
            g = 325.4494125711974 + 0.07943456536662342 * x - 28.0852963507957 * Math.log(x);
            g = MathUtilsd.clamp(g, 0, 255);
        }

        // Blue
        if (temp >= 66) {
            b = 255;
        } else {
            if (temp <= 19) {
                b = 0;
            } else {
                double x = temp - 10;
                b = -254.76935184120902 + 0.8274096064007395 * x + 115.67994401066147 * Math.log(x);
                b = MathUtilsd.clamp(b, 0, 255);
            }
        }

        return new float[] { (float) (r / 255d), (float) (g / 255d), (float) (b / 255d) };
    }

    /**
     * Converts the color index B-V to RGB model. See <a href=
     * "http://stackoverflow.com/questions/21977786/star-b-v-color-index-to-apparent-rgb-color">here</a>
     * 
     * @param bv
     *            The B-V color index
     * @return The RGB as a float array in [0..1]
     */
    public static float[] BVtoRGB(double bv) {
        double t = 4600 * ((1 / ((0.92 * bv) + 1.7)) + (1 / ((0.92 * bv) + 0.62)));
        // t to xyY
        double x = 0, y = 0;

        if (t >= 1667 && t <= 4000) {
            x = ((-0.2661239 * Math.pow(10, 9)) / Math.pow(t, 3)) + ((-0.2343580 * Math.pow(10, 6)) / Math.pow(t, 2)) + ((0.8776956 * Math.pow(10, 3)) / t) + 0.179910;
        } else if (t > 4000 && t <= 25000) {
            x = ((-3.0258469 * Math.pow(10, 9)) / Math.pow(t, 3)) + ((2.1070379 * Math.pow(10, 6)) / Math.pow(t, 2)) + ((0.2226347 * Math.pow(10, 3)) / t) + 0.240390;
        }

        if (t >= 1667 && t <= 2222) {
            y = -1.1063814 * Math.pow(x, 3) - 1.34811020 * Math.pow(x, 2) + 2.18555832 * x - 0.20219683;
        } else if (t > 2222 && t <= 4000) {
            y = -0.9549476 * Math.pow(x, 3) - 1.37418593 * Math.pow(x, 2) + 2.09137015 * x - 0.16748867;
        } else if (t > 4000 && t <= 25000) {
            y = 3.0817580 * Math.pow(x, 3) - 5.87338670 * Math.pow(x, 2) + 3.75112997 * x - 0.37001483;
        }

        // xyY to XYZ, Y = 1
        double Y = (y == 0) ? 0 : 1;
        double X = (y == 0) ? 0 : (x * Y) / y;
        double Z = (y == 0) ? 0 : ((1 - x - y) * Y) / y;

        float[] cc = new float[4];

        cc[0] = correctGamma(3.2406 * X - 1.5372 * Y - 0.4986 * Z);
        cc[1] = correctGamma(-0.9689 * X + 1.8758 * Y + 0.0415 * Z);
        cc[2] = correctGamma(0.0557 * X - 0.2040 * Y + 1.0570 * Z);

        float max = Math.max(1, Math.max(cc[2], Math.max(cc[0], cc[1])));

        cc[0] = Math.max(cc[0] / max, 0f);
        cc[1] = Math.max(cc[1] / max, 0f);
        cc[2] = Math.max(cc[2] / max, 0f);

        return cc;
    }

    private static float correctGamma(double clinear) {
        float result;
        if (clinear <= 0.0031308) {
            result = 12.92f * (float) clinear;
        } else {
            // use 0.05 for pale colors, 0.5 for vivid colors
            float a = 0.5f;
            result = (float) ((1 + a) * Math.pow(clinear, 1 / 2.4f) - a);
        }
        return result;
    }

    /**
     * Returns a copy of the RGB colour brightened up by the given amount
     * 
     * @param rgb
     *            The RGB color
     * @param luminosity
     *            The new luminosity amount in [0..1]
     * @return The new RGB array
     */
    public static float[] brighten(float[] rgb, float luminosity) {
        float[] hsl = rgbToHsl(rgb);
        hsl[2] = luminosity;
        return hslToRgb(hsl);
    }

    /**
     * Converts an RGB color value to HSL. Conversion formula adapted from
     * http://en.wikipedia.org/wiki/HSL_color_space. Assumes r, g, and b are
     * contained in the set [0..255] and returns h, s, and l in the set [0..1]
     *
     * @param Number
     *            r The red color value
     * @param Number
     *            g The green color value
     * @param Number
     *            b The blue color value
     * @return Array The HSL representation
     */
    public static float[] rgbToHsl(float[] rgb) {
        float r, g, b;
        r = rgb[0];
        g = rgb[1];
        b = rgb[2];
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float avg = (max + min) / 2;
        float h = avg, s = avg, l = avg;

        if (max == min) {
            h = s = 0; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else if (max == b) {
                h = (r - g) / d + 4;
            }
            h /= 6;
        }

        return new float[] { h, s, l };
    }

    /**
     * Converts an HSL color value to RGB. Conversion formula adapted from
     * http://en.wikipedia.org/wiki/HSL_color_space. Assumes h, s, and l are
     * contained in the set [0..1] and returns r, g, and b in the set [0..255].
     *
     * @param Number
     *            h The hue
     * @param Number
     *            s The saturation
     * @param Number
     *            l The lightness
     * @return Array The RGB representation
     */
    public static float[] hslToRgb(float[] hsl) {
        float r, g, b;
        float h, s, l;
        h = hsl[0];
        s = hsl[1];
        l = hsl[2];

        if (s == 0) {
            r = g = b = l; // achromatic
        } else {

            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hue2rgb(p, q, h + 1 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1 / 3);
        }

        return new float[] { r, g, b };
    }

    private static float hue2rgb(float p, float q, float t) {
        if (t < 0)
            t += 1;
        if (t > 1)
            t -= 1;
        if (t < 1 / 6)
            return p + (q - p) * 6 * t;
        if (t < 1 / 2)
            return q;
        if (t < 2 / 3)
            return p + (q - p) * (2 / 3 - t) * 6;
        return p;
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    public static int getBlue(int rgb) {
        return (rgb >> 0) & 0xFF;
    }
}
