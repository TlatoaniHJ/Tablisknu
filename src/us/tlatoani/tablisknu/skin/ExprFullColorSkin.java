package us.tlatoani.tablisknu.skin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprFullColorSkin extends SimpleExpression<Skin> {
    private String colorName;
    private Skin skin;

    @Override
    protected Skin[] get(Event event) {
        return new Skin[]{skin};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return colorName + " skin";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        colorName = FullColorSkins.nameByIndex(matchedPattern);
        skin = FullColorSkins.skinByIndex(matchedPattern);
        return true;
    }
}
