package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

public class EffOverlayBlueprint extends Effect {
    private Expression<Blueprint> targetExpression;
    private Expression<Blueprint> overlayedExpression;

    @Override
    protected void execute(Event event) {
        Blueprint target = targetExpression.getSingle(event);
        Blueprint overlayed = overlayedExpression.getSingle(event);
        if (target == null || overlayed == null) {
            return;
        }
        target.overlay(overlayed);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "overlay " + overlayedExpression + " on " + targetExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        overlayedExpression = (Expression<Blueprint>) exprs[0];
        targetExpression = (Expression<Blueprint>) exprs[1];
        return true;
    }
}
