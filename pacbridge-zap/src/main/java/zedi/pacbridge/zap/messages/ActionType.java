package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class ActionType extends NamedType implements Serializable {
    private static final int DELETE_NUMBER = 1;
    private static final int ADD_NUMBER = 2;
    private static final int UPDATE_NUMBER = 3;

    public static final ActionType DELETE = new ActionType("delete", DELETE_NUMBER);
    public static final ActionType ADD = new ActionType("add", ADD_NUMBER);
    public static final ActionType UPDATE = new ActionType("update", UPDATE_NUMBER);

    private ActionType(String name, Integer number) {
        super(name, number);
    }

    public static final ActionType actionTypeForNumber(Integer number) {
        switch (number) {
            case DELETE_NUMBER :
                return DELETE;
            case ADD_NUMBER :
                return ADD;
            case UPDATE_NUMBER :
                return UPDATE;
        }
        throw new IllegalArgumentException("Invalid action number specified: " + number);
    }

    public static final ActionType actionTypeForName(String name) {
        if (name.equals(DELETE.getName()))
            return DELETE;
        if (name.equalsIgnoreCase(ADD.getName()))
            return ADD;
        if (name.equalsIgnoreCase(UPDATE.getName()))
            return UPDATE;
        throw new IllegalArgumentException("Invalid action name specified: " + name);
    }
}
