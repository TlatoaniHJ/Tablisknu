package us.tlatoani.tablisknu.tablist_player;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.tablist_general.TablistMundo;

public class PlayerTablistMundo {

    public static void load() {
        Registration.registerEffect(EffShowHidePlayerTab.class,
                "(0¦show|1¦hide) [the] [player] tab[s] of %players% " + TablistMundo.FOR_TABLIST_OWNER,
                "(0¦show|1¦hide) %players%'[s] [player] tab[s] " + TablistMundo.FOR_TABLIST_OWNER,
                "(0¦show|1¦hide) %players% in " + TablistMundo.TABLIST_OWNER_POSSESSIVE + " tablist[s]",
                "(0¦show|1¦hide) %players% in [the] tablist[s] " + TablistMundo.OF_TABLIST_OWNER,
                "(0¦show|1¦hide) %players% " + TablistMundo.FOR_TABLIST_OWNER + " in [the] tablist[s]")
                .document("Show or Hide in Tablist", "1.0",
                        "Shows or hides certain players for other certain players in their tablist(s). "
                        + "Note: if the Players Are Visible condition/expression is set to false for a specific player "
                        + "and you show a player for them using this effect, "
                        + "then the condition/expression will become true but only that player will become unhidden.");
        Registration.registerEffect(EffClearPlayerModifications.class,
                "(clear|reset) [all] player tab modifications " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Clear Player Tab Modifications", "1.0",
                        "Resets all of the tabs representing players for the specified player(s) to normal. "
                        + "This will make all players visible in the tablist and reset any display name, latency, and score changes.");
        Registration.registerExpression(ExprDisplayNameOfPlayerTab.class, String.class, ExpressionType.PROPERTY,
                "[the] [display] name of [the] [player] tab of %player% " + TablistMundo.FOR_TABLIST_OWNER,
                "[the] [display] name of %player%'s [player] tab " + TablistMundo.FOR_TABLIST_OWNER,
                "[the] [tablisknu] tablist name of %player% " + TablistMundo.FOR_TABLIST_OWNER,
                "%player%'s [tablisknu] tablist name " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Display Name of Player Tab", "1.0",
                        "An expression for the display name of the specified player tab "
                        + "in the tablist of the specified player(s). "
                        + "This will not be set if the player tab's display name has not been changed (or was reset), "
                        + "or the player tab is hidden.");
        Registration.registerExpression(ExprLatencyBarsOfPlayerTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] (latency|ping) [bars] of [the] [player] tab of %player% " + TablistMundo.FOR_TABLIST_OWNER,
                "(latency|ping) [bars] of %player%'s [player] tab " + TablistMundo.FOR_TABLIST_OWNER,
                "[the] tablist (latency|ping) [bars] of %player% " + TablistMundo.FOR_TABLIST_OWNER,
                "%player%'s tablist (latency|ping) [bars] " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Latency Bars of Player Tab", "1.0",
                        "An expression for the amount of latency bars of the specified player tab "
                        + "in the tablist of the specified player(s). "
                        + "When set, this is always between 0 and 5. This will not be set if the player tab is hidden.")
                .changer(Changer.ChangeMode.RESET, "1.0",
                        "Resets any modification of the latency bars to match the player's actual latency. "
                        + "Initially, the latency bars will appear to be 5, but will change within 30 seconds "
                        + "if the player's actual latency requires a different amount of bars.");
        Registration.registerExpression(ExprScoreOfPlayerTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] score of [the] [player] tab of %player% " + TablistMundo.FOR_TABLIST_OWNER,
                "[the] score of %player%'s [player] tab " + TablistMundo.FOR_TABLIST_OWNER,
                "[the] tablist score of %player% " + TablistMundo.FOR_TABLIST_OWNER,
                "%player%'s tablist score " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Score of Player Tab", "1.0",
                        "An expression for the score of the specified player tab "
                        + "in the tablist of the specified player(s). "
                        + "This will not be set if the player tab is hidden.");
        Registration.registerExpressionCondition(CondPlayerTabIsVisible.class, ExpressionType.COMBINED,
                "[the] [player] tab of %player% is (0¦visible|1¦hidden) " + TablistMundo.FOR_TABLIST_OWNER,
                "%player%'s [player] tab is (0¦visible|1¦hidden) " + TablistMundo.FOR_TABLIST_OWNER,
                "%player% is (0¦visible|1¦hidden) in " + TablistMundo.TABLIST_OWNER_POSSESSIVE + " tablist[s]",
                "%player% is (0¦visible|1¦hidden) in [the] tablist[s] " + TablistMundo.FOR_OF_TABLIST_OWNER)
                .document("Player Tab is Visible", "1.0",
                        "Checks whether the first player's tab is visible for the second specified player(s).");
        Registration.registerExpressionCondition(CondPlayerTabsAreVisible.class, ExpressionType.PROPERTY,
                "player tabs (0¦are|1¦aren't|1¦are not) visible " + TablistMundo.FOR_TABLIST_OWNER,
                TablistMundo.TABLIST_OWNER_POSSESSIVE + " tablist[s] (contains|(0¦does|0¦do|1¦doesn't|1¦does not|1¦don't|1¦do not) contain) players",
                "[the] tablist[s] " + TablistMundo.OF_TABLIST_OWNER + " (contains|(0¦does|0¦do|1¦doesn't|1¦does not|1¦don't|1¦do not) contain) players",
                "players are (0¦visible|1¦hidden) in [the] tablist[s] " + TablistMundo.FOR_OF_TABLIST_OWNER,
                "players are (0¦visible|1¦hidden) in " + TablistMundo.TABLIST_OWNER_POSSESSIVE + " tablist[s]")
                .document("Player Tabs Are Visible", "1.0",
                        "Checks whether the tablist(s) of the specified player(s) allow player tabs to be visible. "
                        + "Setting this to false prevents any player tabs from being seen in the tablist for the specified player(s) "
                        + "(players who join will be automatically hidden). "
                        + "Setting this to true immediately makes all player tabs visible in the tablist for the specified player(s). "
                        + "Use the Show or Hide in Tablist effect if you would like to set this condition to be true "
                        + "without immediately showing all players. "
                        + "Note that it is possible for this condition/expression to be true yet no player tabs are visible "
                        + "if they are hidden manually using the Show or Hide in Tablist effect. "
                        + "In this case, players who join will still be visible in the tablist unless manually hidden using the effect.");
    }
}
