package us.tlatoani.tablisknu;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import us.tlatoani.mundocore.base.Config;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.MundoAddon;
import us.tlatoani.mundocore.registration.Documentation;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.mundocore.updating.Updating;
import us.tlatoani.tablisknu.skin.ProfileManager;
import us.tlatoani.tablisknu.skin.SkinMundo;
import us.tlatoani.tablisknu.skin.retrieval.PlayerSkinRetrieval;
import us.tlatoani.tablisknu.player_head.PlayerHeadMundo;
import us.tlatoani.tablisknu.tablist.TablistManager;
import us.tlatoani.tablisknu.tablist_general.TablistMundo;
import us.tlatoani.tablisknu.tablist_array.ArrayTablistMundo;
import us.tlatoani.tablisknu.tablist_group.TablistGroupMundo;
import us.tlatoani.tablisknu.tablist_player.PlayerTablistMundo;
import us.tlatoani.tablisknu.tablist_simple.SimpleTablistMundo;

public class Tablisknu extends MundoAddon {
    public static final Config.Option<Integer> TABLIST_REMOTE_TAB_DELAY_SPAWN = Config.option("tablist_remove_tab_delay_spawn", FileConfiguration::getInt);
    public static final Config.Option<Integer> TABLIST_REMOTE_TAB_DELAY_RESPAWN = Config.option("tablist_remove_tab_delay_respawn", FileConfiguration::getInt);
    public static final Config.Option<Integer> TABLIST_ADD_TO_DEFAULT_GROUP_DELAY = Config.option("tablist_add_to_default_group_delay", FileConfiguration::getInt);
    public static final Config.Option<Boolean> DISPLAY_TABLIST_SYNTAX_WARNING = Config.option("display_tablist_syntax_warning", FileConfiguration::getBoolean);

    public static final Config.Option<Boolean> ENABLE_OFFLINE_SKIN_CACHE = Config.option("enable_offline_skin_cache", FileConfiguration::getBoolean);
    public static final Config.Option<Integer> OFFLINE_SKIN_CACHE_EXPIRE_TIME_MINUTES = Config.option("offline_skin_cache_expire_time_minutes", FileConfiguration::getInt);
    public static final Config.Option<Integer> OFFLINE_SKIN_CACHE_MAX_SIZE = Config.option("offline_skin_cache_max_size", FileConfiguration::getInt);

    public final static Version MINIMUM_PROTOCOLLIB_VERSION = new Version(4, 4);

    public Tablisknu() {
        super(
                "tablisknu",
                ChatColor.DARK_AQUA,
                ChatColor.AQUA,
                ChatColor.GOLD
        );
        link("Metrics", "https://bstats.org/plugin/bukkit/Tablisknu");
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Version protocolLibVersion =
                new Version(Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion());
        if (protocolLibVersion.isSmallerThan(MINIMUM_PROTOCOLLIB_VERSION)) {
            Logging.info("Your version of ProtocolLib is " + protocolLibVersion);
            Logging.info("Tablisknu requires that you run at least version 4.4 of ProtocolLib");
        }

        Documentation.load();
        Updating.load();
        ProfileManager.load();
        TablistManager.load();

        Registration.register("Skin", SkinMundo::load);
        Registration.register("PlayerHead", PlayerHeadMundo::load);
        Registration.register("Tablist", TablistMundo::load);
        Registration.register("ArrayTablist", ArrayTablistMundo::load);
        Registration.register("TablistGroup", TablistGroupMundo::load);
        Registration.register("SimpleTablist", SimpleTablistMundo::load);
        Registration.register("PlayerTablist", PlayerTablistMundo::load);
    }

    @Override
    public void afterPluginsEnabled() {
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> Skript.getVersion().toString()));
    }

    @Override
    public void afterConfigReloaded() {
        PlayerSkinRetrieval.reloadSkinCache();
    }
}
