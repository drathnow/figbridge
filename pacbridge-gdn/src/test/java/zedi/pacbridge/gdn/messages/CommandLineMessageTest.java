package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class CommandLineMessageTest extends BaseTestCase {
    protected static final String TEST_STRING = "Hello World";

    @Test
    public void testConstructor() {
        CommandLineMessage commandLineMessage = new CommandLineMessage(TEST_STRING, 1);
        assertEquals(TEST_STRING, new String(commandLineMessage.asByteArray()));
    }

    @Test
    public void testGetCommand() {
        assertEquals(0, new CommandLineMessage(TEST_STRING, 0).getCommandNumber().intValue());
    }
}