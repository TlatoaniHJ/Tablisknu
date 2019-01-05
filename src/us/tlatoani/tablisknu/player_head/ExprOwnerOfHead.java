package us.tlatoani.tablisknu.player_head;

import ch.njol.skript.classes.Changer;
import us.tlatoani.mundocore.property_expression.ChangeablePropertyExpression;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Tlatoani on 2/18/18.
 */
public class ExprOwnerOfHead extends ChangeablePropertyExpression<Object, String> {

    public static Optional<? extends HeadUtil> getSkullUtil(Object value) {
        if (value instanceof ItemStack) {
            return HeadUtil.from((ItemStack) value);
        } else if (value instanceof Block) {
            return HeadUtil.from((Block) value);
        }
        return Optional.empty();
    }

    @Override
    public void change(Object o, String s, Changer.ChangeMode changeMode) {
        getSkullUtil(o).ifPresent(skullUtil -> skullUtil.setOwner(s));
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public String convert(Object o) {
        return getSkullUtil(o).map(HeadUtil::getOwner).orElse(null);
    }
}
