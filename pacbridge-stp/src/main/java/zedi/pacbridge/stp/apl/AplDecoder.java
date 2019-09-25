package zedi.pacbridge.stp.apl;

import java.nio.ByteBuffer;
import java.util.Formatter;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ExpandableByteBuffer;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.crc.CheckSum;
import zedi.pacbridge.utl.crc.CheckSumException;

public class AplDecoder {
    private static final Logger logger = LoggerFactory.getLogger(AplDecoder.class.getName());
    private static final int DEFAULT_MAX_PACKET_SIZE = 1024;
    private static final int DEFAULT_BUFFER_EXPANSION_SIZE = 256;
    
    private State currentState;
    protected ReadState readState = new ReadState();
    protected EscapeState escapeState = new EscapeState();
    protected SearchingForEscState searchingForEscState = new SearchingForEscState();
    protected ExpectingSofState expectingSofState = new ExpectingSofState();
    protected int lastByte;
    private CheckSum checkSum;
    private ExpandableByteBuffer expandableByteBuffer;
    
    private LinkedList<byte[]> messages = new LinkedList<byte[]>();
    
    public AplDecoder() {
        this(new AplCheckSum());
    }
    
    AplDecoder(CheckSum checkSum) {
        this.checkSum = checkSum;
        this.currentState = searchingForEscState;
        this.expandableByteBuffer = new ExpandableByteBuffer(DEFAULT_MAX_PACKET_SIZE, DEFAULT_BUFFER_EXPANSION_SIZE);
    }

    public void decodeBytesFromByteBuffer(ByteBuffer byteBuffer) throws CheckSumException {
        while (byteBuffer.hasRemaining())
            currentState.add(byteBuffer.get());
    }
    
    public byte[] nextMessage() {
        if (messages.isEmpty())
            return null;
        byte[] message = messages.removeFirst();
        if (logger.isTraceEnabled())
            logger.trace("(APL) Msg: " + HexStringEncoder.bytesAsHexString(message));
        return message;
    }
    
    public void reset() {
        this.currentState = searchingForEscState;
        this.expandableByteBuffer.reset();
    }
    
    private interface State {
        public void add(byte aByte) throws CheckSumException;
    }
    
    private class SearchingForEscState implements State {
        
        public void add(byte aByte) throws CheckSumException {
            if (aByte == Apl.ESC)
                currentState = expectingSofState;
        }
    }

    private class ExpectingSofState implements State {
        public void add(byte aByte) throws CheckSumException {
            if (aByte == Apl.SOF) {
                currentState = readState;
            } else
                currentState = searchingForEscState;
        }
    }

    private class ReadState implements State {
        public void add(byte aByte) throws CheckSumException {
            if (aByte == Apl.ESC)
                currentState = escapeState;
            else
                addByteToOutputBuffer(aByte);
        }
    }

    
    private class EscapeState implements State {
        
        public void add(byte theByte) throws CheckSumException {
            switch (theByte) {
                case Apl.ESC_FOR_SOF:
                    addByteToOutputBuffer(Apl.SOF);
                    currentState = readState;
                    break;
                    
                case Apl.ESC:
                    addByteToOutputBuffer(theByte);
                    currentState = readState;
                    break;
                    
                case Apl.ESC_FOR_EOF:
                    addByteToOutputBuffer(Apl.EOF);
                    currentState = readState;
                    break;
                    
                case Apl.EOF:
                    finish();
                    
                default:
                    currentState = searchingForEscState;
                    break;
                }
        }
    }
    protected void addByteToOutputBuffer(final byte currentByte) {
        expandableByteBuffer.write((byte)currentByte);
        lastByte = currentByte;
    }

    protected void finish() throws CheckSumException {
        currentState = searchingForEscState;
        byte[] message = new byte[expandableByteBuffer.getSize()-1];
        expandableByteBuffer.copyBytesToDestinationByteArray(message);
        expandableByteBuffer.clear();
        int calculatedChecksum = checkSum.calculatedChecksumForByteArray(message);
        int expectedChecksum = lastByte & 0xFF;
        if (expectedChecksum != calculatedChecksum) {
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder);
            formatter.format("Invalid checksum calculated. Expecting 0x%2X but was 0x%2X", expectedChecksum, calculatedChecksum);
            formatter.close();
            currentState = searchingForEscState;
            throw new CheckSumException(stringBuilder.toString());
        }
        messages.addLast(message);
    }
}