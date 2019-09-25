package zedi.pacbridge.utl;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;


public class SiteAddressMatcher extends BaseMatcher<SiteAddress> {

    private SiteAddress siteAddress;
    private SiteAddress butWas;
    
    public SiteAddressMatcher(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
    }
    
    public boolean matches(Object object) {
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
 
    public static Matcher<SiteAddress> matchesSiteAddress(SiteAddress siteAddress) {
        return new SiteAddressMatcher(siteAddress);
    }
    
}