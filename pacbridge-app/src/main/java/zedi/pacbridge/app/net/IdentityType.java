package zedi.pacbridge.app.net;

public class IdentityType {
    public static final IdentityType IpAddress = new IdentityType("ipAddress");
    public static final IdentityType SiteProvided = new IdentityType("siteProvided");
    
    private String name;

    private IdentityType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public static IdentityType identityTypeForString(String typeName) {
        if (IpAddress.getName().equalsIgnoreCase(typeName))
            return IpAddress;
        if (SiteProvided.getName().equalsIgnoreCase(typeName))
            return SiteProvided;
        throw new IllegalArgumentException("Unrecognized Identity type: '" + typeName + "'");
    }
}
