package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

public class EffColorBlueprint extends Effect {
    private Expression<Blueprint> blueprintExpression;
    private Expression<ARGBColor> colorExpression;

    @Override
    protected void execute(Event event) {
        Blueprint blueprint = blueprintExpression.getSingle(event);
        ARGBColor color = colorExpression.getSingle(event);
        if (blueprint == null || color == null) {
            return;
        }
        blueprint.colorAll(color);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "color " + blueprintExpression + " as " + colorExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        blueprintExpression = (Expression<Blueprint>) exprs[0];
        colorExpression = (Expression<ARGBColor>) exprs[1];
        return true;
    }
}
