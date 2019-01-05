package us.tlatoani.tablisknu.tablist_general;

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
                "(0¦enable|1¦disable) scores in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]");
        Registration.registerExpression(ExprDefaultIcon.class, Skin.class, ExpressionType.PROPERTY,
                "[the] (initial|default) (head|icon|player_head) (in|of) [the] tablist[s] " + OF_TABLIST_OWNER,
                "[the] (intial|default) (head|icon|player_head) (in|of) " + TABLIST_OWNER_POSSESSIVE + " [array] tablist[s]");
        Registration.registerExpression(ExprHeaderFooter.class, String.class, ExpressionType.PROPERTY,
                "[the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]")
                .document("Tablist Header or Footer", "1.0",
                        "An expression for the header or footer of the tablist(s) of the specified player(s). "
                        + "This is a list expression as the header and footer can have multiple lines of text.");
        Registration.registerExpression(ExprLineOfHeaderFooter.class, String.class, ExpressionType.COMBINED,
                "(line %-number%|[the] last line) of [the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                "(line %-number%|[the] last line) of " + TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]");
        Registration.registerExpression(ExprHeightOfHeaderFooter.class, Number.class, ExpressionType.PROPERTY,
                "[the] height of [the] tablist (0¦header|1¦footer)[s] " + FOR_OF_TABLIST_OWNER,
                "[the] height of " + TABLIST_OWNER_POSSESSIVE + " tablist (0¦header|1¦footer)[s]");
        Registration.registerExpressionCondition(CondScoresEnabled.class, ExpressionType.PROPERTY,
                "scores [are] (0¦enabled|1¦disabled) in [the] tablist[s] " + OF_TABLIST_OWNER,
                "scores [are] (0¦enabled|1¦disabled) in " + TABLIST_OWNER_POSSESSIVE + " tablist[s]")
                .document("Scores are Enabled", "1.0",
                        "Checks whether the tablist(s) of the specified player(s) have scores enabled. "
                        + "This only applies to enabling scores using MundoSK's tablist syntaxes.");
    }

}
