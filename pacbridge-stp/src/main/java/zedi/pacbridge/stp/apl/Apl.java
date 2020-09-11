package zedi.pacbridge.stp.apl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.ByteBufferManager;
import zedi.pacbridge.net.FramingLayer;
import zedi.pacbridge.net.NetworkAdapter;
import zedi.pacbridge.net.ProtocolException;
import zedi.pacbridge.net.TransmitProtocolPacket;
import zedi.pacbridge.net.TransportLayer;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.crc.CheckSumException;

public class Apl implements FramingLayer {

    private static Logger logger = LoggerFactory.getLogger(Apl.class);
    
    // The min packet size is set to 8 because: 
    // 1. Min 4 bytes for start-of-frame and end-of-frame bytes.
    // 2. Min 2 bytes for the payload (incase the single byte is a framing char).
    // 3. Min 2 bytes for check sum byte, incase it needs to be escaped.
    public static final int MIN_FRAME_SIZE = 8;

    public static final byte ESC = 0x1b;
    public static final byte SOF = 0x02;
    public static final byte EOF = 0x03;
    public static final byte ESC_FOR_SOF = 0x04;
    public static final byte ESC_FOR_EOF = 0x05;
    public static final String MAX_PACKET_SIZE_PROPERTY_NAME = "apl.maxPacketSize";
    public static final int DEFAULT_MAX_PACKET_SIZE = 32768;
    public static final int MIN_MAX_PACKET_SIZE = 64;
    
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    
    public static final IntegerSystemProperty maxPacketSizeProperty = new IntegerSystemProperty(MAX_PACKET_SIZE_PROPERTY_NAME, DEFAULT_MAX_PACKET_SIZE, MIN_MAX_PACKET_SIZE, DEFAULT_MAX_PACKET_SIZE);
    
    private AplDecoder aplDecoder;
    private AplEncoder aplEncoder;
    private TransportLayer transportLayer;
    private NetworkAdapter networkAdapter;
    private int maxPacketSize;
    private ByteBuffer transmitBuffer;
    private ByteBufferManager byteBufferManager;
    
    public Apl() {
        this(new AplDecoder(), new AplEncoder(), new ByteBufferManager());
    }
    
    Apl(AplDecoder aplDecoder, AplEncoder aplEncoder, ByteBufferManager byteBufferManager) {
        this.aplDecoder = aplDecoder;
        this.aplEncoder = aplEncoder;
        this.maxPacketSize = maxPacketSizeProperty.currentValue();
        this.byteBufferManager = byteBufferManager;
        this.transmitBuffer = byteBufferManager.allocateByteBufferWithSize(DEFAULT_BUFFER_SIZE);
    }

    public void setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }
    
    @Override
    public boolean isActive() {
        return false;
    }
    
    @Override
    public void close() {
        networkAdapter.close();
        aplDecoder.reset();
    }

    @Override
    public void receive(ByteBuffer byteBuffer) throws ProtocolException {
        try {
            aplDecoder.decodeBytesFromByteBuffer(byteBuffer);
        } catch (CheckSumException e) {
            logger.error("Unable to decode bytes from message.", e);
        }

        byte[] nextMessage = null;
//        while ((nextMessage = aplDecoder.nextMessage()) != null)
//            transportLayer.handleReceivedData(ByteBuffer.wrap(nextMessage).order(ByteOrder.LITTLE_ENDIAN));
    }

    public static boolean isFramingChar(int aByte) {
        switch (aByte) {
            case EOF :
            case SOF :
            case ESC :
                return true;
            default :
                return false;
        }
    }
    
    public static byte escapeByteForByte(byte aByte) {
        switch (aByte) {
            case Apl.EOF :
                return Apl.ESC_FOR_EOF;
            case Apl.SOF :
                return Apl.ESC_FOR_SOF;
            case Apl.ESC :
                return Apl.ESC;
        }
        throw new IllegalArgumentException("Byte is not an escapable byte: 0x" + Integer.toHexString(aByte & 0xFF));
    }

    @Override
    public void setNetworkAdapter(NetworkAdapter adapter) {
        this.networkAdapter = adapter;
    }

    @Override
    public void start() throws IOException {
        networkAdapter.start();
    }

    @Override
    public void reset() {
    }

    @Override
    public void setTransportLayer(TransportLayer transportLayer) {
        this.transportLayer = transportLayer;
    }

    @Override
    public void transmit(TransmitProtocolPacket protocolPacket) throws IOException
    {
        // TODO Auto-generated method stub
        
    }

}
