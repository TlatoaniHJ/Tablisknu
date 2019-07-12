package us.tlatoani.tablisknu.blueprint;

import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import java.awt.image.BufferedImage;

public class FaceBlueprint extends Blueprint {
    public final Part part;
    public final boolean isSecondLayer;
    public final Face face;

    FaceBlueprint(BufferedImage bufferedImage, Part part, boolean isSecondLayer, Face face, SkinFormat format) {
        super(bufferedImage, format);
        this.part = part;
        this.isSecondLayer = isSecondLayer;
        this.face = face;
    }

    public FaceBlueprint(Part part, boolean isSecondLayer, Face face, SkinFormat format) {
        this(
                new BufferedImage(
                        Face.evaluateFormula(face.imageWidthFormula, part, format),
                        Face.evaluateFormula(face.imageHeightFormula, part, format),
                        BufferedImage.TYPE_INT_ARGB
                ),
                part, isSecondLayer, face, format
        );
    }

    @Override
    public FaceBlueprint duplicate() {
        FaceBlueprint res = new FaceBlueprint(part, isSecondLayer, face, format);
        res.overlay(this);
        return res;
    }

    @Override
    public String toString() {
        return super.toString() + "(part = " + part + ", isSecondLayer = " + isSecondLayer + ", face = " + face + ", format = " + format + ")";
    }
}
