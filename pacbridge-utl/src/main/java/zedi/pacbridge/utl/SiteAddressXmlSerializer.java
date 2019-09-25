package zedi.pacbridge.utl;

import org.jdom2.Element;

public class SiteAddressXmlSerializer implements SiteAddressSerializer<Element>{

    public static final String ROOT_ELEMENT_NAME = "SiteAddress";
    public static final String IP_ADDRESS_TAG = "IpAddress";
    public static final String NETWORK_NUMBER_TAG = "NetworkNumber";
    public static final String NUID_TAG = "Nuid";
    
    @Override
    public SiteAddress siteAddressFor(Element siteAddressElement) {
        Integer networkNumber = new Integer(siteAddressElement.getChildText(NETWORK_NUMBER_TAG));
        String addressString = siteAddressElement.getChildText(IP_ADDRESS_TAG);
        if (addressString == null) {
            addressString = siteAddressElement.getChildText(NUID_TAG);
            return new NuidSiteAddress(addressString, networkNumber);
        } else {
            return new IpSiteAddress(addressString, networkNumber);
        }
    }

    @Override
    public Element objectForSiteAddress(SiteAddress siteAddress) {
        return null;
    }

}
