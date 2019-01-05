package us.tlatoani.tablisknu.tablist_simple;

import ch.njol.skript.lang.ExpressionType;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.tablist_general.TablistMundo;

public class SimpleTablistMundo {

    public static void load() {
        Registration.registerExpression(ExprPriorityOfSimpleTab.class, String.class, ExpressionType.PROPERTY,
                "[the] priority of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER);

        Registration.registerEffect(EffCreateSimpleTab.class,
                "create [a] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER + " [with "
                        + "[priority %-string%] "
                        + "[[display] name %-string%] "
                        + "[(ping|latency) [bars] %-number%] "
                        + "[(head|icon|player_head) %-skin%] "
                        + "[score %-number%]]")
                .document("Create Simple Tab", "1.0",
                        "Creates a simple tab for the specified player(s) with the specified id and properties. "
                        + "If a specified player already has a simple tab with the specified id in their tablist, "
                        + "that tab will be deleted before creating the new tab. "
                        + "This effect will not work for a specified player if they have the array tablist enabled.");
        Registration.registerEffect(EffDeleteSimpleTab.class,
                "delete [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Delete Simple Tab", "1.0",
                        "Removes the simple tab "
                        + "with the specified id from the tablist(s) of the specified player(s).");
        Registration.registerEffect(EffDeleteAllSimpleTabs.class,
                "delete all simple tabs " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Delete All Simple Tabs", "1.0",
                        "Removes all simple tabs from the tablist(s) of the specified players(s).");
        Registration.registerExpression(ExprDisplayNameOfSimpleTab.class, String.class, ExpressionType.PROPERTY,
                "[the] [display] name of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Display Name of Simple Tab", "1.0",
                        "An expression for the display name of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s).");
        Registration.registerExpression(ExprLatencyBarsOfSimpleTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] (latency|ping) [bars] of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Latency Bars of Simple Tab", "1.0",
                        "An expression for the amount of latency bars of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s). "
                        + "This is always between 0 and 5.");
        Registration.registerExpression(ExprIconOfSimpleTab.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (head|icon|player_head) of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Icon of Simple Tab", "1.0",
                        "An expression for the icon of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s).");
        Registration.registerExpression(ExprScoreOfSimpleTab.class, Number.class, ExpressionType.PROPERTY,
                "[the] score of [the] simple tab %string% " + TablistMundo.FOR_TABLIST_OWNER)
                .document("Score of Simple Tab", "1.0",
                        "An expression for the score of the simple tab "
                        + "with the specified id in the tablist(s) of the specified player(s).");
    }
}
