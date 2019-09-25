package zedi.pacbridge.net.logging;

import java.net.InetSocketAddress;

import zedi.pacbridge.utl.HexStringEncoder;

/**
 * The LogMessageFormatter class formats incoming and outoging packets into messages that can
 * be logged to a file, console, or any other display device. Each logging line contains text
 * that indicate whether the packet is incoming or outgoing, the IP address, and the value of
 * the packet in hex.
 */
public class NetworkTrafficMessageFormatter {
    
    /**
     * A regular expression that can be used to parse a log message.  This RE does not
     * contain anything that pulls the timestamp as any logging timestamp is provided by the
     * underlying logging framework and could change depending on the logging configuration.
     */
    public static final String RECEIVED = "Received";
    public static final String SENDING = "Sending";
    public static final String DATE_RE = "^(\\d{4}-\\d{2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}).*";
    public static final String TRAFFIC_PARSING_RE = "^.*("
        + RECEIVED
        + "|"
        + SENDING
        + ") (\\d{1,}) .* (\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})/(.\\d*) \\((.*)\\).* (|.*$)";

    private StringBuffer stringBuffer = new StringBuffer();
    private String protocolName;

    private int networkNumber;

    public NetworkTrafficMessageFormatter(InetSocketAddress socketAddress, String protocolName) {
        this.networkNumber = 0;
        this.protocolName = protocolName;
    }

    public String formattedIncomingLine(String fromAddress, byte[] bytes, int offset, int length) {
        stringBuffer.setLength(0);
        stringBuffer.append("Received ");
        stringBuffer.append(length);
        stringBuffer.append(" bytes from ");
        stringBuffer.append(fromAddress);
        stringBuffer.append("/");
        stringBuffer.append(networkNumber);
        stringBuffer.append(" (");
        stringBuffer.append(protocolName);
        stringBuffer.append(") : ");
        stringBuffer.append(HexStringEncoder.bytesAsHexString(bytes, offset, length, '|'));
        return stringBuffer.toString();
    }

    public String formattedOutgoingLine(String toAddress, byte[] bytes, int offset, int length) {
        stringBuffer.setLength(0);
        stringBuffer.append("Sending ");
        stringBuffer.append(length);
        stringBuffer.append(" bytes to ");
        stringBuffer.append(toAddress);
        stringBuffer.append("/");
        stringBuffer.append(networkNumber);
        stringBuffer.append(" (");
        stringBuffer.append(protocolName);
        stringBuffer.append(") : ");
        stringBuffer.append(HexStringEncoder.bytesAsHexString(bytes, offset, length, '|'));
        return stringBuffer.toString();
    }

}
