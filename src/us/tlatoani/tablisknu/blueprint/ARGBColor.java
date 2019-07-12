package us.tlatoani.tablisknu.blueprint;

import org.bukkit.Color;

import java.util.Objects;

public class ARGBColor {
    public final int argb;

    public static final ARGBColor TRANSPARENT = new ARGBColor(0);


    public ARGBColor(int argb) {
        this.argb = argb;
    }

    public static ARGBColor fromRGB(int rgb) {
        return new ARGBColor(0xFF000000 + rgb);
    }

    public static ARGBColor fromBukkit(Color color) {
        return fromRGB(color.asRGB());
    }

    public String toHexString() {
        StringBuilder res = new StringBuilder(Integer.toHexString(argb));
        while (res.length() < 8) {
            res.insert(0, "0");
        }
        return res.toString();
    }

    public java.awt.Color toAWTColor() {
        return new java.awt.Color(argb, true);
    }

    public boolean isOpaque() {
        return (argb & 0xFF000000) == 0xFF000000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ARGBColor argbColor = (ARGBColor) o;
        return argb == argbColor.argb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argb);
    }
}
