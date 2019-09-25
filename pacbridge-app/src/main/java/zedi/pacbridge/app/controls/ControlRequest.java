package zedi.pacbridge.app.controls;

import java.io.Serializable;

import org.hibernate.search.annotations.Indexed;
import org.json.JSONObject;

import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.app.util.LookupHelper;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;

@Indexed(index="ControlRequestIndex")
public class ControlRequest extends OutgoingRequest implements Serializable {
    private static final long serialVersionUID = 1001L;

    public static final String CONTROL_RESPONSE_TIMEOUT_SECONDS_PROPERTY_NAME = "controls.responseTimeoutSeconds";
    public static final Integer DEFAULT_CONTROL_RESPONSE_TIMEOUT_SECONDS = 30;
    
    private Control control;
    
    @SuppressWarnings("unused")
    private ControlRequest() {
    }
    
    public ControlRequest(SiteAddress siteAddress, Long eventId, Control control) {
        super(siteAddress, eventId, OutgoingRequestType.CONTROL);
        this.control = control;
    }
    
    public Control getControl() {
        return control;
    }
     
    @Override
    public String shortDescription() {
        return "Control: " + control.messageType().getName();
    }

    @Override
    public Integer getResponseTimeoutSeconds() {
        Integer networkNumber = getNetworkNumber();
        NetworkService networkService = DependencyResolver.Implementation.sharedInstance().getImplementationOf(NetworkService.JNDI_NAME);
        PropertyBag propertyBag = networkService.propertyBagForNetworkNumber(networkNumber);
        return propertyBag.integerValueForProperty(CONTROL_RESPONSE_TIMEOUT_SECONDS_PROPERTY_NAME, DEFAULT_CONTROL_RESPONSE_TIMEOUT_SECONDS);
    }

    @Override
    public OutgoingRequestProcessor outgoingRequestProcessor() {
        LookupHelper lookupHelper =  DependencyResolver.Implementation.sharedInstance().getImplementationOf(LookupHelper.JNDI_NAME);
        RequestProgressListener listener = lookupHelper.getRequestProgressListener();
        NotificationCenter notificationCenter = lookupHelper.getNotificationCenter();
        ControlResponseStrategyFactory strategyFactory = lookupHelper.getControlResponseStrategyFactory();
        return new ControlRequestProcessor(this, strategyFactory, listener, notificationCenter);
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject(super.toJSONString());
        jsonObject.put("control", control.messageType().getName());
        return jsonObject.toString();
    }
}
