package us.tlatoani.tablisknu.skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.registration.Registration;
import us.tlatoani.tablisknu.skin.retrieval.EffRetrieveSkin;

import java.io.StreamCorruptedException;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class SkinMundo {
    
    public static void load() {
        Registration.registerType(Skin.class, "skin")
                .document("Skin Texture", "1.0", "Represents a skin, possibly of a player. Write 'steve' or 'alex' for these respective skins.")
                .example("skin with name \"eyJ0aW1lc3RhbXAiOjE0NzQyMTc3NjkwMDAsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE5MmI0NTY2ZjlhMjg2OTNlNGMyNGFiMTQxNzJjZDM0MjdiNzJiZGE4ZjM0ZDRhNjEwODM3YTQ3ZGEwZGUifX19\" signature \"pRQbSEnKkNmi0uW7r8H4xzoWS3E4tkWNbiwwRYgmvITr0xHWSKii69TcaYDoDBXGBwZ525Ex5z5lYe5Xg6zb7pyBPiTJj8J0QdKenQefVnm6Vi1SAR1uN131sRddgK2Gpb2z0ffsR9USDjJAPQtQwCqz0M7sHeXUJhuRxnbznpuZwGq+B34f1TqyVH8rcOSQW9zd+RY/MEUuIHxmSRZlfFIwYVtMCEmv4SbhjLNIooGp3z0CWqDhA7GlJcDFb64FlsJyxrAGnAsUwL2ocoikyIQceyj+TVyGIEuMIpdEifO6+NkCnV7v+zTmcutOfA7kHlj4d1e5ylwi3/3k4VKZhINyFRE8M8gnLgbVxNZ4mNtI3ZMWmtmBnl9dVujyo+5g+vceIj5Admq6TOE0hy7XoDVifLWyNwO/kSlXl34ZDq1MCVN9f1ryj4aN7BB8/Tb2M4sJf3YoGi0co0Hz/A4y14M5JriG21lngw/vi5Pg90GFz64ASssWDN9gwuf5xPLUHvADGo0Bue8KPZPyI0iuIi/3sZCQrMcdyVcur+facIObTQhMut71h8xFeU05yFkQUOKIQswaz2fpPb/cEypWoSCeQV8T0w0e3YKLi4RaWWvKS1MFJDHn7xMYaTk0OhALJoV5BxRD8vJeRi5jYf3DjEgt9+xB742HrbVRDlJuTp4=\"")
                .example("player's skin")
                .example("alex")
                .example("steve")
                .parser(new Registration.SimpleParser<Skin>() {
            @Override
            public Skin parse(String s, ParseContext parseContext) {
                if (s.equalsIgnoreCase("STEVE")) {
                    return Skin.STEVE;
                } else if (s.equalsIgnoreCase("ALEX")) {
                    return Skin.ALEX;
                } else {
                    return null;
                }
            }
        }).serializer(new Serializer<Skin>() {
            @Override
            public Fields serialize(Skin skin) {
                Fields fields = new Fields();
                fields.putObject("value", skin.value);
                fields.putObject("signature", skin.signature);
                fields.putObject("uuid", skin.uuid.toString());
                return fields;
            }

            @Override
            public void deserialize(Skin skin, Fields fields) {
                throw new UnsupportedOperationException("Skin does not have a nullary constructor!");
            }

            @Override
            public Skin deserialize(Fields fields) throws StreamCorruptedException {
                try {
                    String value = (String) fields.getObject("value");
                    String signature = (String) fields.getObject("signature");
                    String uuid = fields.contains("uuid") ? (String) fields.getObject("uuid") : null;
                    Logging.debug(SkinMundo.class, "value: " + value + ", signature: " + signature + ", uuid: " + uuid);
                    if (uuid == null) {
                        return new Skin(value, signature);
                    } else {
                        return new Skin(value, signature, UUID.fromString(uuid));
                    }
                } catch (StreamCorruptedException | ClassCastException e) {
                    try {
                        String value = (String) fields.getObject("value");
                        Logging.debug(SkinMundo.class, "value: " + value);
                        Object parsedObject = new JSONParser().parse(value);
                        Logging.debug(SkinMundo.class, "parsedobject: " + parsedObject);
                        JSONObject jsonObject;
                        if (parsedObject instanceof JSONObject) {
                            jsonObject = (JSONObject) parsedObject;
                        } else {
                            jsonObject = (JSONObject) ((JSONArray) parsedObject).get(0);
                        }
                        return Skin.fromJSON(jsonObject);
                    } catch (ParseException | ClassCastException e1) {
                        throw new StreamCorruptedException();
                    }
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            public boolean canBeInstantiated(Class<? extends Skin> c) {
                return false;
            }

            protected boolean canBeInstantiated() {
                return false;
            }
        });
        Logging.debug(SkinMundo.class, "ClassInfo object for Skin.class: " + Classes.getExactClassInfo(Skin.class));
        Registration.registerEffect(EffRetrieveSkin.class,
                "retrieve [(4¦slim)] skin from (0¦file|1¦url) %-string% [[with] timeout %-timespan%] into %object%",
                "retrieve skin (2¦from uuid %-string%|3¦of %-offlineplayer%) [[with] timeout %-timespan%] into %object%")
                .document("Retrieve Skin", "1.0",
                        "Retrieves a certain skin from online and places it into the specified variable "
                        + "(the object expression at the end). "
                        + "The retrieved skin is either a skin made from the specified image file, "
                        + "a skin from the specified URL, "
                        + "or the skin of either the specified offline player or the offline player with the specified uuid. "
                        + "If you are retrieving a skin made from an image file or from a URL, "
                        + "you can specify it as slim, meaning it would have the smaller arm size like the default Alex skin does, "
                        + "as opposed to the larger arms of the default Steve skin. "
                        + "Note that since this effect has to retrieve data from online APIs, "
                        + "it is a delayed effect and all code following the effect will also be delayed. "
                        + "Tablisknu uses the Mineskin API for retrieving skins based on image files and URLs.");
        Registration.registerExpression(ExprFullColorSkin.class, Skin.class, ExpressionType.PROPERTY,
                Stream
                        .concat(Stream.of("%color%"), FullColorSkins.OTHER.keySet().stream())
                        .map(name -> name.toLowerCase() + " skin")
                        .toArray(String[]::new))
                .document("Full Color Skin", "1.0",
                        "An expression for a full color skin, "
                        + "either of the specified color or of one of the explicit color options available.")
                .example("set player's displayed skin to green skin")
                .example("enable array tablist"
                        , "set icon of tab 1, 1 to blue skin");
        Registration.registerExpression(ExprAllFullColorSkins.class, Skin.class, ExpressionType.SIMPLE,
                "all full color skins")
                .document("All Full Color Skins", "1.0",
                        "A list expression for all the full color skins "
                        + "that are available using the Full Color Skin expression. "
                        + "Useful for giving your tabs a bit of color variety without having to specify specific colors.");
        Registration.registerExpression(ExprSkinWith.class, Skin.class, ExpressionType.PROPERTY,
                "skin [texture] (with|of) value %string% signature %string%")
                .document("Skin with Value", "1.0",
                        "An expression for a skin with the specified value and signature.");
        Registration.registerExpression(ExprSkinOf.class, Skin.class, ExpressionType.PROPERTY,
                "skin [texture] of %player/itemstack/block%", "%player/itemstack/block%'s skin")
                .document("Skin of Player or Skull", "1.0",
                        "An expression for the skin of the specified player (must be online), "
                        + "player head item, or placed player head block.")
                .changer(Changer.ChangeMode.SET, Skin.class, "1.0",
                        "Only allowed for setting the skin of a player head (item or block).");
        Registration.registerExpression(ExprDisplayedSkinOfPlayer.class, Skin.class, ExpressionType.PROPERTY,
                "[(1¦default)] displayed skin of %player% [(for %-players%|excluding %-players%)]",
                "%player%'s [(1¦default)] displayed skin [(for %-players%|excluding %-players%)]")
                .document("Displayed Skin of Player", "1.0",
                        "An expression for the skin currently being displayed as the specified player's skin. "
                        + "If target ('for') players are specified, "
                        + "the expression will return a skin for each target player specified. "
                        + "Excluded players are meant to be specified only when setting the expression "
                        + "(for example, to prevent the original specified player from seeing a change). "
                        + "If the expression is evaluated with excluded players specified, "
                        + "it will act the same as if no target or excluded players had been specified.")
                .changer(Changer.ChangeMode.SET, Skin.class, "1.0",
                        "Changes the displayed skin of the specified player. "
                        + "The behavior of the change differs depending on what is specified in the syntax. "
                        + "If none of the extra syntax options are specified, "
                        + "the player's default nametag will be changed, and all players will see the new nametag "
                        + "(any specific skins assigned for the specified player will be removed). "
                        + "Specifying 'default' means that only the specified player's default displayed skin will be changed, "
                        + "meaning that only the players who do not have a specific skin "
                        + "assigned for the specified player will see the new nametag. "
                        + "Specifying target players means that the displayed skin will be changed for those target players, "
                        + "and will become their specific skin assigned for the specified player. "
                        + "Specifying excluded players means that excluded players "
                        + "who do not currently have a specific skin for the specified player "
                        + "will have the default displayed skin for that player set as the specific skin, "
                        + "and then after that the effect will be the same as changing the default displayed skin. ")
                .changer(Changer.ChangeMode.RESET, "1.0",
                        "If target players or excluded players are specified, "
                        + "this will remove any specified skin of either the target players "
                        + "or all non-exluded players assigned for the specified player, "
                        + "and revert to the default skin for the specified player. "
                        + "If no target players are specified, this will be identical to doing "
                        + "'set <expression> to <specified player>'s skin', "
                        + "with that behavior depending on whether 'default' is specified.")
                .changer(Changer.ChangeMode.DELETE, "1.0", "Same as reset.")
                .example("set player's default displayed skin to alex #All players now see the skin as alex"
                        , "set player's displayed skin to steve for {_p1} #{_p1} now sees the skin as steve"
                        , "set player's default displayed skin to {_p2}'s skin #All players except for {_p1} now see the nametag as {_p2}'s skin"
                        , "set player's displayed skin to {_p3}'s skin #All players (including {_p1}) now see the skin as {_p3}'s skin")
                .example("set player's default displayed skin to steve #All players now see the skin as steve"
                        , "set player's displayed skin excluding {_p1} to alex #All players except for {_p1} now see the skin as alex"
                        , "reset player's default displayed skin #All players except for {_p1} now see the skin as the player's actual skin"
                        , "set player's default displayed skin to {_p3}'s skin #All players except for {_p1} now see the skin as {_p3}'s skin"
                        , "reset player's displayed skin for {_p1} #{_p1} now sees the skin as {_p3}'s skin"
                        , "set player's displayed skin to {_p4}'s skin for {_p1} #{_p1} now sees the skin as {_p4}'s skin"
                        , "reset player's displayed skin #All players (including {_p1}) now see the skin as the player's actual skin");
        Registration.registerExpression(ExprNameTagOfPlayer.class, String.class, ExpressionType.PROPERTY,
                "[mundo[sk]] %player%'s [(1¦default)] name[]tag [for %-players%]",
                "[mundo[sk]] [(1¦default)] name[]tag of %player% [for %-players%]")
                .document("Nametag of Player", "1.0",
                        "An expression for the nametag "
                        + "(the name that appears above a player's head) of the specified player. "
                        + "If target ('for') players are specified, "
                        + "the expression will return a nametag for each target player specified. ")
                .changer(Changer.ChangeMode.SET, String.class, "1.0",
                        "Changes the nametag of the specified player. "
                        + "The behavior of the change differs depending on what is specified in the syntax. "
                        + "If none of the extra syntax options are specified, "
                        + "the player's default nametag will be changed, and all players will see the new nametag. "
                        + "Specifying 'default' means that only the specified player's default nametag will be changed, "
                        + "meaning only the players who do not have a specific nametag "
                        + "assigned for the specified player will see the new nametag. "
                        + "Specifying target players means that the nametag will be changed for those target players, "
                        + "and will become their specific nametag assigned for the specified player.")
                .changer(Changer.ChangeMode.RESET, "1.0",
                        "If target players are specified, "
                        + "this will remove any specified nametag assigned for the specified player, "
                        + "and revert to the default nametag for the specified player. "
                        + "If no target players are specified, "
                        + "this will be identical to doing 'set <expression> to <specified player>'s name', "
                        + "with that behavior depending on whether 'default' is specified.")
                .changer(Changer.ChangeMode.DELETE, "1.0", "Same as reset.")
                .example("set player's default nametag to \"bob\" #All players now see the nametag as bob"
                        , "set player's nametag to \"potter\" for {_p1} #{_p1} now sees the nametag as potter"
                        , "set player's default nametag to \"weird\" #All players except for {_p1} now see the nametag as weird"
                        , "set player's nametag to \"nonweird\" #All players (including {_p1}) now see the nametag as nonweird")
                .example("set player's default nametag to \"diamond\" #All players now see the nametag as diamond"
                        , "set player's nametag to \"emerald\" for {_p1} #{_p1} now sees the nametag as emerald"
                        , "reset player's default nametag #All players except for {_p1} now see the nametag as the player's actual name"
                        , "set player's default nametag to \"gold\" #All players except for {_p1} now see the nametag as gold"
                        , "reset player's nametag for {_p1} #{_p1} now sees the nametag as gold"
                        , "set player's default nametag to \"iron\" for {_p1} #{_p1} now sees the nametag as iron"
                        , "reset player's nametag #All players (including {_p1}) now see the nametag as the player's actual name");
    }
}
