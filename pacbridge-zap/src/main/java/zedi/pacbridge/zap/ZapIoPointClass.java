package zedi.pacbridge.zap;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class ZapIoPointClass extends NamedType implements Serializable {
    private static final long serialVersionUID = 1001;

    private static final int UNKNOWN_NUMBER = 0;
    private static final int RTU_NUMBER = 2;
    private static final int NETWORK_NUMBER = 4;
    private static final int MEMORY_NUMBER = 6;
    private static final int CONFIG_NUMBER = 7;
    private static final int INTERNAL_NUMBER = 3;

    private static final String UNKNOWN_NAME = "Unknown";
    private static final String RTU_NAME = "RTU";
    private static final String NETWORK_NAME = "Network";
    private static final String MEMORY_NAME = "Memory";
    private static final String CONFIG_NAME = "Config";
    private static final String INTERNAL_NAME = "Internal";

    public static final ZapIoPointClass Unknow = new ZapIoPointClass(UNKNOWN_NAME, UNKNOWN_NUMBER);
    public static final ZapIoPointClass RTU = new ZapIoPointClass(RTU_NAME, RTU_NUMBER);
    public static final ZapIoPointClass Network = new ZapIoPointClass(NETWORK_NAME, NETWORK_NUMBER);
    public static final ZapIoPointClass Memory = new ZapIoPointClass(MEMORY_NAME, MEMORY_NUMBER);
    public static final ZapIoPointClass Config = new ZapIoPointClass(CONFIG_NAME, CONFIG_NUMBER);
    public static final ZapIoPointClass Internal = new ZapIoPointClass(INTERNAL_NAME, INTERNAL_NUMBER);

    private ZapIoPointClass(String name, Integer number) {
        super(name, number);
    }

    public static ZapIoPointClass ioPointClassForClassNumber(int typeNumber) {
        switch (typeNumber) {
            case RTU_NUMBER :
                return RTU;
            case NETWORK_NUMBER :
                return Network;
            case MEMORY_NUMBER :
                return Memory;
            case CONFIG_NUMBER :
                return Config;
            case INTERNAL_NUMBER :
                return Internal;
            default :
                return Unknow;
        }
    }

    public static ZapIoPointClass ioPointClassForName(String name) {
        if (name.equalsIgnoreCase(RTU.getName()))
            return RTU;
        if (name.equalsIgnoreCase(Network.getName()))
            return Network;
        if (name.equalsIgnoreCase(Network.getName()))
            return Network;
        if (name.equalsIgnoreCase(Config.getName()))
            return Config;
        if (name.equalsIgnoreCase(Internal.getName()))
            return Internal;
        return Unknow;
    }
}
