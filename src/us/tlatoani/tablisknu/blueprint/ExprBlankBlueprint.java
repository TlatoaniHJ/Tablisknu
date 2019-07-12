package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;

import java.util.Optional;

public class ExprBlankBlueprint extends SimpleExpression<Blueprint> {
    private Optional<Part> part;
    private Optional<Face> face;
    private SkinFormat format;
    private boolean isSecondLayer;

    public Blueprint get() {
        return part
                .map(part1 -> face
                    .<Blueprint>map(face1 -> new FaceBlueprint(part1, isSecondLayer, face1, format))
                    .orElseGet(() -> new PartBlueprint(part1, isSecondLayer, format)))
                .orElseGet(() -> new Blueprint(format));
    }

    @Override
    protected Blueprint[] get(Event e) {
        return new Blueprint[]{get()};
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
        return "blank "
                + (format == SkinFormat.STEVE ? "" : "slim ")
                + face.map(f -> f + " of ").orElse("")
                + (isSecondLayer ? "second layer " : "")
                + part.map(p -> p + " ").orElse("")
                + "blueprint";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        int mark = parseResult.mark;
        face = (mark & 7) == 0 ? Optional.empty() : Optional.of(Face.values()[(mark & 7) - 1]);
        mark >>= 4;
        part = (mark & 7) == 0 ? Optional.empty() : Optional.of(Part.values()[(mark & 7) - 1]);
        mark >>= 3;
        format = SkinFormat.values()[mark];
        return true;
    }
}
