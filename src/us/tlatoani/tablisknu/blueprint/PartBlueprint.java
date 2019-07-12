package us.tlatoani.tablisknu.blueprint;

import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import java.awt.image.BufferedImage;

public class PartBlueprint extends Blueprint {
    public final Part part;
    public final boolean isSecondLayer;

    PartBlueprint(BufferedImage bufferedImage, Part part, boolean isSecondLayer, SkinFormat format) {
        super(bufferedImage, format);
        this.part = part;
        this.isSecondLayer = isSecondLayer;
    }

    public PartBlueprint(Part part, boolean isSecondLayer, SkinFormat format) {
        this(
                new BufferedImage(part.imageWidth(format), part.imageHeight(), BufferedImage.TYPE_INT_ARGB),
                part, isSecondLayer, format
        );
    }

    public FaceBlueprint getFace(Face face) {
        return new FaceBlueprint(
                bufferedImage.getSubimage(
                        Face.evaluateFormula(face.relXFormula, part, format),
                        Face.evaluateFormula(face.relYFormula, part, format),
                        Face.evaluateFormula(face.imageWidthFormula, part, format),
                        Face.evaluateFormula(face.imageHeightFormula, part, format)),
                part, isSecondLayer, face, format
        );
    }

    @Override
    public PartBlueprint duplicate() {
        PartBlueprint res = new PartBlueprint(part, isSecondLayer, format);
        res.overlay(this);
        return res;
    }

    @Override
    public String toString() {
        return super.toString() + "(part = " + part + ", isSecondLayer = " + isSecondLayer + ", format = " + format + ")";
    }
}
