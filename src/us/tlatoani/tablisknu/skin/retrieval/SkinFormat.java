package us.tlatoani.tablisknu.skin.retrieval;

public enum SkinFormat {
    STEVE(""),
    ALEX("model=slim");

    public final String skinOptions;

    SkinFormat(String skinOptions) {
        this.skinOptions = skinOptions;
    }
}
