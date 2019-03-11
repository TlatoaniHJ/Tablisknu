package us.tlatoani.tablisknu.skin.blueprint;

import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.Scheduling;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

public class BlueprintLoader {
    /*
    public static void loadFromFile(String path, Consumer<SkinBlueprint> callable) {
        Scheduling.async(() -> {
            try {
                SkinBlueprint blueprint = new SkinBlueprint(ImageIO.read(new File(path)));
                Scheduling.sync(() -> callable.accept(blueprint));
            } catch (IOException e) {
                Logging.reportException(BlueprintLoader.class, e);
                Scheduling.sync(() -> callable.accept(null));
            }
        });
    }

    public static void loadFromURL(String path, Consumer<SkinBlueprint> callable) {
        Scheduling.async(() -> {
            try {
                SkinBlueprint blueprint = new SkinBlueprint(ImageIO.read(new URL(path)));
                Scheduling.sync(() -> callable.accept(blueprint));
            } catch (IOException e) {
                Logging.reportException(BlueprintLoader.class, e);
                Scheduling.sync(() -> callable.accept(null));
            }
        });
    }
    */
}
