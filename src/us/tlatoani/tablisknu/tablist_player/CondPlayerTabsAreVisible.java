package us.tlatoani.tablisknu.tablist_player;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import us.tlatoani.tablisknu.tablist.PlayerTablist;
import us.tlatoani.tablisknu.tablist.SimpleTablist;
import us.tlatoani.tablisknu.tablist.Tablist;
import us.tlatoani.tablisknu.tablist.TablistProvider;
import org.bukkit.event.Event;
import us.tlatoani.tablisknu.tablist_general.TablistMundo;

/**
 * Created by Tlatoani on 8/11/16.
 */
public class CondPlayerTabsAreVisible extends SimpleExpression<Boolean> {
    private TablistProvider tablistProvider;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{tablistProvider.check(event, Tablist::arePlayersVisible, positive)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("player tabs are " + (positive ? "visible" : "hidden") + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        positive = parseResult.mark == 0;
        TablistMundo.printTablistSyntaxWarning("Hiding players in the tablist", null);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (delta[0] == null) {
            return;
        }
        Boolean visible = positive == (Boolean) delta[0];
        for (Tablist tablist : tablistProvider.get(event)) {
            if ((tablist.arePlayersVisible() != visible) && !tablist.getPlayerTablist().isPresent()) {
                tablist.setSupplementaryTablist(SimpleTablist::new);
            }
            tablist.getPlayerTablist().ifPresent(visible ? PlayerTablist::showAllPlayers : PlayerTablist::hideAllPlayers);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
