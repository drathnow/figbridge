import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;

import zedi.pacbridge.app.controls.SiteAddressFieldBridge;
import zedi.pacbridge.utl.SiteAddress;

public class FooRequest implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

    @Field
    private String requestId;
    @Field
    private Long eventId;
    @Field
    @FieldBridge(impl = SiteAddressFieldBridge.class)
    private SiteAddress siteAddress;
}
