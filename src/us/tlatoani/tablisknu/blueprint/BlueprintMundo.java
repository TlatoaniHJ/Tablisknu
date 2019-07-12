package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Color;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.reflection.Reflection;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
        Registration.registerType(ARGBColor.class, "argb").parser(new Registration.SimpleParser<ARGBColor>() {
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
        Registration.registerType(Blueprint.class, "blueprint").serializer(new Serializer<Blueprint>() {
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
        Registration.registerEffect(EffColorBlueprint.class, "color %blueprint% as %argb%");
        Registration.registerEffect(EffRecolorBlueprint.class, "recolor %argb% in %blueprint% as %argb%");
        Registration.registerEffect(EffOverlayBlueprint.class,
                "overlay %blueprint% on %blueprint%");
        MundoPropertyExpression.registerPropertyExpression(ExprBlueprintCopy.class, Blueprint.class,
                "blueprint", "blueprint copied from %");
        Registration.registerExpression(ExprRectangleOfBlueprint.class, Blueprint.class, ExpressionType.COMBINED,
                "pixels %number%, %number% to %number%, %number% of %blueprint%");
        Registration.registerExpression(ExprColorOfPixel.class, ARGBColor.class, ExpressionType.COMBINED,
                "color of pixel %number%, %number% of %blueprint%");
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
                "create [(128¦slim)] [" + partJoiner + " [" + faceJoiner + " face]] blueprint from (0¦file|8¦url) %string% in %object%");
        Registration.registerExpression(ExprBlankBlueprint.class, Blueprint.class, ExpressionType.SIMPLE,
                "blank [(128¦slim)] [ " + partJoiner + " [" + faceJoiner + " face]] blueprint");
        Registration.registerExpression(ExprPartOfBlueprint.class, Blueprint.class, ExpressionType.PROPERTY,
                "[(8¦second layer)] " + partJoiner + " [" + faceJoiner + " face] pixels of %blueprint%");
        Registration.registerExpression(ExprFaceOfBlueprint.class, Blueprint.class, ExpressionType.PROPERTY,
                faceJoiner + " face pixels of %blueprint%");
    }
}
