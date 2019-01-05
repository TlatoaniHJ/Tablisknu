package us.tlatoani.tablisknu.tablist_group;

import ch.njol.skript.lang.ExpressionType;
import org.bukkit.entity.Player;
import us.tlatoani.mundocore.registration.Registration;

public class TablistGroupMundo {

    public static void load() {
        Registration.registerEffect(EffAddToTablistGroup.class, "add %players% to tablist group %string%");
        Registration.registerEffect(EffRemoveFromTablistGroup.class, "remove %players% from tablist group %string%");
        Registration.registerEffect(EffEmptyGroup.class, "empty tablist group %string%");
        Registration.registerEffect(EffDeleteGroup.class, "delete tablist group %string%");
        Registration.registerExpression(ExprTablistGroup.class, Player.class, ExpressionType.PROPERTY,
                "[the] members of tablist group %string%", "tablist group %string%'s members");
    }
}
