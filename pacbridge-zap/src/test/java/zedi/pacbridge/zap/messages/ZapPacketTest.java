package zedi.pacbridge.zap.messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class ZapPacketTest extends BaseTestCase {
    
    private static final String PKT_STRING1 = " 01 00 05 00 00 00 01 00 01 00 01 00 00 04 C7 00 1C 01 00 00 04 C7 52 D0 40 94 00 01 00 01 00 00 01 00 00 00 64 05 52 ";
    private static final String PKT_STRING2 = "01 00 04 00 00 00 02 01 00 02 00 01 00 00 00 00 00 00 00 00 00 00 00 0a 01 00 00 00 00 00 00 00 00 00 00 00 0a 53 f3 c5 56 00 05 00 01 00 00 0a 00 00 00 6e 01 00 00 00 6f 08 00 00 01 f6 07 00 00 01 f5 08 00 00 02 59 07 53 f3 c5 56 00 01 00 3f 80 00 00 05 00 00 00 00 05 00";
    private static final String PKT_STRING3 = " 01 00 04 00 00 00 07 01 00 07 00 01 00 00 00 64 01 00 00 00 00 00 82 2E 30 00 00 00 64 56 D0 7D E7 00 21 00 01 01 00 01 00 00 00 02 0A 00 00 00 0A 08 00 00 00 0B 08 00 01 4C 08 0A 00 00 00 03 0A 00 00 00 04 0A 00 00 00 01 0A 00 01 4C 09 0A 00 01 4C 0A 0A 00 01 73 2C 0A 00 01 73 1A 0A 00 01 73 1B 0A 00 01 73 2D 0A 00 01 73 18 0A 00 01 73 19 0A 00 01 73 1C 0A 00 01 73 1D 0A 00 01 73 1E 0A 00 01 73 1F 0A 00 01 73 20 08 00 01 73 21 08 00 01 73 22 0A 00 01 73 23 0A 00 01 73 25 03 00 01 73 26 03 00 01 73 27 0A 00 01 73 28 0A 00 01 73 29 0A 00 01 73 2A 0A 00 01 73 2B 0A 00 01 73 7C 0A 00 01 73 E0 0A 00 01 73 E1 03 56 D0 7D E7 00 00 06 20 31 2E 30 2E 35 00 41 3A EA 00 00 41 D8 00 00 00 00 0B 5A 49 4F 31 36 30 37 30 31 36 36 00 00 0B 4D 41 42 31 36 30 36 30 30 38 33 00 00 03 32 2E 32 00 00 0A 5A 46 47 30 30 30 30 35 35 38 00 00 03 32 2E 30 00 00 04 49 4F 2D 31 00 00 0B 4D 4F 42 31 36 30 36 30 30 32 36 00 00 09 31 32 2E 30 30 2E 33 32 34 00 00 0F 33 30 32 36 39 30 30 30 30 30 32 35 32 30 38 00 00 03 31 2E 33 00 00 0F 33 35 33 38 33 36 30 35 35 34 32 30 36 39 39 00 00 09 48 45 39 31 30 2D 4E 41 44 00 00 14 38 39 33 30 32 36 39 30 32 30 31 30 30 30 32 35 32 30 38 33 00 00 04 42 65 6C 6C 00 00 00 00 00 01 31 00 C2 8E 00 00 00 3F 4C CC CD 00 00 10 7A 65 64 69 2E 62 65 6C 6C 2E 63 61 2E 6D 32 6D 00 00 00 00 00 00 02 00 00 0D 31 30 2E 32 35 2E 31 2E 33 33 2F 32 34 00 00 00 00 00 01 35 00 00 01 33 00 00 0B 31 30 2E 37 31 2E 31 37 2E 31 36 00 00 11 39 38 3A 30 32 3A 64 38 3A 34 30 3A 30 33 3A 30 64 00 00 0A 31 30 2E 32 35 2E 31 2E 33 33 00 01 ";
    private static final String PKT_STRING4 = "01 00 04 00 00 03 B5 01 03 B5 00 01 00 00 03 C6 01 00 00 00 00 00 00 00 00 00 00 03 C6 57 A0 1D A3 00 18 00 01 00 00 0C 00 01 86 A0 08 00 01 86 A1 08 00 01 86 A2 08 00 01 86 A3 08 00 01 86 A4 01 00 01 86 A5 01 00 01 86 AE 01 00 01 86 A6 01 00 01 86 A7 01 00 01 86 A8 01 00 01 86 A9 01 00 01 86 AA 01 00 01 86 AB 01 00 01 86 AC 01 00 01 86 AD 01 00 01 86 AF 01 00 01 86 B0 01 00 01 86 B1 01 00 01 86 B2 01 00 01 86 B3 01 00 01 86 B4 01 00 01 86 B5 01 00 01 86 B6 01 00 01 86 B7 01 57 A0 1D A3 00 3F 6B 85 1F 00 3F 87 AE 14 00 3F 88 F5 C3 00 3F 81 47 AE 00 01 00 01 00 00 00 01 00 01 00 01 00 00 00 01 00 01 00 00 00 01 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 00 01"; 

    @Ignore
    @Test
    public void shouldReadFileAndDeserialize() throws Exception {
        File file = new File("d:/temp/foo.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String nextLine;
        
        while ((nextLine = reader.readLine()) != null) {
            nextLine = nextLine.replaceAll("^.*: ", "").replace(" ", " ");
            
            byte[] bytes = HexStringDecoder.hexStringAsBytes(nextLine);
            ZapPacket packet = ZapPacket.packetFromByteBuffer(ByteBuffer.wrap(bytes, 2, bytes.length-2));
            
            ZapMessageDecoder decoder = new ZapMessageDecoder();
            System.out.println(decoder.formattedMessage(packet.getMessage()));
        }
    }
    
    @Ignore
    @Test
    public void shouldDeserialize1() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(PKT_STRING4);
        ZapPacket packet = ZapPacket.packetFromByteBuffer(ByteBuffer.wrap(bytes));
        
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.formattedMessage(packet.getMessage()));
    }

    private static final String ACK_STRING = "01 FF FF 00 00 00 01 00 07 01 00 0B 00 01 00 02 09 01"; 
    
    @Test
    public void shouldDeserializeConfigureUpdateAckDetails() 
    {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(ACK_STRING);
        ZapPacket packet = ZapPacket.packetFromByteBuffer(ByteBuffer.wrap(bytes));
        
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.formattedMessage(packet.getMessage()));
        
    }
}
