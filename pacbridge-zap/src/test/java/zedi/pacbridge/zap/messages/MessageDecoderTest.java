package zedi.pacbridge.zap.messages;

import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class MessageDecoderTest extends BaseTestCase {
   
    //                                |      pkt header    |  bndl header |   rptids  |v |  rpt id   | timestmp  |tcnt |rcnt |rc| pid |  index    |dt| temstamp  |rf|value|
    private static final String PKT1 = "01 00 06 00 00 00 01 01 00 01 00 01 00 01 17 89 01 00 01 17 89 53 34 4b 17 00 01 00 01 00 00 01 00 00 00 64 05 53 34 4b 17 00 00 7b";
    private static final String PKT2 = "|01|00|02|00|00|00|00|10|4A|68|17|1D|A9|F1|EF|6D|43|67|A9|59|DD|D7|9F|50|";
    private static final String PKT3 = "01 00 01 00 00 00 00 01 70 01 00 06 00 0f 01 6b 01 00 3c 00 00 00 01 00 01 00 00 00 02 00 01 00 00 00 03 00 01 00 00 00 04 00 01 00 00 00 05 00 01 00 00 00 06 00 01 00 00 00 07 00 01 00 00 00 08 00 01 00 00 00 09 00 01 00 00 00 0a 00 01 00 00 00 0b 00 01 00 00 00 0c 00 01 00 00 00 0d 00 01 00 00 00 0e 00 01 00 00 00 0f 00 01 00 00 00 11 00 01 00 00 00 10 00 01 00 00 00 13 00 01 00 00 00 12 00 01 00 00 00 15 00 01 00 00 00 14 00 01 00 00 00 17 00 01 00 00 00 16 00 01 00 00 00 19 00 01 00 00 00 18 00 01 00 00 00 1b 00 01 00 00 00 1a 00 01 00 00 00 1d 00 01 00 00 00 1c 00 01 00 00 00 1f 00 01 00 00 00 1e 00 01 00 00 00 22 00 01 00 00 00 23 00 01 00 00 00 20 00 01 00 00 00 21 00 01 00 00 00 26 00 01 00 00 00 27 00 01 00 00 00 24 00 01 00 00 00 25 00 01 00 00 00 2a 00 01 00 00 00 2b 00 01 00 00 00 28 00 01 00 00 00 29 00 01 00 00 00 2e 00 01 00 00 00 2f 00 01 00 00 00 2c 00 01 00 00 00 2d 00 01 00 00 00 33 00 01 00 00 00 32 00 01 00 00 00 31 00 01 00 00 00 30 00 01 00 00 00 37 00 01 00 00 00 36 00 01 00 00 00 35 00 01 00 00 00 34 00 01 00 00 00 3b 00 01 00 00 00 3a 00 01 00 00 00 39 00 01 00 00 00 38 00 01 00 00 00 3c 00 01 00 00 00 00 00 00 00 00 00";
    private static final String PKT4 = "01 00 04 00 00 01 0F 01 01 0F 00 01 00 00 0F F2 01 00 00 00 00 00 00 00 00 00 00 0F F2 57 E2 09 53 00 14 00 01 00 00 0C 00 01 86 EC 01 00 01 86 E8 08 00 01 86 E9 08 00 01 86 EA 08 00 01 86 EB 08 00 01 86 ED 01 00 01 86 EE 01 00 01 86 EF 01 00 01 86 F0 01 00 01 86 F3 01 00 01 86 F1 01 00 01 86 F2 01 00 01 86 F4 01 00 01 86 F5 01 00 01 86 F6 01 00 01 86 F7 01 00 01 86 F8 01 00 01 86 F9 01 00 01 86 FA 01 00 01 86 FB 01 57 E2 09 53 06 01 06 3F 8E 14 7B 06 3F 81 47 AE 06 3F 85 1E B8 06 3F 8C CC CD 06 01 06 01 06 00 06 01 06 01 06 00 06 01 06 00 06 00 06 01 06 00 06 01 06 01 06 01 06 01 00 C4 01 00 04 00 00 01 10 01 01 10 00 01 00 00 0F F2 01 00 00 00 00 00 00 00 00 00 00 0F F2 57 E2 09 53 00 14 00 01 00 00 0C 00 01 86 EC 01 00 01 86 E8 08 00 01 86 E9 08 00 01 86 EA 08 00 01 86 EB 08 00 01 86 ED 01 00 01 86 EE 01 00 01 86 EF 01 00 01 86 F0 01 00 01 86 F3 01 00 01 86 F1 01 00 01 86 F2 01 00 01 86 F4 01 00 01 86 F5 01 00 01 86 F6 01 00 01 86 F7 01 00 01 86 F8 01 00 01 86 F9 01 00 01 86 FA 01 00 01 86 FB 01 57 E2 09 53 06 01 06 3F 8E 14 7B 06 3F 81 47 AE 06 3F 85 1E B8 06 3F 8C CC CD 06 01 06 01 06 00 06 01 06 01 06 00 06 01 06 00 06 00 06 01 06 00 06 01 06 01 06 01 06 01";

    @Test
    public void shouldDecodePkt4() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(PKT4);
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.decodePacketBytes(bytes));
    }

    @Test
    public void shouldDecodePkt3() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(PKT3);
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.decodePacketBytes(bytes));
    }

    @Test
    @Ignore
    public void shouldDecodePkt2() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(PKT2);
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.decodePacketBytes(bytes));
    }
    
    @Test
    @Ignore
    public void shouldShouldDecodePkt1() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(PKT1);
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.decodePacketBytes(bytes));
    }
}
