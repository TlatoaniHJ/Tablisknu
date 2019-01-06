package us.tlatoani.tablisknu.tablist_general;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.Skin;

/**
 * Created by Tlatoani on 3/30/18.
 */
public class TablistMundo {
    public static final String TABLIST_OWNER = "(%-players%|group %-string%)";
    public static final String FOR_TABLIST_OWNER = "[for " + TABLIST_OWNER + "]";
    public static final String OF_TABLIST_OWNER = "[of " + TABLIST_OWNER + "]";
    public static final String FOR_OF_TABLIST_OWNER = "[(for|of) " + TABLIST_OWNER + "]";
    public static final String TABLIST_OWNER_POSSESSIVE = "[" + TABLIST_OWNER + "'[s]]";

    public static void load() {
        Registration.registerEffect(EffEnableDisableScores.class,
                "(0¦enable|1¦disable) scores in [the] tablist[s] " + OF_TABLIST_OWNER,
                "(0¦enable|1¦disable) scores in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]")
                .document("Toggle Scores in Tablist", "1.0",
                        "Enables or disables scores in the specified or all tablist(s)."
                        + "This only applies to scores using MundoSK's tablist syntaxes.");
        Registration.registerExpression(ExprDefaultIcon.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (initial|default) (head|icon|player_head) (in|of) [the] tablist[s] " + OF_TABLIST_OWNER,
                "[the] (intial|default) (head|icon|player_head) (in|of) " + TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]")
                .document("Default Tablist Icon", "1.0",
                        "This is the icon initally applied when creating tabs in an array tablist "
                        + "or when creating a simple tab without specifying the icon. ")
                .changer(Changer.ChangeMode.SET, Skin.class, "1.0",
                        "Sets the default icon in the specified or all tablist(s). "
                        + "Any tabs that are using the default icon due to not having been set to use any other icon "
                        + "will have their icon changed to the new default icon "
                        + "(tabs that have been set to use an icon that happens to be the default icon will not be affected).");
        Registration.registerExpression(ExprHeaderFooter.class, String.class, ExpressionType.PROPERTY,
                "[the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]")
                .document("Tablist Header or Footer", "1.0",
                        "An expression for the header or footer of the specified or all tablist(s). "
                        + "This is a list expression as the header and footer can have multiple lines of text.");
        Registration.registerExpression(ExprLineOfHeaderFooter.class, String.class, ExpressionType.COMBINED,
                "(line %-number%|[the] last line) of [the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                "(line %-number%|[the] last line) of " + TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]")
                .document("Line of Tablist Header or Footer", "1.0",
                        "A particular line of the header or footer of the specified or all tablist(s).")
                .changer(Changer.ChangeMode.SET, String.class, "1.0",
                        "Sets the specified line. "
                        + "If necessary blank lines will be added above the specified line.");
        Registration.registerExpression(ExprHeightOfHeaderFooter.class, Number.class, ExpressionType.PROPERTY,
                "[the] height of [the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                "[the] height of " + TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]")
                .document("Height of Tablist Header or Footer", "1.0",
                        "The amount of lines in the header or footer of the specified or all tablist(s).");
        Registration.registerExpressionCondition(CondScoresEnabled.class, ExpressionType.PROPERTY,
                "scores [are] (0¦enabled|1¦disabled) in [the] tablist[s] " + OF_TABLIST_OWNER,
                "scores [are] (0¦enabled|1¦disabled) in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]")
                .document("Scores are Enabled", "1.0",
                        "Checks whether the specified or all tablist(s) have scores enabled. "
                        + "This only applies to enabling scores using MundoSK's tablist syntaxes.");
    }

}
