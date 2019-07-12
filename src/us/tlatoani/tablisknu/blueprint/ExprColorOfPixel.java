package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;

public class ExprColorOfPixel extends SimpleExpression<ARGBColor> {
    private Expression<Blueprint> blueprintExpression;
    private Expression<Number> xExpression;
    private Expression<Number> yExpression;

    @Override
    protected ARGBColor[] get(Event event) {
        Blueprint blueprint = blueprintExpression.getSingle(event);
        if (blueprint == null) {
            return new ARGBColor[0];
        }
        int x = xExpression.getSingle(event).intValue();
        int y = yExpression.getSingle(event).intValue();
        return new ARGBColor[]{blueprint.getPixelColor(x, y)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ARGBColor> getReturnType() {
        return ARGBColor.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "color of pixel " + xExpression + ", " + yExpression + " of " + blueprintExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        xExpression = (Expression<Number>) exprs[0];
        yExpression = (Expression<Number>) exprs[1];
        blueprintExpression = (Expression<Blueprint>) exprs[2];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Blueprint blueprint = blueprintExpression.getSingle(event);
        if (blueprint == null) {
            return;
        }
        int x = xExpression.getSingle(event).intValue();
        int y = yExpression.getSingle(event).intValue();
        ARGBColor color = mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE
                ? ARGBColor.TRANSPARENT
                : (ARGBColor) delta[0];
        blueprint.colorPixel(x, y, color);
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case SET: return CollectionUtils.array(ARGBColor.class);
            case RESET:
            case DELETE: return CollectionUtils.array();
            default: return null;
        }
    }
}
