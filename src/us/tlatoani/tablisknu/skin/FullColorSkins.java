package us.tlatoani.tablisknu.skin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.tablisknu.Tablisknu;

import java.util.Scanner;

public class FullColorSkins {
    private static ImmutableMap<String, Skin> skinMap = null;
    private static ImmutableList<String> skinNames = null;
    public static final String RESOURCE_NAME = "colored_skins.txt";

    public static void load() {
        ImmutableMap.Builder<String, Skin> skinMapBuilder = ImmutableMap.builder();
        Scanner scanner = new Scanner(Tablisknu.get().getResource(RESOURCE_NAME));
        while (scanner.hasNext()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] colorNames = line.toLowerCase().split(",");
            if (colorNames.length == 0) {
                continue;
            }
            String value, signature;
            while ((value = scanner.nextLine().trim()).startsWith("#") || value.isEmpty()) ;
            while ((signature = scanner.nextLine().trim()).startsWith("#") || value.isEmpty());
            Skin skin = new Skin(value, signature);
            for (String colorName : colorNames) {
                skinMapBuilder.put(colorName.trim(), skin);
            }
        }
        skinMap = skinMapBuilder.build();
        skinNames = skinMap.keySet().asList();
    }

    public static ImmutableMap<String, Skin> getSkinMap() {
        return skinMap;
    }

    public static ImmutableList<String> getSkinNames() {
        return skinNames;
    }

    public static Skin of(String name) {
        name = name.toLowerCase();
        if (!skinMap.containsKey(name)) {
            throw new IllegalArgumentException("Invalid skin color: " + name);
        }
        return skinMap.get(name);
    }

    public static Skin skinByIndex(int ix) {
        return of(nameByIndex(ix));
    }

    public static String nameByIndex(int ix) {
        return skinNames.get(ix);
    }
}
