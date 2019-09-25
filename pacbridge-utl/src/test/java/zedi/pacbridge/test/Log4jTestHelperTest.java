package zedi.pacbridge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Log4jTestHelperTest extends BaseTestCase {
    private static final Logger logger = Logger.getLogger(Log4jTestHelperTest.class);
    private Log4jTestHelper log4jFixture;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        log4jFixture = new Log4jTestHelper(Log4jTestHelperTest.class);
        log4jFixture.setUp();
        log4jFixture.setMinLevel(Level.INFO);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        log4jFixture.tearDown();
        log4jFixture = null;
        super.tearDown();
    }

    @Test
    public void testCapturesMessages() {
        assertEquals(0, log4jFixture.infoMessages.size());
        String testMessage = "hello world";
        logger.info(testMessage);
        assertTrue(log4jFixture.infoMessages.contains(testMessage));
    }

    @Test
    public void testWasMessageLogged() {
        logger.info("hello world 1");
        assertFalse(log4jFixture.wasMessageLogged("hello world 2"));
        logger.info("hello world 2");
        logger.info("hello world 3");
        assertTrue(log4jFixture.wasMessageLogged("hello world 2"));
    }

    @Test
    public void testWasMessageWithSubstringLogged() {
        logger.info("hello world 1");
        assertFalse(log4jFixture.wasMessageWithSubstringLogged("world 2"));
        logger.info("hello world 2");
        logger.info("hello world 3");
        assertTrue(log4jFixture.wasMessageWithSubstringLogged("world 2"));
    }
}