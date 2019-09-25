package zedi.pacbridge.gdn;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class GdnValueTest extends BaseTestCase {

    @Test
    public void testTypeNumberForSerialization() {
        GdnValue<?> gdnValue = new GdnFloat((float)1.0);
        gdnValue.setInternal(true);
    }
}
