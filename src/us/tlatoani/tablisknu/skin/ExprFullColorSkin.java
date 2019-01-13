package us.tlatoani.tablisknu.skin;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprFullColorSkin extends SimpleExpression<Skin> {
    private Expression<Color> colorExpression;
    private String colorName;
    private Skin skin;
    private boolean isSkriptColor;

    @Override
    protected Skin[] get(Event event) {
        if (isSkriptColor) {
            Color color = colorExpression.getSingle(event);
            if (color == null) {
                return new Skin[0];
            } else {
                return new Skin[]{FullColorSkins.of(color)};
            }
        } else {
            return new Skin[]{skin};
        }
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
        return (isSkriptColor ? colorExpression : colorName) + " skin";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        isSkriptColor = matchedPattern == 0;
        if (isSkriptColor) {
            colorExpression = (Expression<Color>) exprs[0];
        } else {
            colorName = FullColorSkins.otherName(matchedPattern - 1);
            skin = FullColorSkins.other(matchedPattern - 1);
        }
        return true;
    }
}
