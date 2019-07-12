package us.tlatoani.tablisknu.blueprint;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import us.tlatoani.tablisknu.skin.Skin;
import us.tlatoani.tablisknu.skin.retrieval.SkinFormat;
import us.tlatoani.tablisknu.util.LocalVariablesRestorer;

import java.util.Optional;
import java.util.function.Consumer;

public class EffCreateBlueprint extends Effect {
    private Expression<Skin> skinExpression;
    private Expression<String> pathExpression;
    private Optional<Part> part;
    private Optional<Face> face;
    private SkinFormat format;
    private Mode mode;
    private Variable<?> variable;

    public enum Mode {
        SKIN,
        FILE,
        URL
    }

    @Override
    protected TriggerItem walk(Event event) {
        LocalVariablesRestorer localVariablesRestorer = new LocalVariablesRestorer(event);
        Consumer<Blueprint> callable = blueprint -> afterRetrieval(event, blueprint, localVariablesRestorer);
        if (mode == Mode.SKIN) {
            Skin skin = skinExpression.getSingle(event);
            if (skin == null) {
                return super.walk(event);
            }
            localVariablesRestorer.removeVariables();
            BlueprintLoader.loadOfSkin(skin, callable);
        } else {
            String path = pathExpression.getSingle(event);
            if (path == null) {
                return super.walk(event);
            }
            localVariablesRestorer.removeVariables();
            if (mode == Mode.FILE) {
                BlueprintLoader.loadFromFile(path, format, callable);
            } else {
                BlueprintLoader.loadFromURL(path, format, callable);
            }
        }
        return null;
    }

    private void afterRetrieval(Event event, Blueprint blueprint, LocalVariablesRestorer localVariablesRestorer) {
        localVariablesRestorer.restoreVariables();
        Blueprint res;
        if (!part.isPresent()) {
            if (mode == Mode.SKIN && !blueprint.isFullSize()) {
                res = new Blueprint(blueprint.format);
                res.overlay(blueprint);
                PartBlueprint[] rightParts = new PartBlueprint[]{res.getPart(Part.RIGHT_ARM, false), res.getPart(Part.RIGHT_LEG, false)};
                PartBlueprint[] leftParts = new PartBlueprint[]{res.getPart(Part.LEFT_ARM, false), res.getPart(Part.LEFT_LEG, false)};
                for (int i = 0; i < 2; i++) {
                    for (Face face : Face.values()) {
                        Face other;
                        if (face == Face.RIGHT) {
                            other = Face.LEFT;
                        } else if (face == Face.LEFT) {
                            other = Face.RIGHT;
                        } else {
                            other = face;
                        }
                        leftParts[i].getFace(face).overlay(rightParts[i].getFace(other));
                    }
                }
            } else {
                res = blueprint;
            }
        } else {
            res = face
                    .<Blueprint>map(f -> new FaceBlueprint(part.get(), false, f, format))
                    .orElseGet(() -> new PartBlueprint(part.get(), false, format));
            res.overlay(blueprint);
        }

        variable.change(event, new Blueprint[]{res}, Changer.ChangeMode.SET);
        TriggerItem.walk(getNext(), event);
    }

    @Override
    protected void execute(Event e) {}

    @Override
    public String toString(Event e, boolean debug) {
        switch (mode) {
            case SKIN: return "create blueprint of " + skinExpression + " in " + variable;
            case FILE:
            case URL: return "create "
                            + (format == SkinFormat.STEVE ? "" : "slim ")
                            + (part.map(p -> p + " ").orElse("")
                            + face.map(f -> f + " face ").orElse("")
                                ).toLowerCase().replace("_", " ")
                            + "blueprint from " + (mode == Mode.FILE ? "file" : "url")
                            + " " + pathExpression
                            + " in " + variable;
        }
        throw new IllegalStateException("Illegal mode = " + mode);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        mode = matchedPattern == 0 ? Mode.SKIN : ((parseResult.mark & 8) == 0 ? Mode.FILE : Mode.URL);
        if (mode == Mode.SKIN) {
            skinExpression = (Expression<Skin>) exprs[0];
            part = Optional.empty();
            face = Optional.empty();
        } else {
            pathExpression = (Expression<String>) exprs[0];
            int mark = parseResult.mark;
            face = (mark & 7) == 0 ? Optional.empty() : Optional.of(Face.values()[(mark & 7) - 1]);
            mark >>= 4;
            part = (mark & 7) == 0 ? Optional.empty() : Optional.of(Part.values()[(mark & 7) - 1]);
            mark >>= 3;
            format = SkinFormat.values()[mark];
        }
        variable = (Variable<?>) exprs[1];
        return true;
    }
}
