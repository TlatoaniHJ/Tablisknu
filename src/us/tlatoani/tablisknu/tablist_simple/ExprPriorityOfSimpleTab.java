package us.tlatoani.tablisknu.tablist_simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.util.OptionalUtil;
import us.tlatoani.tablisknu.tablist.SimpleTab;
import us.tlatoani.tablisknu.tablist.SimpleTablist;
import us.tlatoani.tablisknu.tablist.Tablist;
import us.tlatoani.tablisknu.tablist.TablistProvider;

public class ExprPriorityOfSimpleTab extends SimpleExpression<String> {
    private Expression<String> id;
    private TablistProvider tablistProvider;

    @Override
    protected String[] get(Event event) {
        String id = this.id.getSingle(event);
        return tablistProvider
                .view(event)
                .map(tablist -> OptionalUtil
                        .cast(tablist.getSupplementaryTablist(), SimpleTablist.class)
                        .flatMap(simpleTablist -> simpleTablist.getTab(id))
                        .flatMap(SimpleTab::getPriority)
                        .orElse(null))
                .toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("priority of simple tab " + id + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String id = this.id.getSingle(event);
        String value = mode == Changer.ChangeMode.RESET ? null : (String) delta[0];
        if (value != null) {
            if (value.length() > 12) {
                value = value.substring(0, 12);
            }
            if (value.endsWith(" ")) {
                int i;
                for (i = value.length(); value.charAt(i - 1) == ' '; i--);
                value = value.substring(0, i);
            }
        }
        String preparedValue = value;
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.getTab(id).ifPresent(tab -> tab.setPriority(preparedValue));
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
