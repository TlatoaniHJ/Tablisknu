package us.tlatoani.tablisknu.skin;

import ch.njol.skript.util.Color;
import com.google.common.collect.ImmutableMap;
import org.bukkit.DyeColor;
import us.tlatoani.tablisknu.Tablisknu;

import java.util.Scanner;

public class FullColorSkins {
    public static final ImmutableMap<Color, Skin> SKRIPT;
    public static final ImmutableMap<String, Skin> OTHER;

    public static final String RESOURCE_FILE_NAME = "colored_skins.txt";
    public static final String OTHER_SKIN_PREFIX = "#";

    static {
        ImmutableMap.Builder<Color, Skin> skriptMapBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, Skin> otherMapBuilder = ImmutableMap.builder();
        Scanner scanner = new Scanner(Tablisknu.get().getResource(RESOURCE_FILE_NAME));
        while (scanner.hasNext()) {
            String colorName = scanner.nextLine().trim().toUpperCase();
            if (colorName.isEmpty()) {
                continue;
            }
            String value = scanner.nextLine().trim();
            String signature = scanner.nextLine().trim();
            Skin skin = new Skin(value, signature);
            if (colorName.startsWith(OTHER_SKIN_PREFIX)) {
                colorName = colorName.substring(OTHER_SKIN_PREFIX.length());
                otherMapBuilder.put(colorName, skin);
            } else {
                skriptMapBuilder.put(Color.byWoolColor(DyeColor.valueOf(colorName)), skin);
            }
        }
        SKRIPT = skriptMapBuilder.build();
        OTHER = otherMapBuilder.build();
    }

    public static Skin of(Color color) {
        return SKRIPT.get(color);
    }

    public static Skin of(String name) {
        if (OTHER.containsKey(name)) {
            return OTHER.get(name);
        } else {
            return SKRIPT.get(Color.byWoolColor(DyeColor.valueOf(name.toUpperCase())));
        }
    }

    public static Skin other(int ix) {
        return of(otherName(ix));
    }

    public static String otherName(int ix) {
        return OTHER.keySet().asList().get(ix);
    }
}
