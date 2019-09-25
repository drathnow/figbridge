package zedi.pacbridge.app.events.connect;

import java.io.Serializable;

import zedi.pacbridge.app.events.EventName;

public class ConnectEventName implements EventName, Serializable {
    public static final ConnectEventName OTADPending = new ConnectEventName("OTADPending");
    public static final ConnectEventName DemandPoll = new ConnectEventName("DemandPoll");
    public static final ConnectEventName AddIOPoint = new ConnectEventName("AddIOPoint");
    public static final ConnectEventName DeleteIOPoint = new ConnectEventName("DeleteIOPoint");
    public static final ConnectEventName SetIOPointValue = new ConnectEventName("SetIOPointValue");
    public static final ConnectEventName SetAlarms = new ConnectEventName("SetAlarms");
    public static final ConnectEventName SetExtendedAlarms = new ConnectEventName("SetExtendedAlarms");
    public static final ConnectEventName SetEvents = new ConnectEventName("SetEvents");
    public static final ConnectEventName ConfigureAlarm = new ConnectEventName("ConfigureAlarm");
    public static final ConnectEventName RawDataReceived = new ConnectEventName("RawDataReceived");
    public static final ConnectEventName IORefresh = new ConnectEventName("IORefresh");
    public static final ConnectEventName EventResponse = new ConnectEventName("EventResponse");
    public static final ConnectEventName MockEvent = new ConnectEventName("MockEvent");
    public static final ConnectEventName Noop = new ConnectEventName("Noop"); 
    public static final ConnectEventName Otad = new ConnectEventName("Otad");
    
    static final long serialVersionUID = 1001;
    
    private String name;

    ConnectEventName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public static ConnectEventName eventNameForName(String eventName) {
        if (OTADPending.name.equalsIgnoreCase(eventName))
            return OTADPending;
        if (DemandPoll.name.equalsIgnoreCase(eventName))
            return DemandPoll;
        if (AddIOPoint.name.equalsIgnoreCase(eventName))
            return AddIOPoint;
        if (DeleteIOPoint.name.equalsIgnoreCase(eventName))
            return DeleteIOPoint;
        if (SetIOPointValue.name.equalsIgnoreCase(eventName))
            return SetIOPointValue;
        if (SetAlarms.name.equalsIgnoreCase(eventName))
            return SetAlarms;
        if (SetExtendedAlarms.name.equalsIgnoreCase(eventName))
            return SetExtendedAlarms;
        if (SetEvents.name.equalsIgnoreCase(eventName))
            return SetEvents;
        if (ConfigureAlarm.name.equalsIgnoreCase(eventName))
            return ConfigureAlarm;
        if (RawDataReceived.name.equalsIgnoreCase(eventName))
            return RawDataReceived;
        if (IORefresh.name.equalsIgnoreCase(eventName))
            return IORefresh;
        if (EventResponse.name.equalsIgnoreCase(eventName))
            return EventResponse;
        if (MockEvent.name.equalsIgnoreCase(eventName))
            return MockEvent;
        if (Noop.name.equalsIgnoreCase(eventName))
            return Noop;
        if (Otad.name.equalsIgnoreCase(eventName))
            return Otad;
        return null;
    }

}
