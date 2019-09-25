package zedi.pacbridge.net.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class NetworkTrafficMessageFormatterTest {

    private static final byte[] TEST_BYTES = new byte[]{0x01, 0x02, 0x03};
    private static final int NETWORK_NUMBER = 1;
    private static final String TCP = "TCP";
    private static final String ADDRESS = "1.2.3.4";
    private static final Pattern LOG_PATTERN = Pattern.compile(NetworkTrafficMessageFormatter.TRAFFIC_PARSING_RE);
    
    @Test
    public void shouldFormatIncomingTrafficLine() {
        InetSocketAddress siteAddress = InetSocketAddress.createUnresolved(ADDRESS, 0);
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_BYTES);
        ByteBuffer savedBuffer = byteBuffer.slice();
        NetworkTrafficMessageFormatter formatter = new NetworkTrafficMessageFormatter(siteAddress, TCP);
        
        String line = formatter.formattedOutgoingLine(ADDRESS, byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        
        assertEquals(savedBuffer.position(), byteBuffer.position());
        assertEquals(savedBuffer.limit(), byteBuffer.limit());
        assertEquals(savedBuffer.capacity(), byteBuffer.capacity());

        Matcher matcher = LOG_PATTERN.matcher(line);
        
        assertTrue(matcher.matches());
        assertEquals(6, matcher.groupCount());
        assertEquals(NetworkTrafficMessageFormatter.SENDING, matcher.group(1));
        assertEquals("3", matcher.group(2));
        assertEquals(ADDRESS, matcher.group(3));
        assertEquals(""+0, matcher.group(4));
        assertEquals(TCP, matcher.group(5));
        assertEquals("|01|02|03|", matcher.group(6));

    }
    
    @Test
    public void shouldFormatOutgoingTrafficLine() {
        InetSocketAddress siteAddress = InetSocketAddress.createUnresolved(ADDRESS, 0);
        ByteBuffer byteBuffer = ByteBuffer.wrap(TEST_BYTES);
        ByteBuffer savedBuffer = byteBuffer.slice();
        NetworkTrafficMessageFormatter formatter = new NetworkTrafficMessageFormatter(siteAddress, TCP);
        
        String line = formatter.formattedIncomingLine(ADDRESS, byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        
        assertEquals(savedBuffer.position(), byteBuffer.position());
        assertEquals(savedBuffer.limit(), byteBuffer.limit());
        assertEquals(savedBuffer.capacity(), byteBuffer.capacity());

        Matcher matcher = LOG_PATTERN.matcher(line);
        
        assertTrue(matcher.matches());
        assertEquals(6, matcher.groupCount());
        assertEquals(NetworkTrafficMessageFormatter.RECEIVED, matcher.group(1));
        assertEquals("3", matcher.group(2));
        assertEquals(ADDRESS, matcher.group(3));
        assertEquals(""+0, matcher.group(4));
        assertEquals(TCP, matcher.group(5));
        assertEquals("|01|02|03|", matcher.group(6));
    }
}
