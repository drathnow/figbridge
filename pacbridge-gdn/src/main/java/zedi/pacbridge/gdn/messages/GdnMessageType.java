package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.net.MessageType;

public class GdnMessageType implements MessageType, Serializable {
    
    public static final int NUMBER_FOR_COMMAND_LINE_MESSAGE = 255;
    public static final int NUMBER_FOR_DISPLAY_MESSAGE_MESSAGE = 254;
    public static final int NUMBER_FOR_COMMAND_COMPLETE_MESSAGE = 253;
    public static final int NUMBER_FOR_SET_EVENTS_RESPONSE_MESSAGE = 47;
    public static final int NUMBER_FOR_ADD_EXTENDED_IOPOINT_MESSAGE = 46;
    public static final int NUMBER_FOR_SET_EXTENDED_ALARM_MESSAGE = 45;
    public static final int NUMBER_FOR_FRAGMENTED_REPORT_MESSAGE = 44;
    public static final int NUMBER_FOR_IOPOINT_DB_OVERRIDE_RESPONSE_MESSAGE = 43;
    public static final int NUMBER_FOR_IOPOINT_DB_OVERRIDE_MESSAGE = 42;
    public static final int NUMBER_FOR_MULTIPLE_REPORT_MESSAGE = 41;
    public static final int NUMBER_FOR_REQUEST_TIME_MESSAGE = 40;
    public static final int OTAD_MESSAGE_NUMBER = 36;
    public static final int NUMBER_FOR_SET_EVENT_MESSAGE = 33;
    public static final int NUMBER_FOR_EXTENDED_REPORT_MESSAGE = 38;
    public static final int NUMBER_FOR_SET_ALARMS_MESSAGE = 39;
    public static final int NUMBER_FOR_BLOB_MESSAGE = 11;
    public static final int NUMBER_FOR_STANDARD_REPORT_MESSAGE = 9;
    public static final int NUMBER_FOR_WRITE_IOPOINT_MESSAGE = 8;
    public static final int NUMBER_FOR_DEMAND_POLL_MESSAGE = 7;
    public static final int NUMBER_FOR_DELETE_IOPOINT_MESSAGE = 6;
    public static final int NUMBER_FOR_SET_ALARM_LIMIT_MESSAGE = 4;
    public static final int NUMBER_FOR_ERROR_RESPONSE_MESSAGE = 2;
    public static final int NUMBER_FOR_ADD_IOPOINT_MESSAGE = 1;
    public static final int NUMBER_FOR_PAC_CONSOLE_OPEN_SESSION_MESSAGE = -1;
    public static final int NUMBER_FOR_PAC_CONSOLE_STATUS_MESSAGE = -2;
    public static final int NUMBER_FOR_PAC_CONSOLE_CLOSE_SESSION = -3;
    
