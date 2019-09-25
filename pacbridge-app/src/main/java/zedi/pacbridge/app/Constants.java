package zedi.pacbridge.app;

import java.text.MessageFormat;

import org.jboss.msc.service.ServiceName;

public class Constants {
    public static String BRIDGE_NAME_FMT = "{0}.{1}";
    
    public static final String CLUSTER_NAME = "fig-bridge-cluster";
    public static final ServiceName FIGBRIDGE;
    public static final String BRIDGE_NAME;

    static {
        BRIDGE_NAME = MessageFormat.format(BRIDGE_NAME_FMT, 
                                System.getProperty("jboss.host.name", "TheHost"),
                                System.getProperty("jboss.server.name", "theBridge"));
        FIGBRIDGE = ServiceName.of("figbridge");
    }
}
