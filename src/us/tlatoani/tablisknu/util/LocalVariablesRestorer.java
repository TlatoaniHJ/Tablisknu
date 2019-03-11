package us.tlatoani.tablisknu.util;

import ch.njol.skript.variables.Variables;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.reflection.Reflection;

public class LocalVariablesRestorer {
    public final Event event;
    private Object localVariables = null;

    public static final Reflection.MethodInvoker VARIABLES_REMOVE_LOCALS;
    public static final Reflection.MethodInvoker VARIABLES_SET_LOCAL_VARIABLES;

    static {
        if (Reflection.methodExists(Variables.class, "removeLocals", Event.class)) {
            VARIABLES_REMOVE_LOCALS =
                    Reflection.getMethod(Variables.class, "removeLocals", Event.class);
            VARIABLES_SET_LOCAL_VARIABLES =
                    Reflection.getMethod(Variables.class, "setLocalVariables", Event.class, Object.class);
        } else {
            VARIABLES_REMOVE_LOCALS = null;
            VARIABLES_SET_LOCAL_VARIABLES = null;
        }
    }

    public LocalVariablesRestorer(Event event) {
        this.event = event;
    }

    public void removeVariables() {
        if (VARIABLES_REMOVE_LOCALS != null) {
            localVariables = VARIABLES_REMOVE_LOCALS.invoke(null, event);
        }
    }

    public void restoreVariables() {
        if (VARIABLES_REMOVE_LOCALS != null) {
            VARIABLES_SET_LOCAL_VARIABLES.invoke(null, event, localVariables);
        }
    }
}
