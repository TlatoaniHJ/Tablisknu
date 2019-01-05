package us.tlatoani.tablisknu.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.ChatColor;
import us.tlatoani.tablisknu.Tablisknu;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.tablist.Tablist;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.tablisknu.packetwrapper.WrapperPlayServerPlayerInfo;
import us.tlatoani.tablisknu.packetwrapper.WrapperPlayServerScoreboardScore;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 8/14/17.
 */
public class PacketUtil {

    public static PacketContainer playerInfoPacket(
            String displayName,
            Integer latencyBars,
            GameMode gameMode,
            String name,
            UUID uuid,
            Skin skin,
            EnumWrappers.PlayerInfoAction action
    ) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (skin == null) {
                skin = Tablist.DEFAULT_SKIN_TEXTURE;
            }
            profile.getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                Optional
                        .ofNullable(latencyBars)
                        .map(PacketUtil::getPossibleLatency)
                        .orElse(0),
                Optional
                        .ofNullable(gameMode)
                        .map(EnumWrappers.NativeGameMode::fromBukkit)
                        .orElse(EnumWrappers.NativeGameMode.NOT_SET),
                WrappedChatComponent.fromText(Optional.ofNullable(displayName).orElse(""))
        );
        packet.setData(Collections.singletonList(playerInfoData));
        packet.setAction(action);
        return packet.getHandle();
    }

    public static PacketContainer playerInfoPacket(Player player, EnumWrappers.PlayerInfoAction action) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        PlayerInfoData playerInfoData = new PlayerInfoData(
                WrappedGameProfile.fromPlayer(player),
                5,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                null
        );
        packet.setData(Collections.singletonList(playerInfoData));
        packet.setAction(action);
        return packet.getHandle();
    }

    public static PacketContainer scorePacket(
            String scoreName,
            String objectiveName,
            Integer score,
            EnumWrappers.ScoreboardAction action
    ) {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setScoreName(scoreName);
        packet.setObjectiveName(objectiveName);
        packet.setValue(Optional.ofNullable(score).orElse(0));
        packet.setScoreboardAction(action);
        return packet.getHandle();
    }

    public static WrappedChatComponent stringsToChatComponent(List<String> strings) {
        if (strings.isEmpty()) {
            return WrappedChatComponent.fromText("");
        }
        StringJoiner joiner = new StringJoiner(", {\"text\":\"\n\"}, ", "{\"extra\":[", "],\"text\":\"\"}");
        for (String string : strings) {
            joiner.add(WrappedChatComponent.fromText(ChatColor.RESET + string).getJson());
        }
        Logging.debug(PacketUtil.class, "Final JSON: " + joiner.toString());
        return WrappedChatComponent.fromJson(joiner.toString());
    }

    public static int getPossibleLatency(int latencyBars) {
        switch (latencyBars) {
            case 0: return -1;
            case 1: return 1024;
            case 2: return 768;
            case 3: return 512;
            case 4: return 256;
            case 5: return 0;
            default: throw new IllegalArgumentException(
                    "Illegal amount of latency bars: " + latencyBars + ", required 0 <= latency <= 5");
        }
    }

    public static boolean validatePacketEvent(PacketEvent event) {
        return !event.isCancelled() && event.getPlayer() != null && !event.isPlayerTemporary() && event.getPlayer().isOnline();
    }

    public static void onPacketEvent(PacketType packetType, Consumer<PacketEvent> handler) {
        onPacketEvent(packetType, ListenerPriority.NORMAL, handler);
    }

    public static void onPacketEvent(PacketType packetType, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Tablisknu.get(), priority, packetType) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handler.accept(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                handler.accept(event);
            }
        });
    }

    public static void onPacketEvent(PacketType[] packetTypes, Consumer<PacketEvent> handler) {
        onPacketEvent(packetTypes, ListenerPriority.NORMAL, handler);
    }

    public static void onPacketEvent(PacketType[] packetTypes, ListenerPriority priority, Consumer<PacketEvent> handler) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Tablisknu.get(), priority, packetTypes) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handler.accept(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                handler.accept(event);
            }
        });
    }

    public static void sendPacket(PacketContainer packet, Object exceptLoc, Player player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }

    public static void sendPacket(PacketContainer packet, Object exceptLoc, Player[] players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }

    public static void sendPacket(PacketContainer packet, Object exceptLoc, Iterable<Player> players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            Logging.reportException(exceptLoc, e);
        }
    }
}
