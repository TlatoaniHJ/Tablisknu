package us.tlatoani.tablisknu.player_head;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.tlatoani.mundocore.reflection.Reflection;
import us.tlatoani.tablisknu.skin.Skin;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 2/18/18.
 * A utility class for dealing with player heads
 */
public abstract class HeadUtil {
    public static final String DEFAULT_HEAD_OWNER = "MundoSK-Name";
    public static final UUID DEFAULT_UUID = UUID.fromString("10001000-1000-3000-8000-100010001000");
    public static final Reflection.FieldAccessor CRAFT_META_SKULL_PROFILE =
            Reflection.getField(
                    Reflection.getCraftBukkitClass("inventory.CraftMetaSkull"),
                    "profile",
                    Reflection.getClass("com.mojang.authlib.GameProfile"));
    public static final Reflection.FieldAccessor CRAFT_SKULL_PROFILE =
            Reflection.getField(
                    Reflection.getCraftBukkitClass("block.CraftSkull"),
                    "profile",
                    Reflection.getClass("com.mojang.authlib.GameProfile"));

    /**
     * @return A {@link WrappedGameProfile} containing the information representing the owner and skin of this head,
     * or null for uncertain reasons
     */
    @Nullable
    abstract WrappedGameProfile getGameProfile();

    /**
     * Sets the game profile of this head in order to change the owner and/or skin.
     * @param gameProfile The {@link WrappedGameProfile} containing the new owner/skin
     */
    abstract void setGameProfile(WrappedGameProfile gameProfile);

    /**
     * @return The skin of this head, or {@link Skin#EMPTY} if it does not have a skin
     */
    public Skin getSkin() {
        WrappedGameProfile gameProfile = getGameProfile();
        return gameProfile == null ? Skin.EMPTY : Skin.fromGameProfile(gameProfile);
    }

    /**
     * Sets the skin of this head
     * @param skin The new skin
     */
    public void setSkin(Skin skin) {
        String owner = getOwner();
        setSkinAndOwner(skin, owner == null ? DEFAULT_HEAD_OWNER : owner);
    }

    /**
     * @return The owner of this head
     */
    public String getOwner() {
        WrappedGameProfile gameProfile = getGameProfile();
        return gameProfile == null ? null : gameProfile.getName();
    }

    /**
     * Sets the owner of this head
     * @param owner The new owner
     */
    public void setOwner(String owner) {
        Skin skin = getSkin();
        setGameProfile(skin == null ? new WrappedGameProfile(DEFAULT_UUID, owner) : skin.toGameProfile(owner));
    }

    /**
     * Sets the skin and the owner of this head
     * @param skin The new skin
     * @param owner The new owner
     */
    public void setSkinAndOwner(Skin skin, String owner) {
        setGameProfile(skin.toGameProfile(owner));
    }

    /**
     * Creates a new player head
     * @return The created head
     */
    public static ItemStack playerHeadItem() {
        Held heldSkull = new Held();
        return heldSkull.item;
    }

    /**
     * Creates a player head in the form of an {@link ItemStack} of type {@link Material#PLAYER_HEAD},
     * skin {@code skin}, and owner {@link #DEFAULT_HEAD_OWNER}
     * @param skin The skin of the new player head
     * @return The created player head
     */
    public static ItemStack playerHeadItem(Skin skin) {
        return playerHeadItem(skin, DEFAULT_HEAD_OWNER);
    }

    /**
     * Creates a player head in the form of an {@link ItemStack} of type {@link Material#PLAYER_HEAD},
     * skin {@code skin}, and owner {@code owner}
     * @param skin The skin of the new player head
     * @param owner The owner of the new player head
     * @return The created player head
     */
    public static ItemStack playerHeadItem(Skin skin, String owner) {
        Held heldSkull = new Held();
        heldSkull.setSkinAndOwner(skin, owner);
        return heldSkull.item;
    }

    /**
     * Returns a Held of {@code itemStack} if its type is {@link Material#PLAYER_HEAD}
     * otherwise returns {@link Optional#empty()}.
     * @param itemStack The item to contain in a Held
     * @return An {@link Optional} containing {@code itemStack} if its type is {@link Material#PLAYER_HEAD}
     * {@link Optional#empty()} otherwise
     */
    public static Optional<Held> from(ItemStack itemStack) {
        if (itemStack.getType() == Material.PLAYER_HEAD) {
            return Optional.of(new Held(itemStack));
        }
        return Optional.empty();
    }

    /**
     * Returns a Placed of {@code block} if its type is
     * {@link Material#PLAYER_HEAD} or {@link Material#PLAYER_WALL_HEAD}
     * otherwise returns {@link Optional#empty()}.
     * @param block The block to be contained in a Placed
     * @return An {@link Optional} containing {@code block} if its type is one of the player head types
     * {@link Optional#empty()} otherwise
     */
    public static Optional<Placed> from(Block block) {
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
            return Optional.of(new Placed(block));
        }
        return Optional.empty();
    }

    public static class Held extends HeadUtil {
        public final ItemStack item;

        private Held() {
            this(new ItemStack(Material.PLAYER_HEAD));
        }

        private Held(ItemStack item) {
            this.item = item;
            if (item.getType() != Material.PLAYER_HEAD) {
                throw new IllegalArgumentException("Illegal type: " + item.getType() + ", should be PLAYER_HEAD");
            }
        }

        @Override
        WrappedGameProfile getGameProfile() {
            return WrappedGameProfile.fromHandle(CRAFT_META_SKULL_PROFILE.get(item.getItemMeta()));
        }

        @Override
        void setGameProfile(WrappedGameProfile gameProfile) {
            ItemMeta skullMeta = item.getItemMeta();
            CRAFT_META_SKULL_PROFILE.set(skullMeta, gameProfile.getHandle());
            item.setItemMeta(skullMeta);
        }
    }

    public static class Placed extends HeadUtil {
        public final Block block;

        private Placed(Block block) {
            this.block = block;
            if (block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD) {
                throw new IllegalArgumentException("Illegal block type: " + block.getType() + ", should be PLAYER_HEAD or PLAYER_WALL_HEAD");
            }
        }

        private Skull getState() {
            return (Skull) block.getState();
        }

        @Override
        WrappedGameProfile getGameProfile() {
            return WrappedGameProfile.fromHandle(CRAFT_SKULL_PROFILE.get(getState()));
        }

        @Override
        void setGameProfile(WrappedGameProfile gameProfile) {
            BlockState skull = getState();
            CRAFT_SKULL_PROFILE.set(skull, gameProfile.getHandle());
            skull.update();
        }
    }
}
