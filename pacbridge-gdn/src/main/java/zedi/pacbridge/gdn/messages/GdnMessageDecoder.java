package zedi.pacbridge.gdn.messages;


import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.gdn.PacEventStatus;
import zedi.pacbridge.gdn.messages.otad.LoadImageCommand;
import zedi.pacbridge.gdn.messages.otad.LoadImageResponse;
import zedi.pacbridge.gdn.messages.otad.OtadMessage;
import zedi.pacbridge.gdn.messages.otad.OtadMessageType;
import zedi.pacbridge.gdn.messages.otad.RequestSystemInfoCommand;
import zedi.pacbridge.gdn.messages.otad.RequestSystemInfoResponse;
import zedi.pacbridge.gdn.messages.otad.SetCodeMapCommand;
import zedi.pacbridge.gdn.messages.otad.SetCodeMapResponse;
import zedi.pacbridge.gdn.messages.otad.WriteCodeBlockCommand;
import zedi.pacbridge.gdn.messages.otad.WriteCodeBlockResponse;
import zedi.pacbridge.gdn.otad.CodeMap;
import zedi.pacbridge.net.MessageDecoder;

public class GdnMessageDecoder implements MessageDecoder {
    private static Logger logger = Logger.getLogger(GdnMessageDecoder.class);

    protected static String FORMAT_HEADER = "    Index        Type                  Value             Alarm Status ";
    protected static String FORMAT_ULINE = "    -------      ------------------    ----------------  ------------ ";
    private static String FORMAT_STRING = "    {0}       {1} {2}  {3}";

    protected static String EVENT_FORMAT_HEADER = "    Index     Status";
    protected static String EVENT_FORMAT_ULINE = "    -------   ------------------";
    private static String EVENT_FORMAT_STRING = "    {0}       {1}";

    private SimpleDateFormat dateFormat;
    private DecimalFormat decimalFormat;
    private DecimalFormat integerFormat;
    private MessageFormat messageFormat;
    private MessageFormat eventFormat;

    public GdnMessageDecoder(TimeZone timezone) {
        dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss z");
        dateFormat.setTimeZone(timezone);
        decimalFormat = new DecimalFormat("###,###,###,##0.00");
        integerFormat = new DecimalFormat("####");
        messageFormat = new MessageFormat(FORMAT_STRING);
        eventFormat = new MessageFormat(EVENT_FORMAT_STRING);
    }
    
    public GdnMessageDecoder() {
        this(TimeZone.getTimeZone("GMT"));
    }

    public String decodedMessage(byte[] byteMessage) {
        return decodePacket(GdnPacket.packetFromBuffer(ByteBuffer.wrap(byteMessage)));
    }
    
