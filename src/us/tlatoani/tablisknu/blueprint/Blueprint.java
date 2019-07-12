package us.tlatoani.tablisknu.blueprint;

import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class Blueprint {
    protected final BufferedImage bufferedImage;
    public final SkinFormat format;

    Blueprint(BufferedImage bufferedImage, SkinFormat format) {
        if (bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            this.bufferedImage = bufferedImage;
        } else {
            this.bufferedImage =
                    new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = this.bufferedImage.createGraphics();
            graphics.drawImage(bufferedImage, null, 0, 0);
            graphics.dispose();
        }
        this.format = format;
    }

    public Blueprint(SkinFormat format) {
        this(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB), format);
    }

    public boolean isFullSize() {
        return bufferedImage.getWidth() == 64 && bufferedImage.getHeight() == 64;
    }

    public Blueprint duplicate() {
        Blueprint res = new Blueprint(
                new BufferedImage(
                        this.bufferedImage.getWidth(), this.bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB),
                format
        );
        res.overlay(this);
        return res;
    }

    public Blueprint getRectangle(int x1, int y1, int x2, int y2) {
        return new Blueprint(
                bufferedImage.getSubimage(x1, y1, x2 - x1 + 1, y2 - y1 + 1), format);
    }

    public PartBlueprint getPart(Part part, boolean isSecondLayer) {
        if (!isFullSize()) {
            throw new UnsupportedOperationException("This is not a full size blueprint!");
        }
        return new PartBlueprint(
                isSecondLayer
                        ? bufferedImage.getSubimage(part.x2, part.y2, part.imageWidth(format), part.imageHeight())
                        : bufferedImage.getSubimage(part.x, part.y, part.imageWidth(format), part.imageHeight()),
                part, isSecondLayer, format
        );
    }

    public ARGBColor getPixelColor(int x, int y) {
        return new ARGBColor(bufferedImage.getRGB(x, y));
    }

    public void colorPixel(int x, int y, ARGBColor color) {
        bufferedImage.setRGB(x, y, color.argb);
    }

    public void colorAll(ARGBColor color) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(color.toAWTColor());
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.dispose();
    }

    public void replaceColor(ARGBColor before, ARGBColor after) {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                if (bufferedImage.getRGB(x, y) == before.argb) {
                    bufferedImage.setRGB(x, y, after.argb);
                }
            }
        }
    }

    public void overlay(Blueprint blueprint) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(blueprint.bufferedImage, null, 0, 0);
        graphics.dispose();
    }

    /**
     * Writes the content of this blueprint to {@code output} in PNG format.
     * @throws IOException
     */
    public void write(OutputStream output) throws IOException {
        ImageIO.write(bufferedImage, "png", output);
    }

    @Override
    public String toString() {
        return super.toString() + "(dim = " + bufferedImage.getWidth() + " * " + bufferedImage.getHeight() + ", format = " + format + ")";
    }
}
