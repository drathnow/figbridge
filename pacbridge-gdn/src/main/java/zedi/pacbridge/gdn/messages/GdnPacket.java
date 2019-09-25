package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.messages.otad.OtadMessage;
import zedi.pacbridge.net.Packet;
import zedi.pacbridge.net.PacketHeader;


public class GdnPacket implements Packet {
    protected SwtHeader header;
    protected GdnMessage message;
    
    public GdnPacket(SwtHeader header) {
        this.header = header;
    }
    
    public GdnPacket(SwtHeader header, GdnMessage message) {
        this(header);
        this.message = message;
    }

    public GdnMessage getMessage() {
        return message;
    }

    public void setMessage(GdnMessage message) {
        this.message = message;
    }

    @Override
    public PacketHeader getHeader() {
        return header;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        serializeToByteBuffer(header, message, byteBuffer);
    }

    @Override
    public boolean containsUnsolicitedMessage() {
        return header.supportsSession() == false;
    }

    public static void serializeToByteBuffer(SwtHeader header, GdnMessage message, ByteBuffer byteBuffer) {
        header.serialize(byteBuffer);
        message.serialize(byteBuffer);
    }
    
    public static GdnPacket packetFromBuffer(ByteBuffer byteBuffer) {
        SwtHeader header = SwtHeader.headerFromByteBuffer(byteBuffer);
        GdnMessage message;
        switch (header.messageType().getNumber()) {
            case GdnMessageType.NUMBER_FOR_EXTENDED_REPORT_MESSAGE :
                message = ExtendedReportMessage.extendedReportMessageFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_ADD_EXTENDED_IOPOINT_MESSAGE :
                message = AddExtendedIoPointControl.addExtendedIoPointControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_SET_EVENTS_RESPONSE_MESSAGE :
                message = SetEventsResponseMessage.setEventsResponseMessageFromByteBuffer(byteBuffer);
                break;

            case GdnMessageType.NUMBER_FOR_WRITE_IOPOINT_MESSAGE :
                message = WriteIoPointControl.writeIoPointControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_DISPLAY_MESSAGE_MESSAGE :
                message = DisplayMessage.displayMessageFromByteBuffer(byteBuffer);
                break;

            case GdnMessageType.NUMBER_FOR_COMMAND_LINE_MESSAGE:
                message = CommandLineMessage.commandLineMessageFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_COMMAND_COMPLETE_MESSAGE :
                message = CommandCompleteMessage.commandCompleteMessageFromByteBuffer(byteBuffer);
                break;
               
            case GdnMessageType.OTAD_MESSAGE_NUMBER :
                message = OtadMessage.otadMessageFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_DEMAND_POLL_MESSAGE:
                message = DemandPollControl.demandPollControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_SET_ALARMS_MESSAGE:
                message = ConfigureAlarmsControl.configureAlarmsControlFromByteBuffer(byteBuffer);
                break;
            
            case GdnMessageType.NUMBER_FOR_ADD_IOPOINT_MESSAGE:
                message = AddStandardIoPointControl.addStandardIoPointControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_SET_EVENT_MESSAGE:
                message = SetEventsControl.setEventsControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_DELETE_IOPOINT_MESSAGE:
                message = DeleteIoPointControl.deleteIoPointControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_SET_EXTENDED_ALARM_MESSAGE:
                message = ConfigureExtendedAlarmsControl.configureExtendedAlarmsControlFromByteBuffer(byteBuffer);
                break;
                
            case GdnMessageType.NUMBER_FOR_REQUEST_TIME_MESSAGE:
                message = RequestTimeMessage.RequestTimeMessageFromByteBuffer(byteBuffer);
                break;
                
            default:
                throw new UnsupportedOperationException("Message type not recognized: " + header.messageType().getNumber());
        }
        return new GdnPacket(header, message);
    }
    
}
