package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;

public class ObjectType extends NamedType implements Serializable {
    private static final int SITE_NUMBER = 1;
    private static final int DEVICE_NUMBER = 2;
    private static final int IO_POINT_NUMBER = 3;
    private static final int EVENT_NUMBER = 4;
    private static final int PORT_NUMBER = 5;
    
    public static final ObjectType SITE = new ObjectType("site", SITE_NUMBER);
    public static final ObjectType DEVICE = new ObjectType("device", DEVICE_NUMBER);
    public static final ObjectType IO_POINT = new ObjectType("ioPoint", IO_POINT_NUMBER);
    public static final ObjectType EVENT = new ObjectType("event", EVENT_NUMBER);
    public static final ObjectType PORT = new ObjectType("port", PORT_NUMBER);
    
    private ObjectType(String name, Integer number) {
        super(name, number);
    }
    
    public static final ObjectType objectTypeForNumber(Integer number) {
        switch (number) {
            case SITE_NUMBER : return SITE;
            case DEVICE_NUMBER : return DEVICE;
            case IO_POINT_NUMBER : return IO_POINT;
            case EVENT_NUMBER : return EVENT;
            case PORT_NUMBER : return PORT;
        }
        return null;
    }
    
    public static final ObjectType objectTypeForName(String name) {
        if (name.equalsIgnoreCase(SITE.getName()))
            return SITE;
        if (name.equalsIgnoreCase(DEVICE.getName()))
            return DEVICE;
        if (name.equalsIgnoreCase(IO_POINT.getName()))
            return IO_POINT;
        if (name.equalsIgnoreCase(EVENT.getName()))
            return EVENT;
        if (name.equalsIgnoreCase(PORT.getName()))
            return PORT;
        return null;
    }

}
