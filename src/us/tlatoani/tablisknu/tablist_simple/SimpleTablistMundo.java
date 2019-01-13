package us.tlatoani.tablisknu.tablist_simple;

import ch.njol.skript.lang.ExpressionType;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.tablist_general.TablistMundo;

public class SimpleTablistMundo {

    public static void load() {
        Registration.registerExpression(ExprPriorityOfSimpleTab.class, String.class, ExpressionType.PROPERTY,
                "[the] priority of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Priority of Simple Tab", "1.0",
                        "This is the priority of the specified simple tab in the specified or all tablist(s). "
                        + "The priority is a string (at most 12 characters) "
                        + "that determines the position that the specified simple tab will take relative to the other simple tabs. "
                        + "Specifically, simple tabs are arranged alphabetically by their priority. "
                        + "By default the priority is the first 12 characters of the simple tab's id.");

        Registration.registerEffect(EffCreateSimpleTab.class,
                "create [a] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER + " [with "
                        + "[priority %-string%] "
                        + "[[display] name %-string%] "
                        + "[(ping|latency) [bars] %-number%] "
                        + "[(head|icon|skull) %-skin%] "
                        + "[score %-number%]]")
                .document("Create Simple Tab", "1.0",
                        "Creates a simple tab in the specified or all tablist(s) with the specified id and properties. "
                        + "If a specified player already has a simple tab with the specified id in their tablist, "
                        + "that tab will be deleted before creating the new tab. "
                        + "This effect will not work for a specified player if they have the array tablist enabled.");
        Registration.registerEffect(EffDeleteSimpleTab.class,
                "delete [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Delete Simple Tab", "1.0",
                        "Removes the simple tab with the specified id from the specified or all tablist(s)");
        Registration.registerEffect(EffDeleteAllSimpleTabs.class,
                "delete all simple tabs " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Delete All Simple Tabs", "1.0",
                        "Removes all simple tabs from the specified or all tablist(s).");
        Registration.registerExpression(ExprDisplayNameOfSimpleTab.class, String.class, ExpressionType.PROPERTY,
                "[the] [display] name of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Display Name of Simple Tab", "1.0",
                        "An expression for the display name of the simple tab "
                        + "with the specified id in the specified or all tablist(s).");
        Registration.registerExpression(ExprLatencyBarsOfSimpleTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] (latency|ping) [bars] of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Latency Bars of Simple Tab", "1.0",
                        "An expression for the amount of latency bars of the simple tab "
                        + "with the specified id in the specified or all tablist(s). "
                        + "This is always between 0 and 5.");
        Registration.registerExpression(ExprIconOfSimpleTab.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (head|icon|skull) of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Icon of Simple Tab", "1.0",
                        "An expression for the icon of the simple tab "
                        + "with the specified id in the specified or all tablist(s).");
        Registration.registerExpression(ExprScoreOfSimpleTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] score of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Score of Simple Tab", "1.0",
                        "An expression for the score of the simple tab "
                        + "with the specified id in the specified or all tablist(s).");
    }
}
