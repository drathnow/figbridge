package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;


public class CommandLineParserTest {

    @Test
    public void testArgumentsFromCommandLine() throws Exception {
        String[] args = new String[]{"--foo=bar", "--spooge"};
        
        Properties properties = CommandLineParser.argumentsFromCommandLine(args);
        
        assertTrue(properties.containsKey("foo"));
        assertEquals("bar", properties.get("foo"));
        assertTrue(properties.containsKey("spooge"));
        assertEquals("", properties.get("spooge"));
    }
}