    public static final GdnMessageType CloseConsoleRequest = new GdnMessageType("Close Console ", -3, false);
    public static final GdnMessageType ConsoleStatusRequest = new GdnMessageType("Console Status", -2, false);
    public static final GdnMessageType OpenConsoleSession = new GdnMessageType("Console Open", -1, false);
    public static final GdnMessageType AddIoPointMessage = new GdnMessageType("Add IO Point", 1, true);
    public static final GdnMessageType ErrorResponseMessage = new GdnMessageType("Error Response", NUMBER_FOR_ERROR_RESPONSE_MESSAGE, false);
    public static final GdnMessageType ConfigureAlarmLimits = new GdnMessageType("Configure Alarm Limit", 4, true);
    public static final GdnMessageType DeleteIoPoint = new GdnMessageType("Delete IO Point", 6, true);
    public static final GdnMessageType DemandPoll = new GdnMessageType("Demand Poll", 7, true);
    public static final GdnMessageType WriteIoPoint = new GdnMessageType("Write IO Point", 8, true);
    public static final GdnMessageType StandardReport = new GdnMessageType("Standard Report", 9, false);
    public static final GdnMessageType BlobMessage = new GdnMessageType("Blob Message", 11, true);
    public static final GdnMessageType ExtendedReport = new GdnMessageType("Extended Report", 38, false);
    public static final GdnMessageType ConfigureAlarms = new GdnMessageType("Configure Alarms", 39, true);
    public static final GdnMessageType SetEvents = new GdnMessageType("Set Events", 33, true);
    public static final GdnMessageType RequestTime = new GdnMessageType("Request Time", 40, false);
    public static final GdnMessageType MultipleReport = new GdnMessageType("Multiple Report", 41, true);
    public static final GdnMessageType IoRefresh = new GdnMessageType("IO Refresh", 42, true);
    public static final GdnMessageType IoRefreshResponse = new GdnMessageType("IO Refresh Response", 43, true);
    public static final GdnMessageType FragmentedReport = new GdnMessageType("Fragmented Report", 44, false);
    public static final GdnMessageType ConfigureExtendedAlarms = new GdnMessageType("Configure Extended Alarms", 45, true);
    public static final GdnMessageType AddExtendedIoPoint = new GdnMessageType("Add Extended IO Point", NUMBER_FOR_ADD_EXTENDED_IOPOINT_MESSAGE, true);
    public static final GdnMessageType SetEventsResponse = new GdnMessageType("Configure Events Response", 47, true);
    public static final GdnMessageType PacConsoleCloseCommand = new GdnMessageType("PAC Console Close", 253, false);
    public static final GdnMessageType PacConsoleDisplay = new GdnMessageType("PAC Console Display", 254, false);
    public static final GdnMessageType PacConsoleCommand = new GdnMessageType("PAC Console Command", 255, false);
    public static final GdnMessageType Otad = new GdnMessageType("OTAD", 36, true);
    public static final GdnMessageType Reserved = new GdnMessageType("Reserved", Integer.MAX_VALUE, true);

    private String name;
    private Integer messageNumber;
    private boolean control;
    
    private GdnMessageType() {
    }
    
    private GdnMessageType(String name, Integer messageNumber, boolean isControl) {
        this.name = name;
        this.messageNumber = messageNumber;
        this.control = isControl;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isControl() {
        return control;
    }
    
    public Integer getNumber() {
        return messageNumber;
    }
    
    @Override
    public String toString() {
        return name + "(" + messageNumber + ")";
    }
    
    public static final GdnMessageType messageTypeForMessageNumber(int messageNumber) {
        switch (messageNumber) {
            case NUMBER_FOR_PAC_CONSOLE_STATUS_MESSAGE : return ConsoleStatusRequest;
            case NUMBER_FOR_PAC_CONSOLE_OPEN_SESSION_MESSAGE : return OpenConsoleSession;
            case NUMBER_FOR_PAC_CONSOLE_CLOSE_SESSION : return CloseConsoleRequest;
            case 1 : return AddIoPointMessage;
            case 4 : return ConfigureAlarmLimits;
            case 6 : return DeleteIoPoint;
            case 7 : return DemandPoll;
            case 8 : return WriteIoPoint;
            case 9 : return StandardReport;
            case 11 : return BlobMessage;
            case 38 : return ExtendedReport;
            case 39 : return ConfigureAlarms;
            case 33 : return SetEvents;
            case 36 : return Otad;
            case 40 : return RequestTime;
            case 41 : return MultipleReport;
            case 42 : return IoRefresh;
            case 43 : return IoRefreshResponse;
            case 44 : return FragmentedReport;
            case 45 : return ConfigureExtendedAlarms;
            case 46 : return AddExtendedIoPoint;
            case 47 : return SetEventsResponse;
            case 253 : return PacConsoleCloseCommand;
            case 254 : return PacConsoleDisplay;
            case 255 : return PacConsoleCommand;
            
        }
        throw new IllegalArgumentException("Unknown message type: " + messageNumber);
    }
}
