package us.tlatoani.tablisknu.skin.retrieval;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.Scheduling;
import us.tlatoani.mundocore.updating.HTTPClient;
import us.tlatoani.tablisknu.skin.Skin;

import java.io.*;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 5/7/17.
 * Based on (and with portions of code copied from) the Mineskin Client created by inventivetalent (who also created the Mineskin API)
 */
public class MineSkinRetrieval {
    public static final String MINESKIN_URL_API = "https://api.mineskin.org/generate/url?url=%s&%s";
    public static final String MINESKIN_UPLOAD_API = "https://api.mineskin.org/generate/upload?%s";

    enum Source {
        URL {
            @Override
            public HTTPClient httpClient(String path, SkinFormat skinFormat) throws IOException {
                return HTTPClient
                        .url(MINESKIN_URL_API, path, skinFormat.skinOptions)
                        .method("POST");
            }
        },
        FILE {
            @Override
            public HTTPClient httpClient(String path, SkinFormat skinFormat) throws IOException {
                return HTTPClient
                        .url(MINESKIN_UPLOAD_API, skinFormat.skinOptions)
                        .method("POST")
                        .uploadFile(path);
            }
        };

        public abstract HTTPClient httpClient(String path, SkinFormat skinFormat) throws IOException;
    }

    enum SkinFormat {
        STEVE(""),
        ALEX("model=slim");

        public final String skinOptions;

        SkinFormat(String skinOptions) {
            this.skinOptions = skinOptions;
        }
    }

    public static void retrieveFromMineSkinAPI(Source source, String path, SkinFormat skinFormat, int timeoutMillis, Consumer<Skin> callable) {
        Scheduling.async(() -> {
            Skin skin = skinFromMineSkinAPI(source, path, skinFormat, timeoutMillis);
            Scheduling.sync(() -> {
                callable.accept(skin);
            });
        });
    }

    private static Skin skinFromMineSkinAPI(Source source, String path, SkinFormat skinFormat, int timeoutMillis) {
        try {
            HTTPClient httpClient = source
                    .httpClient(path, skinFormat)
                    .timeout(timeoutMillis);
            int statusCode = httpClient.statusCode();
            if (statusCode != 200) {
                Logging.debug(MineSkinRetrieval.class,
                        "While retrieving skin from " + path + ", status code = " + statusCode + " != 200, aborting");
                return null;
            }
            return fromMineSkinInputStream(httpClient.getInput());
        } catch (IOException | ParseException | ClassCastException | NullPointerException e) {
            Logging.debug(MineSkinRetrieval.class,
                    "Exception occurred while retrieving skin from " + path);
            Logging.debug(MineSkinRetrieval.class, e);
            return null;
        }
    }

    private static Skin fromMineSkinInputStream(InputStream inputStream) throws IOException, ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(inputStream));
        JSONObject data = (JSONObject) jsonObject.get("data");
        JSONObject texture = (JSONObject) data.get("texture");
        return Skin.fromJSON(texture);
    }
}
