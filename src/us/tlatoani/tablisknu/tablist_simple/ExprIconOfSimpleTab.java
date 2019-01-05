package us.tlatoani.tablisknu.tablist_simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.tablist.SimpleTablist;
import us.tlatoani.tablisknu.tablist.Tab;
import us.tlatoani.tablisknu.tablist.Tablist;
import us.tlatoani.tablisknu.tablist.TablistProvider;
import us.tlatoani.mundocore.util.OptionalUtil;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprIconOfSimpleTab extends SimpleExpression<Skin> {
    private Expression<String> id;
    private TablistProvider tablistProvider;

    @Override
    protected Skin[] get(Event event) {
        String id = this.id.getSingle(event);
        return tablistProvider
                .view(event)
                .map(tablist -> OptionalUtil
                        .cast(tablist.getSupplementaryTablist(), SimpleTablist.class)
                        .flatMap(simpleTablist -> simpleTablist.getTab(id))
                        .flatMap(Tab::getIcon)
                        .orElse(null))
                .toArray(Skin[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("icon of simple tab " + id + " [for %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String id = this.id.getSingle(event);
        Skin value = mode == Changer.ChangeMode.SET ? (Skin) delta[0] : null;
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.getTab(id).ifPresent(tab -> tab.setIcon(value));
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
