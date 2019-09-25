package zedi.pacbridge.utl;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.json.JSONObject;

public class NuidSiteAddress extends SiteAddress implements Serializable {
    private static final long serialVersionUID = 1001L;

    private String networkUnitId;
    private Long hashedValue;
    private InetSocketAddress remoteAddress;

    public NuidSiteAddress(String nuid) {
        this(nuid, 0, null);
    }

    public NuidSiteAddress(String nuid, Integer networkNumber, InetSocketAddress remoteAddress) {
        super(networkNumber);
        this.networkUnitId = nuid;
        this.remoteAddress = remoteAddress;
        this.hashedValue = Utilities.hash(nuid);
    }
    
    public NuidSiteAddress(String nuid, Integer networkNumber) {
        this(nuid, networkNumber, null);
    }

    @Override
    protected Long getComparableAddressValue() {
        return hashedValue;
    }
    
    @Override
    public String getAddress() {
        return networkUnitId;
    }
    
    @Override
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("networkNumber", networkNumber);
        jsonObject.put("address", networkUnitId.toString());
        return jsonObject.toString();
    }
    
    public String getNetworkUnitId() {
        return networkUnitId;
    }
    
    public String toString() {
        return networkUnitId + "/" + networkNumber;
    }

    public boolean equals(Object object) {
        if (object instanceof NuidSiteAddress) {
            NuidSiteAddress otherObject = (NuidSiteAddress)object;
            return (otherObject.networkNumber.intValue() == networkNumber.intValue() 
                    && otherObject.networkUnitId.equals(networkUnitId));
        }
        return false;
    }

    public int hashCode() {
        return networkUnitId.hashCode() + Integer.valueOf(networkNumber).hashCode();
    }
}
