package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

public class ExprRectangleOfBlueprint extends SimpleExpression<Blueprint> {
    private Expression<Blueprint> blueprintExpression;
    private Expression<Number> x1Expression;
    private Expression<Number> y1Expression;
    private Expression<Number> x2Expression;
    private Expression<Number> y2Expression;

    @Override
    protected Blueprint[] get(Event event) {
        Blueprint blueprint = blueprintExpression.getSingle(event);
        if (blueprint == null) {
            return new Blueprint[0];
        }
        int x1 = x1Expression.getSingle(event).intValue();
        int x2 = x2Expression.getSingle(event).intValue();
        int y1 = y1Expression.getSingle(event).intValue();
        int y2 = y2Expression.getSingle(event).intValue();
        return new Blueprint[]{blueprint.getRectangle(x1, y1, x2, y2)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Blueprint> getReturnType() {
        return Blueprint.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "pixels " + x1Expression + ", " + y1Expression + " to " + x2Expression + ", " + x2Expression
                + " of " + blueprintExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        x1Expression = (Expression<Number>) exprs[0];
        y1Expression = (Expression<Number>) exprs[1];
        x2Expression = (Expression<Number>) exprs[2];
        y2Expression = (Expression<Number>) exprs[3];
        blueprintExpression = (Expression<Blueprint>) exprs[4];
        return true;
    }
}
