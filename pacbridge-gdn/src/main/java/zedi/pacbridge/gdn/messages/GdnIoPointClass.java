package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.net.IoPointClass;
import zedi.pacbridge.utl.NamedType;


public class GdnIoPointClass extends NamedType implements IoPointClass, Serializable {
    private static final long serialVersionUID = 1001;
    
    public static final int VALUE_MASK = 0x3F;
    public static final int ATTRIBUTE_MASK = 0xC0;
    public static final int INTERNAL_ATTRIBUTE_VALUE = 0x80;

    public static final GdnIoPointClass System = new GdnIoPointClass("System",1);
    public static final GdnIoPointClass Rtu = new GdnIoPointClass("RTU",2);
    public static final GdnIoPointClass IoBoard = new GdnIoPointClass("IOBoard",3);
    public static final GdnIoPointClass Network = new GdnIoPointClass("Network",4);
    public static final GdnIoPointClass Application = new GdnIoPointClass("Application",5);
    

    private GdnIoPointClass(String name, int classNumber) {
        super(name, classNumber);
    }
    
    public boolean isInternal() {
        return this == IoBoard;
    }
    
    public static GdnIoPointClass ioPointClassForClassNumber(int typeNumber) {
        switch (typeNumber) {
            case 1 :
                return System;
                
            case 2 :
                return Rtu;
                
            case 3 :
                return IoBoard;
                
            case 4 :
                return Network;
                
            case 5 :
                return Application;
                
            default : return null;
        }
    }

    public static GdnIoPointClass ioPointClassForName(String name) {
        if (name.equals(System.getName()))
            return System;
        if (name.equals(Rtu.getName()))
            return Rtu;
        if (name.equals(IoBoard.getName()))
            return IoBoard;
        if (name.equals(Network.getName()))
            return Network;
        if (name.equals(Application.getName()))
            return Application;
        return null;
    }

    public static boolean isInternalDataTypeNumber(int dataTypeNumber) {
        return (dataTypeNumber & INTERNAL_ATTRIBUTE_VALUE) != 0;
    }
}
