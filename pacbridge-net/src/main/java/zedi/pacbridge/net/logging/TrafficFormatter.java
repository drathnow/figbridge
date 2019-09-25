package zedi.pacbridge.net.logging;

import zedi.pacbridge.utl.HexStringEncoder;

public class TrafficFormatter {
    public static final String RECEIVED = "Received";
    public static final String SENDING = "Sending";
    private StringBuffer stringBuffer = new StringBuffer();

    public String formattedIncomingLine(byte[] bytes, int offset, int length) {
        return formattedString(RECEIVED, bytes, offset, length);
    }

    public String formattedOutgoingLine(byte[] bytes, int offset, int length) {
        return formattedString(SENDING, bytes, offset, length);
    }

    private String formattedString(String prefix, byte[] bytes, int offset, int length) {
        stringBuffer.setLength(0);
        stringBuffer.append(prefix);
        stringBuffer.append("(");
        stringBuffer.append(length);
        stringBuffer.append("): ");
        stringBuffer.append(HexStringEncoder.bytesAsHexString(bytes, offset, length, '|'));
        return stringBuffer.toString();
    }
}
