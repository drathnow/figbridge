package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class StringPropertyReplacerTest extends BaseTestCase {

    @Test
    public void shouldReplaceProperty() throws Exception {
        System.setProperty("foo", "Bar");
        assertEquals("Bar.spooge", StringPropertyReplacer.replaceProperties("${foo}.spooge"));
    }
}
