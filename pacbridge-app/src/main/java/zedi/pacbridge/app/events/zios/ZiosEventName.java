package zedi.pacbridge.app.events.zios;

import java.io.Serializable;

import zedi.pacbridge.app.events.EventName;

public class ZiosEventName implements EventName, Serializable {
    public static final ZiosEventName WriteIOPoints = new ZiosEventName("WriteIOPoints");
    public static final ZiosEventName AddIOPoint = new ZiosEventName("AddIOPoint");
    public static final ZiosEventName DemandPoll = new ZiosEventName("DemandPoll");
    public static final ZiosEventName SiteConnected = new ZiosEventName("SiteConnected");
    public static final ZiosEventName SiteDisconnected = new ZiosEventName("SiteDisconnected");
    public static final ZiosEventName Configure= new ZiosEventName("Configure");
    public static final ZiosEventName ConfigureResponse = new ZiosEventName("ConfigureResponse");
    public static final ZiosEventName RawDataReceived = new ZiosEventName("RawDataReceived");
    public static final ZiosEventName EventResponse = new ZiosEventName("EventResponse");
    public static final ZiosEventName SiteReport = new ZiosEventName("SiteReport");
    public static final ZiosEventName Scrub = new ZiosEventName("Scrub");
    public static final ZiosEventName ConfigureUpdate = new ZiosEventName("ConfigureUpdate");
    public static final ZiosEventName OtadRequest = new ZiosEventName("OtadRequest");
    public static final ZiosEventName OtadStatus = new ZiosEventName("OtadStatus");

    static final long serialVersionUID = 1001;
    
    private String name;

    ZiosEventName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static ZiosEventName eventNameForName(String eventName) {
        if (WriteIOPoints.name.equalsIgnoreCase(eventName))
            return WriteIOPoints;
        if (DemandPoll.name.equalsIgnoreCase(eventName))
            return DemandPoll;
        if (SiteConnected.name.equalsIgnoreCase(eventName))
            return SiteConnected;
        if (SiteDisconnected.name.equalsIgnoreCase(eventName))
            return SiteDisconnected;
        if (Configure.name.equalsIgnoreCase(eventName))
            return Configure;
        if (ConfigureResponse.name.equalsIgnoreCase(eventName))
            return ConfigureResponse;
        if (RawDataReceived.name.equalsIgnoreCase(eventName))
            return RawDataReceived;
        if (SiteReport.name.equalsIgnoreCase(eventName))
            return SiteReport;
        if (Scrub.name.equalsIgnoreCase(eventName))
            return Scrub;
        if (ConfigureUpdate.name.equalsIgnoreCase(eventName))
            return ConfigureUpdate;
        if (OtadRequest.name.equalsIgnoreCase(eventName))
            return OtadRequest;
        if (OtadStatus.name.equalsIgnoreCase(eventName))
            return OtadStatus;
        return null;
    }

}
