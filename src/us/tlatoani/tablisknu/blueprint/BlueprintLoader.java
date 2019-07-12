package us.tlatoani.tablisknu.blueprint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.Scheduling;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.function.Consumer;

public class BlueprintLoader {

    public static void loadFromFile(String path, SkinFormat format, Consumer<Blueprint> callable) {
        Scheduling.async(() -> {
            try {
                Logging.debug(BlueprintLoader.class, "path = " + path);
                BufferedImage bufferedImage = ImageIO.read(new File(path));
                Logging.debug(BlueprintLoader.class, "bufferedImage = " + bufferedImage);
                Blueprint blueprint = bufferedImage == null ? null : new Blueprint(bufferedImage, format);
                Logging.debug(BlueprintLoader.class, "blueprint = " + blueprint);
                Scheduling.sync(() -> callable.accept(blueprint));
            } catch (IOException e) {
                Logging.reportException(BlueprintLoader.class, e);
                Scheduling.sync(() -> callable.accept(null));
            }
        });
    }

    public static void loadFromURL(String path, SkinFormat format, Consumer<Blueprint> callable) {
        Scheduling.async(() -> {
            try {
                Blueprint blueprint = new Blueprint(ImageIO.read(new URL(path)), format);
                Scheduling.sync(() -> callable.accept(blueprint));
            } catch (IOException e) {
                Logging.reportException(BlueprintLoader.class, e);
                Scheduling.sync(() -> callable.accept(null));
            }
        });
    }

    public static void loadOfSkin(Skin skin, Consumer<Blueprint> callable) {
        try {
            JSONObject textureJSON = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(skin.value)));
            JSONObject skinJSON = (JSONObject) ((JSONObject) textureJSON.get("textures")).get("SKIN");
            if (skinJSON == null) {
                Scheduling.sync(() -> callable.accept(null));
            }
            SkinFormat format = skinJSON.containsKey("metadata")
                    && ((JSONObject) skinJSON.get("metadata")).containsKey("model")
                    && ((JSONObject) skinJSON.get("metadata")).get("model").equals("slim") ? SkinFormat.ALEX : SkinFormat.STEVE;
            loadFromURL((String) skinJSON.get("url"), format, callable);
        } catch (ParseException | ClassCastException e) {
            Logging.reportException(BlueprintLoader.class, e);
            Scheduling.sync(() -> callable.accept(null));
        }
    }
}
