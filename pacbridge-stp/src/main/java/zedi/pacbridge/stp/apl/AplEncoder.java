package zedi.pacbridge.stp.apl;

import java.nio.ByteBuffer;

import zedi.pacbridge.utl.crc.CheckSum;

public class AplEncoder  {

    private CheckSum checkSum;
    
    public AplEncoder() {
        this(new AplCheckSum());
    }
    
    AplEncoder(CheckSum checkSum) {
        this.checkSum = checkSum;
    }

    public void encodeDataFromSrcBufferToDstBuffer(ByteBuffer srcByteBuffer, ByteBuffer dstByteBuffer) {
        startPacket(dstByteBuffer);
        int length = srcByteBuffer.limit() - srcByteBuffer.position();
        byte cs = (byte)checkSum.calculatedChecksumForByteArray(srcByteBuffer.array(), srcByteBuffer.position(), length);
        while (srcByteBuffer.hasRemaining()) {
            byte nextByte = srcByteBuffer.get();
            addByteToByteBuffer(nextByte, dstByteBuffer);
        }
        endPacketWithChecksum(dstByteBuffer, cs);
    }
        
    private static void endPacketWithChecksum(ByteBuffer byteBuffer, byte checkSum) {
        addByteToByteBuffer(checkSum, byteBuffer);
        byteBuffer.put(Apl.ESC);
        byteBuffer.put(Apl.EOF);
    }

    private static void startPacket(ByteBuffer byteBuffer) {
        byteBuffer.clear();
        byteBuffer.put(Apl.ESC);
        byteBuffer.put(Apl.SOF);
    }
    
    private static void addByteToByteBuffer(byte theByte, ByteBuffer byteBuffer) {
        if (Apl.isFramingChar(theByte)) {
            byteBuffer.put((byte)Apl.ESC);
            byteBuffer.put((byte)Apl.escapeByteForByte(theByte));
        } else
            byteBuffer.put(theByte);
    }
}
