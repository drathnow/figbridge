package zedi.pacbridge.app.events.zios;

import org.jdom2.Element;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.app.events.ControlEvent;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.app.services.OutgoingRequestService;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.OtadRequestControl;

public class OtadRequestEvent extends DeviceEvent implements ControlEvent {
    public static final String ROOT_ELEMENT_TAG = ZiosEventName.OtadRequest.getName();
    public static final String URL_TAG = "Url";
    public static final String RETRIES_TAG = "Retries";
    public static final String TIMEOUT_TAG = "TimeoutSeconds";
    public static final String MD5HASH_TAG = "Md5Hash";
    public static final String FORCE_RESTART_TAG = "ForceRestart";
    public static final String USE_AUTHENTICAION_TAG = "UseAuthentication";
    public static final String RETRY_INTERVAL_TAG = "RetryIntervalSeconds";

    private String url;
    private String md5Hash;
    private Integer retries;
    private Integer retryIntervalSeconds;
    private Integer timeoutSeconds;
    private Boolean forceRestart;
    private Boolean useAuthentication;
    
    OtadRequestEvent(Long eventId, 
                            SiteAddress siteAddress, 
                            String firmwareVersion, 
                            String url, 
                            String md5Hash, 
                            boolean forceRestart, 
                            boolean useAuthentication, 
                            int retries, 
                            int retryIntervalSeconds, 
                            int timeoutSeconds) {
        super(ZiosEventName.OtadRequest, eventId, siteAddress, firmwareVersion);
        this.url = url;
        this.md5Hash = md5Hash;
        this.retries = retries;
        this.retryIntervalSeconds = retryIntervalSeconds;
        this.timeoutSeconds = timeoutSeconds;
        this.forceRestart = forceRestart;
        this.useAuthentication = useAuthentication;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public Integer getRetries() {
        return retries;
    }
    
    public Integer getRetryIntervalSeconds() {
        return retryIntervalSeconds;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public Boolean isForceRestart() {
        return forceRestart;
    }
    
    public Boolean isUseAuthentication() {
        return useAuthentication;
    }
    
    @Override
    public String asXmlString() {
        Element element =  super.rootElement();
        Element subElement = new Element(getEventName().getName());
        subElement.addContent(new Element(URL_TAG).setText(url));
        subElement.addContent(new Element(MD5HASH_TAG).setText(md5Hash));
        if (retries > 0)
            subElement.addContent(new Element(RETRIES_TAG).setText(retries.toString()));
        if (timeoutSeconds > 0)
            subElement.addContent(new Element(TIMEOUT_TAG).setText(timeoutSeconds.toString()));
        if (forceRestart)
            subElement.addContent(new Element(FORCE_RESTART_TAG).setText(forceRestart.toString()));
        element.addContent(subElement);
        return JDomUtilities.xmlStringForElement(element);
    }

    @Override
    public void handle(OutgoingRequestService outgoingRequestService) {
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
        flags.setForceRestart(forceRestart.booleanValue());
        flags.setUseAuthentication(useAuthentication.booleanValue());
        OtadRequestControl control = new OtadRequestControl(getEventId(), flags, url, md5Hash, retries, retryIntervalSeconds, timeoutSeconds);
        ControlRequest controlRequest = new ControlRequest(getSiteAddress(), getEventId(), control);
        outgoingRequestService.queueOutgoingRequest(controlRequest);
    }

    public static OtadRequestEvent otadRequestEventForElement(Element element, DeviceCache deviceCache) throws InvalidEventFormatException {
        int retries = 0;
        int timeoutSeconds = 0;
        String eventIdString = element.getChildText(EVENT_ID_TAG);
        if (eventIdString == null)
            throw new InvalidEventFormatException("Event is missing an EventId");
        Long eventId = Long.valueOf(element.getChildText(EVENT_ID_TAG));
        String nuid = element.getChildText(NUID_TAG);
        if (nuid == null)
            throw new InvalidEventFormatException("Event is missing an NUID");
        Device device = deviceCache.deviceForNetworkUnitId(nuid);
        if (device == null)
            device = new Device(nuid, 17);
        SiteAddress siteAddress = new NuidSiteAddress(nuid, correctedNetworkNumber(device.getNetworkNumber()));
        String firmware = device.getFirmwareVersion();
        Element subElement = element.getChild(ZiosEventName.OtadRequest.getName());
        String retriesString = subElement.getChildText(RETRIES_TAG);
        if (retriesString != null)
            retries = Integer.parseInt(retriesString);
        String timeoutString = subElement.getChildText(TIMEOUT_TAG);
        if (timeoutString != null)
            timeoutSeconds = Integer.parseInt(timeoutString);
        String url = subElement.getChildText(URL_TAG);
        if (url == null)
            throw new InvalidEventFormatException("Event is missing a URL");
        String md5Hash = subElement.getChildText(MD5HASH_TAG);
        if (md5Hash == null)
            throw new InvalidEventFormatException("Event is missing an MD5 hash");
        String forceRestartString =  subElement.getChildText(FORCE_RESTART_TAG);
        boolean forceRestart = (forceRestartString == null) ? false : Boolean.parseBoolean(forceRestartString);
        String useAuthString =  subElement.getChildText(USE_AUTHENTICAION_TAG);
        boolean useAuth = (useAuthString == null) ? false : Boolean.parseBoolean(useAuthString);
        return new OtadRequestEvent(eventId, siteAddress, firmware, url, md5Hash, forceRestart, useAuth, retries, 0, timeoutSeconds);
    }
}
