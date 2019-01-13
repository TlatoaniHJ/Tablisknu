package us.tlatoani.tablisknu.tablist_array;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.tablist_general.TablistMundo;

public class ArrayTablistMundo {

    public static void load() {
        Registration.registerEffect(EffEnableDisableArrayTablist.class,
                "(enable|activate) [the] array tablist " + TablistMundo.FOR_TABLIST_OWNER
                        + " [with [%-number% columns] [%-number% rows] [(default|initial) (head|icon|player_head) %-skin%]]",
                "(disable|deactivate) [the] array tablist " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Enable or Disable Array Tablist", "1.0",
                        "Enables or disables array tablist in the specified or all tablist(s) "
                        + "The array tablist creates a grid of tabs that allows you to use grid coordinates to easily modify individual tabs. "
                        + "When enabling, you can specify the amount of columns (defaults to 4), the amount of rows (defaults to 20), "
                        + "and the initial icon (defaults to a white texture). "
                        + "See the Size of Array Tablist and Icon of Array Tab expressions for more info. "
                        + "Note that it is recommended to use the default tablist dimensions of 4x20 to avoid issues with skins. "
                        + "If the array tablist is already enabled, it will be replaced with a blank one matching the new specifications. "
                        + "Note that when enabling the array tablist, all simple tabs will be removed and all player tabs will be hidden. "
                        + "When disabling the array tablist, all player tabs will be visible, "
                        + "and there will be no simple tabs.")
                .example("command /example_tablist:"
                        , "\ttrigger:"
                        , "\t\tenable array tablist for player "
                        + "#This creates a 4 x 20 grid of tabs in the player's tablist, there is further syntax for other amounts of rows/columns"
                        , "\t\tset display name of tab 1, 1 for player to \"Hello!\" #This sets the first tab as \"Hello!\""
                        , "\t\tset icon of tab 4, 20 for player to alex #This sets the icon of the last tab as the alex skin"
                        , "\t\tset display name of tab 4, 20 for player to \"ALEX\" #This sets the last tab as \"ALEX!\""
                        , "\t\tloop 20 times:"
                        , "\t\t\tset display name of tab 2, loop-number for player to \"Column 2, Row %loop-number%\" "
                        + "#This sets all of the tabs in the second column to display their column and row"
                        , "\t\t\tset icon of tab 2, loop-number for player to steve "
                        + "#This sets all of the tabs in the second column to have a steve skin as their icon");
        Registration.registerEffect(EffAddRemoveArrayTabs.class,
                "add ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) [with icon %-skin%] to [the] [array] tablist[s] " + TablistMundo.OF_TABLIST_OWNER,
                "add ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) [with icon %-skin%] to " + TablistMundo.TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]",
                "remove ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) from [the] [array] tablist[s] " + TablistMundo.OF_TABLIST_OWNER,
                "remove ([a] (0¦column|1¦row)|%-number% (0¦column|1¦row)s) from " + TablistMundo.TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]")
                .document("Add or Remove Columns or Rows in Tablist", "1.0",
                        "Adds or removes the specified amount of rows or columns to the specified or all tablist(s), "
                        + "optionally giving those rows/columns the specified icon instead of the default for the tablist(s).");
        Registration.registerExpression(ExprDisplayNameOfArrayTab.class, String.class, ExpressionType.PROPERTY,
                "[the] [display] name of [the] [array] tab %number%, %number% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Display Name of Array Tab", "1.0",
                        "An expression for the display name of the specified array tab in the specified or all tablist(s).");
        Registration.registerExpression(ExprLatencyBarsOfArrayTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] (latency|ping) [bars] of [the] [array] tab %number%, %number% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Latency Bars of Array Tab", "1.0",
                        "An expression for the amount of latency bars of the specified array tab in the specified or all tablist(s). "
                        + "This is always an integer between 0 and 5 (inclusive).");
        Registration.registerExpression(ExprIconOfArrayTab.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (head|icon|skull) of [the] [array] tab %number%, %number% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Icon of Array Tab", "1.0",
                        "An expression for either the icon of the specified array tab in the specified or all tablist(s).");
        Registration.registerExpression(ExprScoreOfArrayTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] score of [the] [array] tab %number%, %number% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Score of Array Tab", "1.0",
                        "An expression for the score of the specified array tab in the specified or all tablist(s).");
        Registration.registerExpression(ExprDimensionOfArrayTablist.class, Number.class, ExpressionType.PROPERTY,
                "[the] amount of (0¦column|1¦row)s in " + TablistMundo.TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]")
                .document("Dimension of Array Tablist", "1.0",
                        "An expression for the amount of rows or columns in the specified or all array tablist(s). "
                        + "There can be 1 to 4 columns."
                        + "For each amount of columns, there is a specified range for the amount of rows:"
                        , "1 Column: 1 to 20 rows"
                        , "2 Columns: 11 to 20 rows"
                        , "3 Columns: 14 to 20 rows"
                        , "4 Columns: 16 to 20 rows"
                        , "This is due to the fact that Minecraft allows 1 to 80 total tabs, and for each amount, "
                        + "there is only one way the tablist can appear. "
                        + "Minecraft only allows a maximum of 20 tabs in one column, "
                        + "so the tabs will try to fill as few columns as possible will adhering to this rule. "
                        + "For example, if there are 40 tabs, this is satisfied by a 2x20 tablist, but for 41 and 42 you need 3x14.")
                .changer(Changer.ChangeMode.SET, Number.class, "1.0",
                        "Sets the amount of rows or columns. "
                        + "It's recommended to use the Add or Remove Columns or Rows in Tablist effect in place of this, "
                        + "especially if you want to set the new columns/rows to have an icon different from the default icon.");
        Registration.registerExpressionCondition(CondArrayTablistEnabled.class, ExpressionType.PROPERTY,
                "[the] array tablist is (0¦enabled|1¦disabled) " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Array Tablist is Enabled", "1.0",
                        "Checks whether the array tablist is enabled or disabled in the specified or all tablist(s). "
                        + "See the Enable or Disable Array Tablist effect for more info.");
    }
}
