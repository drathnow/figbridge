package zedi.pacbridge.app.events;

import java.io.Serializable;

public class EventQualifier implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final EventQualifier Connect = new EventQualifier("Connect");
    public static final EventQualifier ZIOS  = new EventQualifier("ZIOS");

    private String name;

    private EventQualifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EventQualifier eventQualifierForName(String name) {
        if (Connect.getName().equals(name))
            return Connect;
        if (ZIOS.getName().equals(name))
            return ZIOS;
        return null;
    }

}
