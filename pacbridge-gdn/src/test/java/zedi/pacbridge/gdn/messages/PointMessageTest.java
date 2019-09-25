package zedi.pacbridge.gdn.messages;

import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.test.BaseTestCase;


public abstract class PointMessageTest extends BaseTestCase {
    protected ByteArrayOutputStream byteArrayOutputStream;

    public static final Integer INDEX = 10;
    public static final Integer POLLSETID = 1;
    public static final Integer FIELD1 = 111;
    public static final Integer FIELD2 = 222;
    public static final Integer FIELD3 = 333;
    public static final Integer FIELD4 = 444;
    public static final Integer ADDRESS = 12325;
    public static final Integer TYPE = GdnDataType.Binary.getNumber();
    public static final Float TEST_FLOAT_VALUE = (float)1.22;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
        byteArrayOutputStream.reset();
        byteArrayOutputStream = null;
        super.tearDown();
    }
}
