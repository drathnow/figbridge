package zedi.pacbridge.net;

public class TransportType {
    
    public static final TransportType TCP = new TransportType("TCP");
    public static final TransportType UDP = new TransportType("UDP");
    
    private String name;
    
    private TransportType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return getName();
    }
    
    public boolean isTcp() {
        return this == TCP;
    }
    
    public boolean isUdp() {
        return this == UDP;
    }
    
    public static TransportType transportTypeForName(String name) {
        if (name.equalsIgnoreCase("TCP"))
            return TCP;
        if (name.equalsIgnoreCase("UDP"))
            return UDP;
        throw new IllegalArgumentException("Unrecognized network type name");
    }
}
