package zedi.pacbridge.utl;

import java.io.Serializable;

import org.json.JSONObject;


public class IpSiteAddress extends SiteAddress implements Serializable {
    private static final long serialVersionUID = 1001L;

    private String address;
    private Long comparableValue;

    public IpSiteAddress(String address, Integer networkNumber) {
        super(networkNumber);
        if (Utilities.isValidIpAddress(address) == false)
            throw new IllegalArgumentException("Invalid IP Address specified: '" + address + "'");
        this.address = address;
        this.comparableValue = Utilities.ipAddressAsIPv4Int(address);
    }

    @Override
    protected Long getComparableAddressValue() {
        return comparableValue;
    }

    @Override
    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("networkNumber", networkNumber);
        jsonObject.put("address", address);
        return jsonObject.toString();
    }
    
    public String getAddress() {
        return address;
    }

    public String toString() {
        return address + "/" + networkNumber;
    }
    
    public boolean equals(Object object) {
        if (object instanceof IpSiteAddress) {
            IpSiteAddress otherObject = (IpSiteAddress)object;
            return (otherObject.networkNumber.intValue() == networkNumber.intValue() 
                    && otherObject.address.equals(address));
        }
        return false;
    }

    public int hashCode() {
        return address.hashCode() + Integer.valueOf(networkNumber).hashCode();
    }
}
