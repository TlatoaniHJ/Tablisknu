package us.tlatoani.tablisknu.skin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.tlatoani.tablisknu.Tablisknu;
import us.tlatoani.tablisknu.skin.retrieval.PlayerSkinRetrieval;
import us.tlatoani.tablisknu.util.PacketUtil;
import us.tlatoani.tablisknu.skin.ModifiableProfile.Specific;
import us.tlatoani.tablisknu.tablist.TablistManager;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.reflection.Reflection;
import us.tlatoani.mundocore.base.Scheduling;
import us.tlatoani.tablisknu.packetwrapper.WrapperPlayServerPlayerInfo;
import us.tlatoani.tablisknu.packetwrapper.WrapperPlayServerScoreboardScore;
import us.tlatoani.tablisknu.packetwrapper.WrapperPlayServerScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import us.tlatoani.tablisknu.util.WorldLockedLocation;

import java.util.*;

/**
 * Created by Tlatoani on 1/20/18.
 */
public class ProfileManager {
    static final Map<Player, ModifiableProfile> profileMap = new HashMap<>();

    private static final ArrayList<Player> spawnedPlayers = new ArrayList<>();

    private static Reflection.MethodInvoker CRAFT_PLAYER_GET_HANDLE = null;
    private static Reflection.MethodInvoker DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = null;
    private static Object DIMENSION_MANAGER_OVERWORLD = null;
    private static Object DIMENSION_MANAGER_NETHER = null;
    private static Object DIMENSION_MANAGER_THE_END = null;

