package zedi.pacbridge.app.controls;

import org.hibernate.search.bridge.StringBridge;

public class SiteAddressFieldBridge implements StringBridge {

    @Override
    public String objectToString(Object object) {
        return object.toString();
    }

}
