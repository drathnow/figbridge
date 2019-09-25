import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.zap.messages.ZapMessageDecoder;


public class DecodePacket {

    private static final String PKT = "|00|1E|01|00|06|FF|F4|00|01|00|00|00|00|00|9F|59|25|00|00|00|01|86|AA|00|84|00|01|00|00|00|00|00|";
    
    public static void main(String[] args) {
        ZapMessageDecoder decoder = new ZapMessageDecoder();
        System.out.println(decoder.decodePacketBytes(HexStringDecoder.hexStringAsBytes(PKT)));
    }
}
