package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Color;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.reflection.Reflection;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import java.awt.image.BufferedImage;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class BlueprintMundo {

    public static void load() {
        Class<?> skriptColorClass = Reflection.getClass("ch.njol.skript.util.Color");
        Reflection.MethodInvoker toBukkitColorMethod;
        Map<ARGBColor, String> colorNames = new HashMap<>();
        colorNames.put(ARGBColor.TRANSPARENT, "transparent");
        if (Reflection.classExists("ch.njol.skript.util.SkriptColor")) {
            toBukkitColorMethod = Reflection.getMethod(skriptColorClass, "asBukkitColor");
            for (Object color : (Object[]) Reflection.getMethod("ch.njol.skript.util.SkriptColor", "values").invoke(null)) {
                colorNames.put(ARGBColor.fromBukkit((Color) toBukkitColorMethod.invoke(color)), color.toString().toLowerCase());
            }
        } else {
            toBukkitColorMethod = Reflection.getMethod(skriptColorClass, "getBukkitColor");
            for (Object color : (Object[]) Reflection.getMethod(skriptColorClass, "values").invoke(null)) {
                colorNames.put(ARGBColor.fromBukkit((Color) toBukkitColorMethod.invoke(color)), color.toString().toLowerCase());
            }
        }
        Registration.registerType(ARGBColor.class, "argb")
                .document("ARGB Color", "1.0",
                        "A color that can have transparency; "
                        + "'rgb' stands for the usual red, green, blue values (from 0 to 255 inclusive), "
                        + "and 'a' stands for the alpha value that describes transparency, "
                        + "where 0 means fully transparent, and 255 means fully opaque. "
                        + "When using normal Skript colors (green, blue, etc.) as argb colors, "
                        + "they will be converted as fully opaque. "
                        + "You can also write argb colors as hex codes: "
                        + "`rgb RRGGBB` will be a fully opaque argb color from the hex code RRGGBB, "
                        + "and `argb AARRGGBB` will be an argb colors with transparency AA and rgb hex code RRGGBB.")
                .example("color right arm pixels of {_blueprint} as yellow",
                        "set color of pixel 3, 4 in head pixels of {_blueprint} to rgb 00ffff # aqua color",
                        "color second layer head front face pixels of {_blueprint} as argb 80ff00ff # semitransparent purple",
                        "color pixels 3, 0 to 4, 11 of second layer body back face pixels of {_blueprint} as transparent")
                .usage("transparent")
                .parser(new Registration.SimpleParser<ARGBColor>() {
            @Override
            public String toString(ARGBColor color, int flags) {
                return colorNames.getOrDefault(color, color.isOpaque() ? ("rgb " + color.toHexString().substring(2)) : ("argb " + color.toHexString()));
            }

            @Override
            public ARGBColor parse(String s, ParseContext context) {
                try {
                    if (s.equals("transparent")) {
                        return ARGBColor.TRANSPARENT;
                    } else if (s.startsWith("rgb ") && s.length() == 10) {
                        return ARGBColor.fromRGB(Integer.parseInt(s.substring(4), 16));
                    } else if (s.startsWith("argb ") && s.length() == 13) {
                        return new ARGBColor((int) Long.parseLong(s.substring(5), 16));
                    }
                } catch (NumberFormatException ignored) {}
                return null;
            }
        }).serializer(new Serializer<ARGBColor>() {
            @Override
            public Fields serialize(ARGBColor o) {
                Fields fields = new Fields();
                fields.putObject("argb", o.argb);
                return fields;
            }

            @Override
            public void deserialize(ARGBColor o, Fields f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ARGBColor deserialize(Fields fields) throws StreamCorruptedException {
                return new ARGBColor((Integer) fields.getObject("argb"));
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }
        });
        Registration.registerConverter(skriptColorClass, ARGBColor.class, color -> ARGBColor.fromBukkit((Color) toBukkitColorMethod.invoke(color)));
        Registration.registerType(Blueprint.class, "blueprint")
                .document("Blueprint", "1.0",
                        "A blueprint of a skin that can be modified in Skript "
                        + "and then used to create an actual skin that you can put on players. "
                        + "A blueprint could be for a whole skin or be a section of another blueprint.")
                .serializer(new Serializer<Blueprint>() {
            @Override
            public Fields serialize(Blueprint blueprint) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("format", blueprint.format.ordinal());
                if (blueprint instanceof FaceBlueprint) {
                    FaceBlueprint faceBlueprint = (FaceBlueprint) blueprint;
                    fields.putObject("type", "face");
                    fields.putObject("part", faceBlueprint.part.ordinal());
                    fields.putObject("isSecondLayer", faceBlueprint.isSecondLayer);
                    fields.putObject("face", faceBlueprint.face.ordinal());
                } else if (blueprint instanceof PartBlueprint) {
                    PartBlueprint partBlueprint = (PartBlueprint) blueprint;
                    fields.putObject("type", "part");
                    fields.putObject("part", partBlueprint.part.ordinal());
                    fields.putObject("isSecondLayer", partBlueprint.isSecondLayer);
                } else {
                    fields.putObject("type", "default");
                }
                int height = blueprint.bufferedImage.getHeight();
                fields.putObject("height", height);
                int[] pixels = new int[height * blueprint.bufferedImage.getWidth()];
                for (int i = 0; i < pixels.length; i++) {
                    pixels[i] = blueprint.getPixelColor(i / height, i % height).argb;
                }
                fields.putObject("pixels", pixels);
                return fields;
            }

            @Override
            public void deserialize(Blueprint o, Fields f) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Blueprint deserialize(Fields fields) throws StreamCorruptedException {
                Blueprint blueprint;
                SkinFormat format = SkinFormat.values()[(Integer) fields.getObject("format")];
                int height = (Integer) fields.getObject("height");
                int[] pixels = (int[]) fields.getObject("pixels");
                BufferedImage bufferedImage = new BufferedImage(pixels.length / height, height, BufferedImage.TYPE_INT_ARGB);
                for (int i = 0; i < pixels.length; i++) {
                    bufferedImage.setRGB(i / height, i % height, pixels[i]);
                }
                if (fields.getObject("type").equals("face")) {
                    Part part = Part.values()[(Integer) fields.getObject("part")];
                    boolean isSecondLayer = (Boolean) fields.getObject("isSecondLayer");
                    Face face = Face.values()[(Integer) fields.getObject("face")];
                    blueprint = new FaceBlueprint(bufferedImage, part, isSecondLayer, face, format);
                } else if (fields.getObject("type").equals("part")) {
                    Part part = Part.values()[(Integer) fields.getObject("part")];
                    boolean isSecondLayer = (Boolean) fields.getObject("isSecondLayer");
                    blueprint = new PartBlueprint(bufferedImage, part, isSecondLayer, format);
                } else {
                    blueprint = new Blueprint(bufferedImage, format);
                }
                return blueprint;
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }
        });
        Registration.registerEffect(EffColorBlueprint.class, "color %blueprint% as %argb%")
            .document("Color Blueprint", "1.0",
                    "Colors all of the specified blueprint as the specified color. "
                    + "Normally the blueprint you specify will be a section of a larger blueprint, "
                    + "as we normally don't need skins that are a single color.");
        Registration.registerEffect(EffRecolorBlueprint.class, "recolor %argb% in %blueprint% as %argb%")
            .document("Recolor Blueprint", "1.0",
                    "Recolors all pixels with the first specified color in the specified blueprint "
                    + "as having the second specified color.");
        Registration.registerEffect(EffOverlayBlueprint.class,
                "overlay %blueprint% on %blueprint%")
            .document("Overlay Blueprint on Blueprint", "1.0",
                    "Overlays the first specified blueprint on the second specified blueprint. "
                    + "To understand what \"overlay\" means, you can imagine the blueprints being pieces of paper "
                    + "(possibly with transparency), and just putting one piece of paper on top of the other. "
                    + "So if the first specified blueprint is fully opaque, "
                    + "then it will completely replace the second specified blueprint, which is sometimes what you need. "
                    + "Note that the overlaying is done by matching the top left corner of both blueprints, "
                    + "so if the first specified blueprint is smaller than the second, "
                    + "then the lower and or the right pixels of the second specified blueprint "
                    + "won't have anything overlayed on them.");
        MundoPropertyExpression.registerPropertyExpression(ExprBlueprintCopy.class, Blueprint.class,
                "blueprint", "blueprint copied from %")
            .document("Copy of Blueprint", "1.0",
                    "Produces a copy of the specified blueprint. "
                    + "Changes made to the copy won't affect the original, so this might be used, "
                    + "for example, if you have a template that you modify to produce skins.");
        Registration.registerExpression(ExprRectangleOfBlueprint.class, Blueprint.class, ExpressionType.COMBINED,
                "pixels %number%, %number% to %number%, %number% (of|in) %blueprint%")
            .document("Rectangle of Blueprint", "1.0",
                    "A blueprint consisting of a rectangular region of the specified blueprint, "
                    + "delineated by the two specified corners of the rectangle. "
                    + "Changes made to the returned blueprint will also be made to the corresponding pixels in the original. "
                    + "You can additionally use the Copy of Blueprint expression if you don't want this to happen.");
        Registration.registerExpression(ExprColorOfPixel.class, ARGBColor.class, ExpressionType.COMBINED,
                "color of pixel %number%, %number% (of|in) %blueprint%")
            .document("Color of Pixel in Blueprint", "1.0",
                    "The color of the specified pixel in the specified blueprint.");
        StringJoiner faceJoiner = new StringJoiner("|", "(", ")");
        for (int i = 1; i <= Face.values().length; i++) {
            faceJoiner.add(i + "¦" + Face.values()[i - 1].name().toLowerCase().replace("_", " "));
        }
        StringJoiner partJoiner = new StringJoiner("|", "(", ")");
        for (int i = 1; i <= Part.values().length; i++) {
            partJoiner.add((i << 4) + "¦" + Part.values()[i - 1].name().toLowerCase().replace("_", " "));
        }
        Registration.registerEffect(EffCreateBlueprint.class,
                "create blueprint of %skin% in %object%",
                "create [(128¦slim)] [" + partJoiner + " [" + faceJoiner + " face]] blueprint from (0¦file|8¦url) %string% in %object%")
            .document("Create Blueprint", "1.0",
                    "Creates a blueprint and puts it in the specified variable (the %object% at the end). "
                    + "This is a delayed effect as it requires retrieving image data either from online or from a file, "
                    + "so if used inside of a function the function will return before the effect (and any code after the effect) completes. "
                    + "The first syntax creates the blueprint from an existing skin. "
                    + "The second syntax creates from an image at the specified url or file path. "
                    + "If the blueprint is meant to be a blueprint of a certain part of a skin (ex. the head, right leg, etc.), "
                    + "a part in the second layer of a skin, or even a certain face (front, back, top, etc.) of a part, "
                    + "you can specify that. You can also specify whether it is meant to be a slim skin (skin with thinner arms).");
        Registration.registerExpression(ExprBlankBlueprint.class, Blueprint.class, ExpressionType.SIMPLE,
                "blank [(128¦slim)] [ " + partJoiner + " [" + faceJoiner + " face]] blueprint")
            .document("Blank Blueprint", "1.0",
                    "A new, blank blueprint. All pixels are colored transparent by default, "
                    + "which for the first layer means that they will render as black "
                    + "(but still act as transparent when using blueprint syntaxes)."
                    + "If the blueprint is meant to be a blueprint of a certain part of a skin (ex. the head, right leg, etc.), "
                    + "a part in the second layer of a skin, or even a certain face (front, back, top, etc.) of a part, "
                    + "you can specify that. You can also specify whether it is meant to be a slim skin (skin with thinner arms).");
        Registration.registerExpression(ExprPartOfBlueprint.class, Blueprint.class, ExpressionType.PROPERTY,
                "[(8¦second layer)] " + partJoiner + " [" + faceJoiner + " face] pixels of %blueprint%")
            .document("Body Part of Blueprint", "1.0",
                    "A blueprint containing the region of the specified blueprint representing the specified body part, "
                    + "optionally restricted to a certain face (front, back, left, right, top, bottom) of the body part. "
                    + "You can specify whether the blueprint should be of the first (main) layer of the body part, "
                    + "or the second layer (which can have transparency).");
        Registration.registerExpression(ExprFaceOfBlueprint.class, Blueprint.class, ExpressionType.PROPERTY,
                faceJoiner + " face pixels of %blueprint%")
            .document("Face of Body Part Blueprint", "1.0",
                    "A blueprint containing the region of the specified blueprint representing the specified face of the body part. "
                    + "The specified blueprint must represent a single body part in a skin.");
    }
}
