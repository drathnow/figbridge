package zedi.pacbridge.net;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import zedi.pacbridge.utl.NuidSiteAddress;


public class NuidSiteAddressMatcher extends BaseMatcher<NuidSiteAddress> {

    private String expectedNuid;
    private int expectedNetworkNumber;
    private String wasString;
    
    public NuidSiteAddressMatcher(String nuid, int networkNumber) {
        this.expectedNuid = nuid;
        this.expectedNetworkNumber = networkNumber;
    }
    
    public boolean matches(Object object) {
        NuidSiteAddress address = ((NuidSiteAddress)object);
        wasString = address.toString();
        return address.getNetworkNumber() == expectedNetworkNumber && address.getNetworkUnitId().equals(expectedNuid);
    }

    public void describeTo(Description description) {
        description.appendText("Expected: ");
        description.appendText(expectedNuid+"/"+expectedNetworkNumber);
        description.appendText("But was: ");
        description.appendText(wasString);
    }
    
}