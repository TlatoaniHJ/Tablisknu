package us.tlatoani.tablisknu.tablist_general;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import us.tlatoani.tablisknu.tablist.Tablist;
import us.tlatoani.tablisknu.tablist.TablistProvider;
import org.bukkit.event.Event;

public class EffEnableDisableScores extends Effect {
    private TablistProvider tablistProvider;
    private boolean enable;

    @Override
    protected void execute(Event event) {
        for (Tablist tablist : tablistProvider.get(event)) {
            tablist.setScoresEnabled(enable);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString((enable ? "enable" : "disable") + " scores in tablist [of %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        enable = parseResult.mark == 0;
        return true;
    }
}
