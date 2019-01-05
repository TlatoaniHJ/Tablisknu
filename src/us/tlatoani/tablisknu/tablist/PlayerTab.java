package us.tlatoani.tablisknu.tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.util.PacketUtil;

/**
 * Used to simplify creation of {@link Tab}s corresponding to players,
 * catch bugs related to players who are not online,
 * and catch bugs related to calling {@link Tab#setIcon(Skin)}
 * (since this will cause problems with the player's actual skin).
 */
public class PlayerTab extends Tab {
    private PlayerTablist playerTablist;
    private final Player objPlayer;

    /**
     * Initializes a PlayerTab corresponding to {@code player}.
     * @param player
     * @throws IllegalArgumentException If {@code !player.isOnline()}
     */
    PlayerTab(PlayerTablist playerTablist, Player player) {
        super(playerTablist.tablist, player.getName(), player.getUniqueId());
        this.playerTablist = playerTablist;
        if (!player.isOnline()) {
            throw new IllegalArgumentException("The player parameter in the constructor of PlayerTab must be online: " + player);
        }
        objPlayer = player;
    }

    @Override
    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
        return PacketUtil.playerInfoPacket(objPlayer, action);
    }

    @Override
    public void setIcon(Skin value) {
        throw new UnsupportedOperationException("You can't set the icon of a PlayerTab!");
    }
}
