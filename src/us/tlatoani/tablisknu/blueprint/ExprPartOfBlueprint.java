package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Optional;

public class ExprPartOfBlueprint extends SimpleExpression<Blueprint> {
    private Expression<Blueprint> blueprintExpression;
    private Part part;
    private boolean isSecondLayer;
    private Optional<Face> face;

    @Override
    protected Blueprint[] get(Event event) {
        Blueprint blueprint = blueprintExpression.getSingle(event);
        if (blueprint == null || !blueprint.isFullSize()) {
            return new Blueprint[0];
        }
        return new Blueprint[]{
                face
                        .map(f -> (Blueprint) blueprint.getPart(part, isSecondLayer).getFace(f))
                        .orElseGet(() -> blueprint.getPart(part, isSecondLayer))};
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
        return ((isSecondLayer ? "second layer " : "") + part + face.map(f -> f + " face ").orElse(""))
                    .toLowerCase().replace("_", " ")
                + " pixels of " + blueprintExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        blueprintExpression = (Expression<Blueprint>) exprs[0];
        int mark = parseResult.mark;
        face = (mark & 7) == 0 ? Optional.empty() : Optional.of(Face.values()[(mark & 7) - 1]);
        mark >>= 3;
        isSecondLayer = (mark & 1) == 1;
        mark >>= 1;
        part = Part.values()[mark - 1];
        return true;
    }
}