    public static void load() {
        loadReflectionStuff();
        loadPacketEvents();
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                PlayerSkinRetrieval.onJoin(event.getPlayer());
            }
        }, Tablisknu.get());
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                ProfileManager.onQuit(event.getPlayer());
            }
        }, Tablisknu.get());
    }

    private static void loadReflectionStuff() {
        try {
            CRAFT_PLAYER_GET_HANDLE = Reflection.getTypedMethod(Reflection.getCraftBukkitClass("entity.CraftPlayer"), "getHandle", Reflection.getMinecraftClass("EntityPlayer"));
            DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = Reflection.getMethod(
                    Reflection.getMinecraftClass("DedicatedPlayerList"), "moveToWorld",
                    Reflection.getMinecraftClass("EntityPlayer"),
                    Reflection.getMinecraftClass("DimensionManager"),
                    boolean.class,
                    Location.class,
                    boolean.class
            );
            Class<?> dimensionManagerClass = Reflection.getMinecraftClass("DimensionManager");
            DIMENSION_MANAGER_OVERWORLD = Reflection.getStaticField(dimensionManagerClass, "OVERWORLD");
            DIMENSION_MANAGER_NETHER = Reflection.getStaticField(dimensionManagerClass, "NETHER");
            DIMENSION_MANAGER_THE_END = Reflection.getStaticField(dimensionManagerClass, "THE_END");
        } catch (Exception e) {
            Logging.reportException(ProfileManager.class, e);
        }
    }

    private static void loadPacketEvents() {
        PacketUtil.onPacketEvent(PacketType.Play.Server.PLAYER_INFO, event -> {
            if (!PacketUtil.validatePacketEvent(event)) {
                return;
            }
            Player target = event.getPlayer();
            WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());
            if (packet.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER || packet.getAction() == EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME) {
                Logging.debug(ProfileManager.class, "Player Info, target = " + target.getName() + ", action = " + packet.getAction());
                List<PlayerInfoData> oldData = packet.getData();
                List<PlayerInfoData> newData = new ArrayList<>(oldData.size());
                for (PlayerInfoData oldPlayerInfoData : oldData) {
                    Player player = Bukkit.getPlayer(oldPlayerInfoData.getProfile().getUUID());
                    if (player == null) {
                        newData.add(oldPlayerInfoData);
                        continue;
                    }
                    Logging.debug(ProfileManager.class, "Player Info Packet: " + player.getName());
                    if (!spawnedPlayers.contains(player)) {
                        Logging.debug(ProfileManager.class, "New player!");
                        spawnedPlayers.add(player);
                    }
                    Logging.debug(ProfileManager.class, "Old nametag = " + oldPlayerInfoData.getProfile().getName());
                    Specific specificProfile = getProfile(player).getSpecificProfile(target);
                    WrappedChatComponent displayName = oldPlayerInfoData.getDisplayName();
                    Logging.debug(ProfileManager.class, "Old displayName = " + displayName);
                    if (displayName == null) {
                        String rawDisplayName = Optional
                                .ofNullable(target.getScoreboard())
                                .map(scoreboard -> scoreboard.getEntryTeam(player.getName()))
                                .map(team -> team.getPrefix() + player.getName() + team.getSuffix())
                                .orElse(player.getName());
                        displayName = WrappedChatComponent.fromText(rawDisplayName);
                        Logging.debug(ProfileManager.class, "New displayName = " + displayName);
                    }
                    String nametag = specificProfile.getNametag();
                    //Code change to allow NameTagEdit to work theoretically
                    if (nametag.equals(player.getName())) {
                        nametag = oldPlayerInfoData.getProfile().getName();
                    }
                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(
                            oldPlayerInfoData.getProfile().withName(nametag),
                            oldPlayerInfoData.getLatency(),
                            oldPlayerInfoData.getGameMode(),
                            displayName
                    );
                    Logging.debug(ProfileManager.class, "New nametag = " + newPlayerInfoData.getProfile().getName());
                    if (packet.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                        Skin skin = specificProfile.getDisplayedSkin();
                        Logging.debug(ProfileManager.class, "Skin replacement (may not exist): " + skin);
                        if (skin != null) {
                            newPlayerInfoData.getProfile().getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
                        }
                    }
                    newData.add(newPlayerInfoData);
                }
                packet.setData(newData);
            }
        });

        PacketUtil.onPacketEvent(PacketType.Play.Server.SCOREBOARD_TEAM, event -> {
            if (!PacketUtil.validatePacketEvent(event)) {
                return;
            }
            Player target = event.getPlayer();
            WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam(event.getPacket());
            Logging.debug(ProfileManager.class, "Scoreboard Team Packet");
            if (packet.getMode() == WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED) {
                Collection<String> modifiedNames = Optional
                        .ofNullable(target.getScoreboard())
                        .map(scoreboard -> scoreboard.getTeam(packet.getName()))
                        .map(Team::getEntries)
                        .orElse(Collections.emptySet());
                for (String name : modifiedNames) {
                    Player player = Bukkit.getPlayerExact(name);
                    if (player != null && player.isOnline()) {
                        Logging.debug(ProfileManager.class, "Player " + name + ", updating");
                        PacketUtil.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), ProfileManager.class, target);
                    }
                }
            } else if (packet.getMode() == WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PacketUtil.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), ProfileManager.class, target);
                    Logging.debug(ProfileManager.class, "Player " + player.getName() + ", updating");
                }
            } else {
                Collection<String> oldNames = packet.getPlayers();
                Collection<String> newNames = new HashSet<>(oldNames.size());
                for (String name : oldNames) {
                    newNames.add(name);
                    Player player = Bukkit.getPlayerExact(name);
                    if (player != null && player.isOnline()) {
                        Specific specificProfile = getProfile(player).getSpecificProfile(target);
                        String nameTag = specificProfile.getNametag();
                        if (!name.equals(nameTag)) {
                            newNames.add(nameTag);
                        }
                        Logging.debug(ProfileManager.class, "Player " + name + ", nameTag = " + nameTag);
                        PacketUtil.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME), ProfileManager.class, target);
                    }
                }
                Logging.debug(ProfileManager.class, "oldNames = " + oldNames);
                Logging.debug(ProfileManager.class, "newNames = " + newNames);
                packet.setPlayers(new ArrayList<>(newNames));
            }
        });

        PacketUtil.onPacketEvent(PacketType.Play.Server.SCOREBOARD_SCORE, event -> {
            if (!PacketUtil.validatePacketEvent(event)) {
                return;
            }
            Player target = event.getPlayer();
            WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore(event.getPacket());
            Optional
                    .ofNullable(packet.getScoreName())
                    .map(Bukkit::getPlayerExact)
                    .ifPresent(player -> {
                        packet.setScoreName(getProfile(player).getSpecificProfile(target).getNametag());
                        Logging.debug(ProfileManager.class, "Replacing score for player = " + player);
                    });
        });
    }

    //Join/Leave Events

    private static void onQuit(Player player) {
        profileMap.remove(player);
        for (ModifiableProfile generalProfile : profileMap.values()) {
            generalProfile.onQuit(player);
        }
        spawnedPlayers.remove(player);
    }

    //API Stuffs

    public static ModifiableProfile getProfile(Player player) {
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Player must be non-null and online: " + player);
        }
        return profileMap.computeIfAbsent(player, ModifiableProfile::new);
    }

    //Manipulations stuffs

    static void refreshPlayer(Player player, Player target) {
        if (!spawnedPlayers.contains(player)) {
            return;
        }
        if (player.equals(target)) {
            respawnPlayer(player);
            return;
        }
        target.hidePlayer(Tablisknu.get(), player);
        Scheduling.syncDelay(1, () -> target.showPlayer(Tablisknu.get(), player));
        //DO NOT REMOVE THE FOLLOWING CODE
        //It ensures that targets who are not currently tracking the player and thus will not receive a spawn packet
        //still have the tab hidden for them if necessary
        Scheduling.syncDelay(2, () -> {
            if (!TablistManager.getTablistOfPlayer(target).isPlayerVisible(player)) {
                PacketUtil.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), ProfileManager.class, target);
            }
        });
    }

    private static void respawnPlayer(Player player) {
        PacketUtil.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), ProfileManager.class, player);
        PacketUtil.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), ProfileManager.class, player);

        Location playerLoc = new WorldLockedLocation(player.getLocation());
        Logging.debug(ProfileManager.class, "playerLoc = " + playerLoc);
        try {
            Logging.debug(ProfileManager.class, "DEDICATED_PLAYER_LIST_MOVE_TO_WORLD: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            Logging.debug(ProfileManager.class, "NMS_SERVER: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            DEDICATED_PLAYER_LIST_MOVE_TO_WORLD.invoke(
                    Reflection.NMS_SERVER,
                    CRAFT_PLAYER_GET_HANDLE.invoke(player),
                    dimensionToManager(playerLoc.getWorld().getEnvironment()),
                    true,
                    playerLoc,
                    true
            );
        } catch (Exception e) {
            Logging.debug(ProfileManager.class, "Failed to make player see his skin change: " + player.getName());
            Logging.reportException(ProfileManager.class, e);
        }
    }

    private static Object dimensionToManager(World.Environment dimension) {
        switch (dimension) {
            case NORMAL: return DIMENSION_MANAGER_OVERWORLD;
            case NETHER: return DIMENSION_MANAGER_NETHER;
            case THE_END: return DIMENSION_MANAGER_THE_END;
        }
        throw new IllegalArgumentException("Illegal dimension = null");
    }
}
