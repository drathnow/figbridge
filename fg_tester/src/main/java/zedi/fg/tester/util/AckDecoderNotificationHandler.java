package zedi.fg.tester.util;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ZapPacket;

public class AckDecoderNotificationHandler implements Notifiable
{
    private static final Logger traceLogger = Logger.getLogger(Constants.TRACE_LOGGER_NAME);

    private FieldTypeLibrary fieldTypeLibrary;
    
    public AckDecoderNotificationHandler(FieldTypeLibrary fieldTypeLibrary)
    {
        this.fieldTypeLibrary = fieldTypeLibrary;
    }

    @Override
    public void handleNotification(Notification notification)
    {
        TransmissionPackage transmissionPackage = notification.getAttachment();
        ByteBuffer byteBuffer = ByteBuffer.wrap(transmissionPackage.getBytes(), transmissionPackage.getOffset()+2, transmissionPackage.getLength()-2);
        
        ZapPacket packet = ZapPacket.packetFromByteBuffer(byteBuffer, fieldTypeLibrary);
        if (packet.getMessage().messageType() == ZapMessageType.Acknowledgement)
        {
            AckMessage ackMessage = (AckMessage)packet.getMessage();
            if (ackMessage.getAckedMessageType() == ZapMessageType.Configure)
            {
                ConfigureResponseAckDetails ackDetails = ackMessage.additionalDetails();
                List<Action> actions = ackDetails.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
                for (Action action : actions)
                {
                    traceLogger.trace("Action Type: " + action.getActionType().toString());
                    List<Field<?>> fields = action.getFields();
                    for (Field<?> field : fields)
                    {
                        if (field.getFieldType().getNumber() == 4)
                            traceLogger.trace("    " + field.getFieldType() + ": " + ZiosReturnCodes.stringForErrorCode((Number)field.getValue()));
                        else
                            traceLogger.trace("    " + field.getFieldType() + ": " + field.getValue());
                    }
                }
            }
        }

    }
}
