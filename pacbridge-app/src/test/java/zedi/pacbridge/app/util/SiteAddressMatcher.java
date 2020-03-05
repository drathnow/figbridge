package zedi.pacbridge.app.util;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import zedi.pacbridge.utl.SiteAddress;


public class SiteAddressMatcher implements ArgumentMatcher<SiteAddress> {

    private SiteAddress siteAddress;
    private SiteAddress butWas;
    
    public SiteAddressMatcher(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
    }
    
    public boolean matches(SiteAddress object) {
        butWas = ((SiteAddress)object);
        return siteAddress.equals(butWas);
    }

    public void describeTo(Description description) {
        description.appendText("Expected <")
            .appendValue(siteAddress)
            .appendText("> but was <")
            .appendValue(butWas)
            .appendText(">");
    }
 
    public static ArgumentMatcher<SiteAddress> matchesSiteAddress(SiteAddress siteAddress) {
        return new SiteAddressMatcher(siteAddress);
    }
    
}