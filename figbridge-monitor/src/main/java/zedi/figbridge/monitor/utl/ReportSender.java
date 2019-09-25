package zedi.figbridge.monitor.utl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.messaging.ReportToSiteReportConverter;
import zedi.pacbridge.app.messaging.SiteReport;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.AckMessage;
import zedi.pacbridge.zap.messages.BundledReportAckDetails;
import zedi.pacbridge.zap.messages.BundledReportMessage;
import zedi.pacbridge.zap.messages.SessionHeader;
import zedi.pacbridge.zap.messages.ZapPacket;
import zedi.pacbridge.zap.messages.ZapPacketHeader;
import zedi.pacbridge.zap.reporting.ResponseStatus;
import zedi.pacbridge.zap.reporting.ZapReport;


public class ReportSender implements LastErrorable {
    private static final Logger logger = LoggerFactory.getLogger(ReportSender.class.getName());
    
    private String lastError;
    private Long timestamp;
    private CountedBytePacketReader packetReader;
    private CountedBytePacketWriter packetWriter;
    
    public ReportSender(CountedBytePacketReader packetReader, CountedBytePacketWriter packetWriter) {
        this.packetReader = packetReader;
        this.packetWriter = packetWriter;
    }
    
    @Override
    public String getLastErrorText() {
        return lastError;
    }

    public Long getTimestamp() {
        return timestamp;
    }
    
    public boolean didSendReportWithMessageGenerator(BundledReportMessageGenerator messageGenerator) throws IOException {
        byte[] buffer = new byte[128];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        timestamp = System.currentTimeMillis();
        BundledReportMessage message = messageGenerator.buildBundledReportMessage();
        ZapPacketHeader header = new SessionHeader(message.messageType());
        ZapPacket packet = new ZapPacket(header, message);
        packet.serialize(byteBuffer);
        packetWriter.sendBytes(buffer, byteBuffer.position());
        int length = packetReader.lengthOfNextPacket(buffer);
        if (length == 0) {
            lastError = "No ACK recieved after sending message";
            logger.debug(lastError);
            return false;
        }
        
        byteBuffer = ByteBuffer.wrap(buffer, 0, length);
        packet = ZapPacket.packetFromByteBuffer(byteBuffer);
        if (packet.getMessage().messageType() != ZapMessageType.Acknowledgement) {
            lastError = "Wrong response! Expected Acknowledgement but received " + packet.getMessage().messageType().getName();
            logger.debug(lastError);
            return false;
        }
        
        AckMessage ack = (AckMessage)packet.getMessage();
        BundledReportAckDetails details = ack.additionalDetails();
        Map<Integer, ResponseStatus> statusMap = details.getStatusMap();
        if (statusMap.containsKey(messageGenerator.getReportId()) == false) {
            lastError = "ACK did not contain our ReportId";
            logger.debug(lastError);
            return false;
        }
        
        if (statusMap.get(messageGenerator.getReportId()) != ResponseStatus.OK) {
            if (statusMap.get(messageGenerator.getReportId()) == ResponseStatus.TransientError)
                lastError = "Bridge was unable to publish to message queue";
            else
                lastError = "Bridge was unable to decode report";
            logger.debug(lastError);
            return false;
        }
        return true;
    }
}
