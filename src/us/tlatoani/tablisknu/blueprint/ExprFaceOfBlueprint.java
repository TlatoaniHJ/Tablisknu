package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.util.OptionalUtil;

public class ExprFaceOfBlueprint extends SimpleExpression<Blueprint> {
    private Expression<Blueprint> blueprintExpression;
    private Face face;

    @Override
    protected Blueprint[] get(Event event) {
        return OptionalUtil
                .cast(blueprintExpression.getSingle(event), PartBlueprint.class)
                .map(blueprint -> new Blueprint[]{blueprint.getFace(face)})
                .orElseGet(() -> new Blueprint[0]);
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
        return face.name().toLowerCase().replace("_", " ")
                + " face pixels of " + blueprintExpression;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        blueprintExpression = (Expression<Blueprint>) exprs[0];
        face = Face.values()[parseResult.mark - 1];
        return true;
    }
}
