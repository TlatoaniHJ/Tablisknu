package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

public class EffRecolorBlueprint extends Effect {
    private Expression<Blueprint> blueprintExpression;
    private Expression<ARGBColor> beforeExpression;
    private Expression<ARGBColor> afterExpression;

    @Override
    protected void execute(Event event) {
        Blueprint blueprint = blueprintExpression.getSingle(event);
        ARGBColor before = beforeExpression.getSingle(event);
        ARGBColor after = afterExpression.getSingle(event);
        if (blueprint == null || before == null || after == null) {
            return;
        }
        blueprint.replaceColor(before, after);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "recolor " + beforeExpression + " in " + blueprintExpression + " with " + afterExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        beforeExpression = (Expression<ARGBColor>) exprs[0];
        blueprintExpression = (Expression<Blueprint>) exprs[1];
        afterExpression = (Expression<ARGBColor>) exprs[2];
        return true;
    }
}
