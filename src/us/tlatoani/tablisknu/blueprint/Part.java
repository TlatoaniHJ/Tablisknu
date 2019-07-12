package us.tlatoani.tablisknu.blueprint;

import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

/**
 * (x, y) is the upper left corner of the location of the main part
 * (x2, y2) is the upper left corner of the location of the second layer for this part
 * width, height are the dimensions of this part
 */
public enum Part {
    HEAD(0, 0, 32, 0, 8, 8, 8),
    BODY(16, 16, 16, 32, 8, 4, 12),
    RIGHT_ARM(40, 16, 40, 32, 4, 3,4, 12),
    LEFT_ARM(32, 48, 48, 48, 4, 3, 4, 12),
    RIGHT_LEG(0, 16, 0, 32, 4, 4, 12),
    LEFT_LEG(16, 48, 0, 48, 4, 4, 12);

    public final int x;
    public final int y;
    public final int x2;
    public final int y2;
    public final int normalLength;
    public final int slimLength;
    public final int width;
    public final int height;

    Part(int x, int y, int x2, int y2, int normalLength, int slimLength, int width, int height) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.normalLength = normalLength;
        this.slimLength = slimLength;
        this.width = width;
        this.height = height;
    }

    Part(int x, int y, int x2, int y2, int length, int width, int height) {
        this(x, y, x2, y2, length, length, width, height);
    }

    public int imageWidth(SkinFormat format) {
        return ((format == SkinFormat.ALEX ? slimLength : normalLength) + width) * 2;
    }

    public int imageHeight() {
        return width + height;
    }
}
