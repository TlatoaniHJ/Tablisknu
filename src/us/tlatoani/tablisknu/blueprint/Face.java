package us.tlatoani.tablisknu.blueprint;

import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

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

    public static int evaluateFormula(int formula, Part part, SkinFormat format) {
        return ((formula >> 8) * (format == SkinFormat.ALEX ? part.slimLength : part.normalLength))
                + (((formula >> 4) & 0xF) * part.width)
                + ((formula & 0xF) * part.height);
    }
}
