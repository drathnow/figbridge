package zedi.pacbridge.utl;

import java.io.Serializable;


public abstract class SiteAddress implements Comparable<SiteAddress>, Serializable, Cloneable {
    private static final long serialVersionUID = 1001L;

    public static final SiteAddress UNKNOWN_ADDRESS = new IpSiteAddress("1.0.0.127", 0);
    
    protected Integer networkNumber;

    protected SiteAddress(Integer networkNumber) {
        this.networkNumber = networkNumber;
    }

    public abstract String getAddress();
    public abstract String toJSONString();
    protected abstract Long getComparableAddressValue();

    public Integer getNetworkNumber() {
        return networkNumber;
    }


    public int compareTo(SiteAddress otherSiteAddress) {
        int result = networkNumber.compareTo(otherSiteAddress.networkNumber);
        return (result == 0) ? getComparableAddressValue().compareTo(otherSiteAddress.getComparableAddressValue()) : result;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public interface SiteAddressFormatter {
        public void takeNuid(String nuid);
        public void takeNetworkNumer(Integer networkNumber);
    }
}