    public String decodePacket(GdnPacket gdnPacket) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Header Version: ").append(gdnPacket.getHeader().headerType()).append("\n");
        switch (gdnPacket.getHeader().headerType().getTypeNumber()) {
            case SwtHeaderType.HEADER_VERSION12 :
                SwtHeader12 header = (SwtHeader12)gdnPacket.getHeader();
                int contextId = header.getSessionId();
                stringBuffer.append("     ContextId: ").append(contextId).append("\n");
                break;

            case SwtHeaderType.HEADER_VERSION10 :
                break;

            default :
                stringBuffer.append("Header version not supported: " + gdnPacket.getHeader().headerType()).append("\n");
        }
        stringBuffer.append("\n");
        stringBuffer.append(decodeGdnMessage(gdnPacket.getMessage()));
        return stringBuffer.toString();
    }

    public String decodeGdnMessage(GdnMessage gdnMessage) {
        switch (gdnMessage.messageType().getNumber()) {
            case GdnMessageType.NUMBER_FOR_EXTENDED_REPORT_MESSAGE :
            case GdnMessageType.NUMBER_FOR_STANDARD_REPORT_MESSAGE:
                return decodeMessage((IoPointReportMessage<?>)gdnMessage);

            case GdnMessageType.NUMBER_FOR_DISPLAY_MESSAGE_MESSAGE :
                return decodeMessage((DisplayMessage)gdnMessage);

            case GdnMessageType.NUMBER_FOR_COMMAND_LINE_MESSAGE :
                return decodeMessage((CommandLineMessage)gdnMessage);

            case GdnMessageType.NUMBER_FOR_COMMAND_COMPLETE_MESSAGE :
                return decodeMessage((CommandCompleteMessage)gdnMessage);

            case GdnMessageType.OTAD_MESSAGE_NUMBER :
                return decodeOtaMessage((OtadMessage)gdnMessage);

            case GdnMessageType.NUMBER_FOR_DEMAND_POLL_MESSAGE :
                return decodeMessage((DemandPollControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_SET_ALARMS_MESSAGE :
                return decodeMessage((ConfigureAlarmsControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_ADD_IOPOINT_MESSAGE :
                return decodeMessage((AddStandardIoPointControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_ADD_EXTENDED_IOPOINT_MESSAGE :
                return decodeMessage((AddExtendedIoPointControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_SET_EVENT_MESSAGE :
                return decodeMessage((SetEventsControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_WRITE_IOPOINT_MESSAGE :
                return decodeMessage((WriteIoPointControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_DELETE_IOPOINT_MESSAGE :
                return decodeMessage((DeleteIoPointControl)gdnMessage);

            case GdnMessageType.NUMBER_FOR_SET_EVENTS_RESPONSE_MESSAGE :
                return decodeMessage((SetEventsResponseMessage)gdnMessage);

            case GdnMessageType.NUMBER_FOR_SET_EXTENDED_ALARM_MESSAGE :
                return decodeMessage((ConfigureExtendedAlarmsControl)gdnMessage);
                
            case GdnMessageType.NUMBER_FOR_REQUEST_TIME_MESSAGE : 
                return decodeMessage((RequestTimeMessage)gdnMessage);
                
//            case GdnMessage.NUMBER_FOR_IOPOINT_DB_OVERRIDE_MESSAGE :
//                return decodeMessage((PacIoRefreshMessage)gdnMessage);
//                
//            case GdnMessage.NUMBER_FOR_IOPOINT_DB_OVERRIDE_RESPONSE_MESSAGE :
//                return decodeMessage((PacIoRefreshResponseMessage)gdnMessage);
//            

            default :
                return "Message type not currently supported: " + gdnMessage.getClass().getName() + '(' + gdnMessage.messageType() + ')';
        }
    }
//
//    private String decodeMessage(PacIoRefreshResponseMessage responseMessage) {
//        StringBuilder stringBuffer = new StringBuilder();
//        stringBuffer.append("Message Type: IORefresh Response").append("\n");
//        stringBuffer.append("   Containing messageType: ")
//                    .append(GdnMessage.gdnMessageForMessageNumber(responseMessage.getMessageTypeNumber()).getName())
//                    .append('\n');
//        stringBuffer.append("                  Timeout: ")
//                    .append(responseMessage.isTimeout())
//                    .append("\n");;
//        stringBuffer.append("            Message Count: ")
//                    .append(responseMessage.messageCount());
//        return stringBuffer.toString();
//    }
//    
    public String decodeMessage(RequestTimeMessage gdnMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Request Time").append("\n");
        
        String clientTimestamp = gdnMessage.getClientTimestamp().getTime() == 0 ? "0" : dateFormat.format(gdnMessage.getClientTimestamp());
        String serverTimestamp = gdnMessage.getClientTimestamp().getTime() == 0 ? "0" : dateFormat.format(gdnMessage.getServerTimestamp());
        stringBuffer.append("        Type: ").append(gdnMessage.isRequest() ? "Request" : "Response").append("\n");
        stringBuffer.append("   Client TS: ").append(clientTimestamp).append("\n");
        stringBuffer.append("   Server TS: ").append(serverTimestamp).append("\n");
        return stringBuffer.toString();
    }

    public String decodeMessage(SetEventsResponseMessage responseMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Set Events Response").append("\n");

        stringBuffer.append(EVENT_FORMAT_HEADER).append("\n");
        stringBuffer.append(EVENT_FORMAT_ULINE).append("\n");

        for (Integer index : responseMessage.getIndexes()) {
            PacEventStatus eventStatus = responseMessage.eventStatusForIndex(index);
            String statusMessage = eventStatus.getMessage();
            Object[] args = new Object[]{index, statusMessage};
            stringBuffer.append(eventFormat.format(args)).append('\n');
        }

        return stringBuffer.toString();
    }

    public String decodeMessage(DeleteIoPointControl control) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type  : Delete IO Point").append("\n");
        stringBuffer.append("Pollset Number: ").append(control.getPollSetNumber()).append("\n");
        stringBuffer.append("         Index: ").append(control.getIndex()).append("\n");
        return stringBuffer.toString();
    }

    public String decodeMessage(WriteIoPointControl gdnMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Write IO Point").append("\n");

        stringBuffer.append("       Index: ").append(gdnMessage.getIndex()).append("\n");
        stringBuffer.append("   Data Type: ").append(gdnMessage.getDataType()).append("\n");
        stringBuffer.append("       Value: ").append(gdnMessage.getValue().getValue().toString()).append("\n");

        return stringBuffer.toString();
    }

//    public String decodeMessage(PacIoRefreshMessage refreshMessage) {
//        StringBuilder stringBuffer = new StringBuilder();
//        stringBuffer.append("Message Type: PAC IO Refresh").append("\n");
//        stringBuffer.append("   Total Messgae Count: ").append(refreshMessage.totalMessageCount()).append("\n");
//        stringBuffer.append(" Current Message index: ").append(refreshMessage.currentMessageIndex()).append("\n");
//        stringBuffer.append("  Containing Messages : ").append(refreshMessage.containingMessageTypeNumber()).append("\n");
//
//        Vector<GdnMessage> messages = refreshMessage.gdnMessages();
//        for (GdnMessage message : messages)
//            stringBuffer.append(decodeGdnMessage(message)).append("\n");
//
//        return stringBuffer.toString();
//    }
//
    public String decodeMessage(ConfigureAlarmsControl alarmMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Set Alarms").append("\n");

        stringBuffer.append("      Index: ").append(alarmMessage.getIndex()).append("\n");
        stringBuffer.append("   Data Type: ").append(alarmMessage.getDataType()).append("\n");
        stringBuffer.append("Alarm Values: ").append(alarmMessage.getAlarmValues().size()).append("\n\n");

        for (StandardAlarmValue alarmValue : alarmMessage.getAlarmValues()) {
            stringBuffer.append("           Alarm Type: ").append(alarmValue.getAlarmType()).append("\n");
            stringBuffer.append("              Enabled: ").append(alarmValue.isEnabled());
            if (alarmValue.isEnabled())
                stringBuffer.append("          Limit Value: ").append(alarmValue.getLimitValue()).append("\n");
        }
        return stringBuffer.toString();
    }

    public String decodeMessage(ConfigureExtendedAlarmsControl alarmMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Set Extended Alarm").append("\n");

        stringBuffer.append("    Version: ").append(alarmMessage.getVersion()).append("\n");
        stringBuffer.append("      Index: ").append(alarmMessage.getIndex()).append("\n");
        stringBuffer.append("   Data Type: ").append(alarmMessage.getDataType()).append("\n");
        stringBuffer.append("Alarm Values: ").append(alarmMessage.getExtendedAlarmValues().size()).append("\n\n");

        for (ExtendedAlarmValue alarmValue : alarmMessage.getExtendedAlarmValues()) {
            stringBuffer.append("           Alarm Type: ").append(alarmValue.getAlarmType()).append("\n");
            stringBuffer.append("              Enabled: ").append(alarmValue.isEnabled()).append("\n");
            if (alarmValue.isEnabled()) {
                stringBuffer.append("           Clear Time: ").append(alarmValue.getClearTimeSeconds()).append("\n");
                stringBuffer.append("             Set Time: ").append(alarmValue.getSetTimeSeconds()).append("\n");
                stringBuffer.append("          Limit Value: ").append(alarmValue.getLimitValue()).append("\n");
                stringBuffer.append("     Hysteresis Value: ").append(alarmValue.getHysteresisValue().toString()).append("\n");
            }
        }
        return stringBuffer.toString();
    }

    public String decodeMessage(DemandPollControl gdnMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Demand Poll").append("\n");

        stringBuffer.append("Poll Set Number: ").append(gdnMessage.getPollSetNumber()).append("\n");
        stringBuffer.append("          Index: ").append(gdnMessage.getIndex()).append("\n");
        return stringBuffer.toString();
    }

    public String decodeMessage(AddExtendedIoPointControl addIoPointMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Add Extended IO Point").append("\n");

        stringBuffer.append("         Index: ").append(addIoPointMessage.getIndex()).append("\n");
        stringBuffer.append("Pollset Number: ").append(addIoPointMessage.getPollSetNumber()).append("\n");
        stringBuffer.append("     Data Type: ").append(addIoPointMessage.getDataType()).append("\n");
        stringBuffer.append("   RTU Address: ").append(addIoPointMessage.getRtuAddress()).append("\n");
        stringBuffer.append("            F1: ").append(addIoPointMessage.getF1()).append("\n");
        stringBuffer.append("            F2: ").append(addIoPointMessage.getF2()).append("\n");
        stringBuffer.append("            F3: ").append(addIoPointMessage.getF3()).append("\n");
        stringBuffer.append("            F4: ").append(addIoPointMessage.getF4()).append("\n");
        stringBuffer.append("        Factor: ").append(addIoPointMessage.getFactor()).append("\n");
        stringBuffer.append("IO Point Class: ").append(addIoPointMessage.getIoPointClass()).append("\n");
        stringBuffer.append("        Offset: ").append(addIoPointMessage.getOffset()).append("\n");
        return stringBuffer.toString();
    }

    public String decodeMessage(AddStandardIoPointControl addIoPointMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Add IO Point").append("\n");

        stringBuffer.append("         Index: ").append(addIoPointMessage.getIndex()).append("\n");
        stringBuffer.append("Pollset Number: ").append(addIoPointMessage.getPollSetNumber()).append("\n");
        stringBuffer.append("     Data Type: ").append(addIoPointMessage.getDataType()).append("\n");
        stringBuffer.append("   RTU Address: ").append(addIoPointMessage.getRtuAddress()).append("\n");
        stringBuffer.append("            F1: ").append(addIoPointMessage.getF1()).append("\n");
        stringBuffer.append("            F2: ").append(addIoPointMessage.getF2()).append("\n");
        stringBuffer.append("            F3: ").append(addIoPointMessage.getF3()).append("\n");
        stringBuffer.append("            F4: ").append(addIoPointMessage.getF4()).append("\n");
        return stringBuffer.toString();
    }

    public String decodeMessage(SetEventsControl eventsMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Set Events").append("\n").append("\n");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        printWriter.printf("%-5s %-10s %-6s %-6s %-24s %-9s %s\n", "Index", "Action", "Param1", "Param2", "Start Time", "Interval", "Duration");
        printWriter.printf("%-5s %-10s %-6s %-6s %-24s %-9s %s\n", "-----", "------", "------", "------", "----------", "--------", "--------");
        for (DeviceEvent event : eventsMessage.getEvents()) {
            printWriter.printf("%-5d %-10s %-6d %-6d %-24s %-9d %d\n", 
                    event.getEventIndex(), 
                    event.getEventAction().toString(), 
                    event.getEventParameter1(), 
                    event.getEventParameter2(), 
                    dateFormat.format(event.getStartTime()), 
                    event.getIntervalSeconds(),
                    event.getDurationSeconds());
        }

        printWriter.flush();
        stringBuffer.append(new String(byteArrayOutputStream.toByteArray()));
        return stringBuffer.toString();
    }

    private String decodeOtaMessage(OtadMessage message) {
        if (message.getOtadMessageType() == OtadMessageType.RequestSystemInfo) {
            if (message.isCommand())
                return decodeMessage((RequestSystemInfoCommand)message);
            else
                return decodeMessage((RequestSystemInfoResponse)message);
        }
        
        if (message.getOtadMessageType() == OtadMessageType.WriteCodeBlock) {
            if (message.isCommand())
                return decodeMessage((WriteCodeBlockCommand)message);
            else
                return decodeMessage((WriteCodeBlockResponse)message);
        }
        
        if (message.getOtadMessageType() == OtadMessageType.SetCodeMap) {
            if (message.isCommand())
                return decodeMessage((SetCodeMapCommand)message);
            else
                return decodeMessage((SetCodeMapResponse)message);
        }
        if (message.getOtadMessageType() == OtadMessageType.LoadImage) {
            if (message.isCommand())
                return decodeMessage((LoadImageCommand)message);
            else
                return decodeMessage((LoadImageResponse)message);
        }
        logger.error("Invalid message type for decoding: " 
                + message.getOtadMessageType()
                + ". (Class: " + message.getClass().getName()
                + ")");
        return "Uknown OTAD message: " + message.getOtadMessageType();
    }

    public String decodeMessage(SetCodeMapCommand command) {
        StringBuilder stringBuffer = new StringBuilder();
        Formatter formatter = new Formatter(stringBuffer);
        
        stringBuffer.append("Message Type: Set Code Map").append("\n");
        formatter.format(" Code Map ID: %1$d (0x%1$04X)\n", command.getIdentifier());
        stringBuffer.append("Command Type: Command\n");
        if (command.getCodeMap() != null) {
            CodeMap flashMap = command.getCodeMap();
            byte[] blocks = flashMap.getMapBytes();
            String bytesString = formattedBytes(blocks, "  Code Block: ");
            stringBuffer.append(bytesString);
        } else 
            stringBuffer.append("  Code Block: <null>");
        formatter.close();
        return stringBuffer.toString();
    }
    
    public String decodeMessage(SetCodeMapResponse response) {
        StringBuilder stringBuffer = new StringBuilder();
        Formatter formatter = new Formatter(stringBuffer);
        
        stringBuffer.append("Message Type: Set Code Map").append("\n");
        formatter.format(" Code Map ID: %1$d (0x%1$04X)\n", response.getIdentifier());
        stringBuffer.append("Command Type: Response\n");
        stringBuffer.append("  Error Code: ").append(response.getErrorCode()).append("\n");
        if (response.getCodeMap() != null) {
            CodeMap flashMap = response.getCodeMap();
            byte[] blocks = flashMap.getMapBytes();
            String bytesString = formattedBytes(blocks, "  Code Block: ");
            stringBuffer.append(bytesString);
        } else 
            stringBuffer.append("  Code Block: <null>");
        formatter.close();
        return stringBuffer.toString();
    }

    public String decodeMessage(LoadImageCommand otaMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Load Image").append("\n");
        stringBuffer.append("Command Type: Command").append("\n");
        stringBuffer.append("  Identifier: ").append(otaMessage.getIdentifier());
        return stringBuffer.toString();
    }

    public String decodeMessage(LoadImageResponse otaMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Load Image").append("\n");
        stringBuffer.append("  Error Code: ").append(otaMessage.getErrorCode()).append("\n");
        stringBuffer.append("Command Type: Response").append("\n");
        stringBuffer.append("  Identifier: ").append(otaMessage.getIdentifier());
        return stringBuffer.toString();
    }

    public String decodeMessage(WriteCodeBlockCommand otaMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Write Code Block").append("\n");

        stringBuffer.append("Command Type: Command").append("\n");
        String hexAddress = "0x" + Integer.toHexString(otaMessage.getAddress()).toUpperCase();
        stringBuffer.append("     Address: ").append(hexAddress).append("\n");
        stringBuffer.append("      Length: ").append(otaMessage.getCodeBlock().length).append("\n");
        stringBuffer.append("  Block Type: ").append(otaMessage.getBlockType()).append("\n");
        stringBuffer.append("  Identifier: ").append(otaMessage.getIdentifier()).append("\n");
        
        if (otaMessage.getCodeBlock() != null) {
            String bytesString = formattedBytes(otaMessage.getCodeBlock(), "  Code Block: ");
            stringBuffer.append(bytesString);
        } else 
            stringBuffer.append("  Code Block: <null>");
        return stringBuffer.toString();
    }

    public String decodeMessage(WriteCodeBlockResponse otaMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Write Code Block").append("\n");
        stringBuffer.append("  Error Code: ").append(otaMessage.getErrorCode()).append("\n");
        stringBuffer.append("Command Type: Response").append("\n");
        String hexAddress = "0x" + Integer.toHexString(otaMessage.getAddress()).toUpperCase();
        stringBuffer.append("     Address: ").append(hexAddress).append("\n");
        stringBuffer.append("      Length: ").append(otaMessage.getBlockLength()).append("\n");
        stringBuffer.append("  Block Type: ").append(otaMessage.getBlockType()).append("\n");
        stringBuffer.append("  Identifier: ").append(otaMessage.getIdentifier()).append("\n");
        return stringBuffer.toString();
    }

    
    public String decodeMessage(RequestSystemInfoCommand command) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Request System Information").append("\n");
        stringBuffer.append("Command Type: Command\n");
        return stringBuffer.toString();
    }
    
    public String decodeMessage(RequestSystemInfoResponse response) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Request System Information").append("\n");

        if (response.isCommand() == false) {
            stringBuffer.append("         Error Code: ").append(response.getErrorCode()).append("\n");
        }
        stringBuffer.append("       Command Type: Response").append("\n");
        stringBuffer.append("        Platform ID: ").append(response.getPlatformId()).append("\n");
        stringBuffer.append("     Application ID: ").append(response.getApplicationId()).append("\n");
        stringBuffer.append("Application Version: ").append(response.getApplicationVersion()).append("\n");
        stringBuffer.append("  Application Build: ").append(response.getApplicationBuild()).append("\n");
        stringBuffer.append("             RTU ID: ").append(response.getRtuId()).append("\n");
        stringBuffer.append("        RTU Version: ").append(response.getRtuVersion()).append("\n");
        stringBuffer.append("         Network ID: ").append(response.getNetworkId()).append("\n");
        stringBuffer.append("    Network Version: ").append(response.getNetworkVersion()).append("\n");
        stringBuffer.append("         Flash Size: ").append(response.getCodeMapBytes().length).append("\n");
        stringBuffer.append("         Identifier: ").append(response.getIdentifier()).append("\n");
        if (response.getCodeMapBytes() != null) {
            String hexString = formattedBytes(response.getCodeMapBytes(), "           Code Map: ");
            stringBuffer.append(hexString);
        } else
            stringBuffer.append("           Code Map: ").append("None");
        return stringBuffer.toString();
    }

    public String decodeMessage(IoPointReportMessage<?> reportMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(reportTypeStringForReportMessage(reportMessage)).append("\n");
        stringBuffer.append(timestampStringForReportMessage(reportMessage)).append("\n");
        stringBuffer.append(reasonCodeStringForReportMessage(reportMessage)).append("\n");
        if (reportMessage instanceof ExtendedReportMessage)
            stringBuffer.append(valueTypeStringForReportMessage((ExtendedReportMessage)reportMessage)).append("\n");
        stringBuffer.append(pollsetIdStringForReportMessage(reportMessage)).append("\n");
        stringBuffer.append("\n");
        if (reportMessage.getReportItems().size() > 0) {
            stringBuffer.append(FORMAT_HEADER).append("\n");
            stringBuffer.append(FORMAT_ULINE).append("\n");
        }

        for (Iterator<? extends IoPointReportItem> iterator = reportMessage.getReportItems().iterator(); iterator.hasNext();)
            stringBuffer.append(stringForReportItem(iterator.next()));
        return stringBuffer.toString();
    }

    public String decodeMessage(CommandLineMessage message) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Display Message").append("\n");
        stringBuffer.append("Sequence No: ");
        stringBuffer.append(message.getCommandNumber()).append("\n");
        stringBuffer.append(message.getCommandLine()).append("\n");
        return stringBuffer.toString();
    }

    public String decodeMessage(CommandCompleteMessage message) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Command Complete Message").append("\n");
        stringBuffer.append("Sequence No: ");
        stringBuffer.append(message.getCommandNumber());
        return stringBuffer.toString();
    }

    public String decodeMessage(DisplayMessage displayMessage) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("Message Type: Display Message").append("\n");
        stringBuffer.append("Sequence No: ");
        stringBuffer.append(displayMessage.getCommandNumber()).append("\n");
        stringBuffer.append(displayMessage.getMessage());
        return stringBuffer.toString();
    }

    public String reportTypeStringForReportMessage(IoPointReportMessage<?> reportMessage) {
        if (reportMessage instanceof ExtendedReportMessage)
            return "Message Type: Extended IO Point Report";
        if (reportMessage instanceof StandardReportMessage)
            return "Message Type: Standard IO Point Report";
        return null;
    }

    public String timestampStringForReportMessage(IoPointReportMessage<?> reportMessage) {
        return "Timestamp   : " + dateFormat.format(reportMessage.getTimeStamp());
    }

    public String reasonCodeStringForReportMessage(IoPointReportMessage<?> reportMessage) {
        try {
            return "Reason code : " + reportMessage.getReasonCode();
        } catch (IllegalArgumentException e) {
        }
        return "Unknown reason code: " + reportMessage.getReasonCode();
    }

    public String valueTypeStringForReportMessage(ExtendedReportMessage reportMessage) {
        return "Value type  : " + reportMessage.getValueType().toString();
    }

    public String stringForReportItem(IoPointReportItem reportItem) {
        StringBuilder stringBuffer = new StringBuilder();
        String decimalFormatString;
        GdnValue<?> gdnValue = reportItem.getValue();
        if (gdnValue.isNumeric())
            decimalFormatString = decimalFormat.format((Number)gdnValue.getValue());
        else
        {
            decimalFormatString = gdnValue.toString();
            if (decimalFormatString == null)
                decimalFormatString = "null";
        }

        Integer index = new Integer(reportItem.getIndex());
        String valueName = gdnValue.dataType().getName();
        String ljGdnValueString = leftJustifiedPaddedString(valueName, 20);
        String rjPaddedString = rightJustifiedPaddedString(decimalFormatString, 17);
        GdnAlarmStatus alarmStatus = reportItem.getAlarmStatus();
        String lgIntegerString = leftJustifiedPaddedString(integerFormat.format(index), 6);
        Object[] args = new Object[]{lgIntegerString, ljGdnValueString, rjPaddedString, alarmStatus};
        stringBuffer.append(messageFormat.format(args)).append("\n");
        return stringBuffer.toString();
    }

    protected String rightJustifiedPaddedString(String aString, int aWidth) {
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = aString.length(); i < aWidth; i++)
            stringBuffer.append(' ');
        stringBuffer.append(aString);
        return stringBuffer.toString();
    }

    protected String leftJustifiedPaddedString(String aString, int aWidth) {
        StringBuilder stringBuffer = new StringBuilder(aString);
        for (int i = stringBuffer.length(); i < aWidth; i++)
            stringBuffer.append(' ');
        return stringBuffer.toString();
    }

    public String pollsetIdStringForReportMessage(IoPointReportMessage<?> reportMessage) {
        return "Pollset ID  : " + reportMessage.getPollSetNumber();
    }

    private String formattedBytes(byte[] bytes, String label) {
        StringBuilder stringBuilder = new StringBuilder(label);
        Formatter formatter = new Formatter(stringBuilder);
        int i = 0;
        while (i < bytes.length) {
            int j = 0;
            while (j++ < 40 && i < bytes.length)
                formatter.format("%02X ", bytes[i++]);
            if (i < bytes.length) {
                formatter.format("\n", (Object[])null);
                padSpaces(stringBuilder, label.length());
            }
        }
        formatter.close();
        return stringBuilder.toString();
    }

    private void padSpaces(StringBuilder builder, int fillCount) {
        for (int i = 0; i < fillCount; i++)
            builder.append(' ');
    }
}