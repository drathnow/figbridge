package zedi.pacbridge.gdn;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class GdnEmptyValueTest extends BaseTestCase {

    @Test
    public void testToString() {
        GdnEmptyValue emptyValue = new GdnEmptyValue();
        assertNull(emptyValue.toString());
    }
}
