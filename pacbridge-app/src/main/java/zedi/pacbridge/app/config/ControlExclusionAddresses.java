package zedi.pacbridge.app.config;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jdom2.Element;

import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

class ControlExclusionAddresses {
    public static final String ROOT_ELEMENT_NAME = "ControlExclusionAddresses";
    public static final String ADDRESS_TAG = "Address";
    public static final String IP_TAG = "ip";
    public static final String NUID_TAG = "nuid";
    public static final String NUMBER_TAG = "number";
    
    public static Set<SiteAddress> controlExclusionAddressesForJDomElement(Element rootElement) {
        Set<SiteAddress> siteAddresses = new TreeSet<SiteAddress>();
        Iterator<Element> iterator = rootElement.getChildren(ADDRESS_TAG).iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Integer networkNumber = Integer.parseInt(element.getAttributeValue(NUMBER_TAG));
            if (element.getAttributeValue(NUID_TAG) == null)
                siteAddresses.add(new IpSiteAddress(element.getAttributeValue(IP_TAG), networkNumber));
            else
                siteAddresses.add(new NuidSiteAddress(element.getAttributeValue(NUID_TAG), networkNumber));
        }
        return siteAddresses;
    }
}