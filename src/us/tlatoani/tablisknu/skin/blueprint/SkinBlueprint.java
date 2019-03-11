package us.tlatoani.tablisknu.skin.blueprint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class SkinBlueprint {
    private final BufferedImage bufferedImage;
    public final boolean slim;

    SkinBlueprint(BufferedImage bufferedImage, boolean slim) {
        if (bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            this.bufferedImage = bufferedImage;
        } else {
            this.bufferedImage =
                    new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            ((Graphics2D) this.bufferedImage.getGraphics()).drawImage(bufferedImage, null, 0, 0);
        }
        this.slim = slim;
    }

    public SkinBlueprint(boolean slim) {
        this(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB), slim);
    }

    public SkinBlueprint(Part part, boolean slim) {
        this(new BufferedImage(part.imageWidth(slim), part.imageHeight(), BufferedImage.TYPE_INT_ARGB), slim);
    }

    public void makeColorTransparent(int color) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                if (((bufferedImage.getRGB(x, y) & 0xFFFFFF)) == color) {
                    bufferedImage.setRGB(x, y, 0);
                }
            }
        }
    }

    /**
     * (x, y) is the upper right corner of the location of the main part
     * (x2, y2) is the upper right corner of the location of the second layer for this part
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

        public int imageWidth(boolean slim) {
            return ((slim ? slimLength : normalLength) + width) * 2;
        }

        public int imageHeight() {
            return width + height;
        }
    }

    public enum Face {
        FRONT(0x010, 0x010, 0x100, 0x001),
        BACK(0x120, 0x010, 0x100, 0x001),
        RIGHT(0x000, 0x010, 0x010, 0x001),
        LEFT(0x110, 0x010, 0x010, 0x001),
        TOP(0x010, 0x000, 0x100, 0x010),
        BOTTOM(0x110, 0x000, 0x100, 0x010);

        final int relXFormula;
        final int relYFormula;
        final int imageWidthFormula;
        final int imageHeightFormula;

        /** formulas are of the form 0xLWH
         * where L, W, H are the coefficients on the length, width, and height respectively */
        Face(int relXFormula, int relYFormula, int imageWidthFormula, int imageHeightFormula) {
            this.relXFormula = relXFormula;
            this.relYFormula = relYFormula;
            this.imageWidthFormula = imageWidthFormula;
            this.imageHeightFormula = imageHeightFormula;
        }

        static int evaluateFormula(int formula, Part part, boolean slim) {
            return ((formula >> 8) * (slim ? part.slimLength : part.normalLength))
                    + (((formula >> 4) & 0xF) * part.width)
                    + ((formula & 0xF) * part.height);
        }
    }

    public SkinBlueprint getPart(Part part, boolean isSecondLayer) {
        return new SkinBlueprint(isSecondLayer
                ? bufferedImage.getSubimage(part.x, part.y, part.imageWidth(slim), part.imageHeight())
                : bufferedImage.getSubimage(part.x2, part.y2, part.imageWidth(slim), part.imageHeight()), slim);
    }

    public SkinBlueprint getPartFace(Part part, boolean isSecondLayer, Face face) {
        return new SkinBlueprint(
                bufferedImage.getSubimage(
                        (isSecondLayer ? part.x : part.x2) + Face.evaluateFormula(face.relXFormula, part, slim),
                        (isSecondLayer ? part.y : part.y2) + Face.evaluateFormula(face.relYFormula, part, slim),
                        Face.evaluateFormula(face.imageWidthFormula, part, slim),
                        Face.evaluateFormula(face.imageHeightFormula, part, slim)),
                slim);
    }

    public void overlay(SkinBlueprint blueprint) {
        ((Graphics2D) bufferedImage.getGraphics()).drawImage(bufferedImage, null, 0, 0);
    }

    public void write(OutputStream output) throws IOException {
        ImageIO.write(bufferedImage, "png", output);
    }
}
