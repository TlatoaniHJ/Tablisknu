package us.tlatoani.tablisknu.skin.retrieval;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.Scheduling;
import us.tlatoani.mundocore.updating.HTTPClient;
import us.tlatoani.tablisknu.Tablisknu;
import us.tlatoani.tablisknu.skin.Skin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PlayerSkinRetrieval {
    public static final String MOJANG_PROFILE_API = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final AtomicReference<Cache<UUID, Skin>> cacheReference = new AtomicReference<>();

    public static void reloadSkinCache() {
        if (Tablisknu.ENABLE_OFFLINE_SKIN_CACHE.getCurrentValue()) {
            CacheBuilder builder = CacheBuilder.newBuilder();
            int maxSize = Tablisknu.OFFLINE_SKIN_CACHE_MAX_SIZE.getCurrentValue();
            if (maxSize != -1) {
                builder.maximumSize(maxSize);
            }
            int expireTimeMinutes = Tablisknu.OFFLINE_SKIN_CACHE_EXPIRE_TIME_MINUTES.getCurrentValue();
            if (expireTimeMinutes != -1) {
                builder.expireAfterAccess(expireTimeMinutes, TimeUnit.MINUTES);
            }
            cacheReference.set(builder.build());
        } else {
            cacheReference.set(null);
        }
    }

    public static void onJoin(Player player) {
        if (Tablisknu.ENABLE_OFFLINE_SKIN_CACHE.getCurrentValue()) {
            cacheReference.get().invalidate(player.getUniqueId());
        }
    }

    public static void retrieveSkinFromUUID(UUID uuid, int timeoutMillis, Consumer<Skin> callback) {
        Scheduling.async(() -> {
            Cache<UUID, Skin> cache = cacheReference.get();
            Skin skin = cache == null ? null : cache.getIfPresent(uuid);
            if (skin == null) {
                skin = skinFromMojangAPI(uuid, timeoutMillis);
                if (skin != null && cache != null) {
                    cache.put(uuid, skin);
                }
            }
            Skin finalSkin = skin;
            Scheduling.sync(() -> {
                callback.accept(finalSkin);
            });
        });
    }

    private static Skin skinFromMojangAPI(UUID uuid, int timeoutMillis) {
        try {
            HTTPClient httpClient = HTTPClient
                    .url(MOJANG_PROFILE_API, uuid.toString().replace("-", ""))
                    .method("GET")
                    .timeout(timeoutMillis);
            int statusCode = httpClient.statusCode();
            if (statusCode != 200) {
                Logging.debug(PlayerSkinRetrieval.class,
                        "While retrieving skin for UUID " + uuid + ", status code = " + statusCode + " != 200, aborting");
                return null;
            }
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(httpClient.getInput()));
            Logging.debug(PlayerSkinRetrieval.class, "JSON retrieved for UUID " + uuid + ": " + jsonObject);
            JSONArray properties = (JSONArray) jsonObject.get("properties");
            JSONObject property = (JSONObject) properties.get(0);
            return Skin.fromJSON(property, uuid);
        } catch (IOException | ParseException | ClassCastException | NullPointerException e) {
            Logging.debug(PlayerSkinRetrieval.class, "Exception while retrieving skin for UUID " + uuid);
            Logging.debug(PlayerSkinRetrieval.class, e);
            return null;
        }
    }
}
