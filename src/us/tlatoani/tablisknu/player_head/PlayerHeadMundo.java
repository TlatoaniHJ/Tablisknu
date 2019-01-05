package us.tlatoani.tablisknu.player_head;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.inventory.ItemStack;
import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.registration.Registration;

public class PlayerHeadMundo {

    public static void load() {
        MundoPropertyExpression.registerPropertyExpression(ExprOwnerOfHead.class, String.class,
                "itemstack/block", "owner of (head|skull) %", "(head|skull) %'s owner")
                .document("Owner of Player Head", "1.0",
                        "An expression for the owner of the specified player head, as an item or placed. "
                        + "The owner only means the name that is shown when held, like \"Tlatoani's Head\", "
                        + "and doesn't affect the actual skin that the player head has.")
                .changer(Changer.ChangeMode.SET, String.class, "1.0",
                        "Sets the owner of this player head. "
                        + "If the specified item is not a player head, nothing will happen.");
        Registration.registerExpression(ExprHeadFromSkin.class, ItemStack.class, ExpressionType.PROPERTY,
                "(head|skull) from %skin% [with owner %-string%]")
                .document("Player Head from Skin", "1.0",
                        "An expression for a player head bearing the specified skin, "
                        + "optionally with the specified owner. "
                        + "If you do not specify an owner, the owner will appear to be \"MundoSK-Name\". "
                        + "This really only matters if anybody is going to actually have the player head in their inventory "
                        + "(i.e. if the player head is only going to be used as a block, the owner isn't important, "
                        + "though it may be useful as an identifier).");
    }
}
