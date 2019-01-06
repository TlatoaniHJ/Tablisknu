package us.tlatoani.tablisknu.tablist_group;

import ch.njol.skript.lang.ExpressionType;
import org.bukkit.entity.Player;
import us.tlatoani.mundocore.registration.Registration;

public class TablistGroupMundo {

    public static void load() {
        Registration.registerEffect(EffAddToTablistGroup.class,
                "add %players% to tablist group %string%")
                .document("Add Players to Tablist Group", "1.0",
                        "Adds the specified players to the specified tablist group. "
                        + "This means that you can make modifications to the tablist of the specified group, "
                        + "and those modifications will be applied to those players that have been added to the group. "
                        + "However, you can also modify the tablist of an individual player "
                        + "and the tablists of the rest of the group will be unchanged. "
                        + "Players can be added to multiple tablist groups without conflict "
                        + "as long as the modifications made to each tablist group are independent of each other, "
                        + "meaning they don't both, for example, change the display name of the same tab, "
                        + "or one of them change the display name while the other one hides the tab "
                        + "(you can see how that would conflict). "
                        + "The modifications that have been previously applied to the specified tablist group "
                        + "will be applied to the specified players "
                        + "(modifications that have become redundant will not be applied). ");
        Registration.registerEffect(EffRemoveFromTablistGroup.class,
                "remove %players% from tablist group %string%")
                .document("Remove Players from Tablist Group", "1.0",
                        "Removes the specified players from the specified tablist group. "
                        + "This effect will not make any modifications to the tablists of the specified players. ");
        Registration.registerEffect(EffEmptyGroup.class, "empty tablist group %string%")
                .document("Empty Tablist Group", "1.0",
                        "Removes all players from the specified tablist group. "
                        + "Like the Remove Players from Tablist Group effect, "
                        + "this will not make any modifications to the tablists of those players. "
                        + "This will also not reset modifications that have been applied to the specified tablist group, "
                        + "meaning that if a new player were to be added to the group, "
                        + "modifications applied to the group previously would be applied to the player. "
                        + "If you also want to reset these modifications, see the Delete Tablist Group effect.");
        Registration.registerEffect(EffDeleteGroup.class, "delete tablist group %string%")
                .document("Delete Tablist Group", "1.0",
                        "Removes all players from the specified tablist group. "
                                + "Like the Remove Players from Tablist Group effect, "
                                + "this will not make any modifications to the tablists of those players. "
                                + "This will also reset modifications that have been applied to the specified tablist group, "
                                + "meaning that if a new player were to be added to the group, "
                                + "modifications applied to the group before it was deleted would not be applied to the player. "
                                + "If you don't want to reset these modifications, see the Empty Tablist Group effect.");
        Registration.registerEffect(EffDeleteGroup.class, "delete tablist group %string%");
        Registration.registerExpression(ExprTablistGroup.class, Player.class, ExpressionType.PROPERTY,
                "[the] members of tablist group %string%", "tablist group %string%'s members")
                .document("Members of Tablist Group", "1.0",
                        "This is a list of the players who are currently in the specified tablist group.");
    }
}
