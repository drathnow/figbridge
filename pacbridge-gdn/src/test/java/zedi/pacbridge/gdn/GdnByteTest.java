package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class GdnByteTest extends BaseTestCase {
    @Test
    public void testGetValue() {
        GdnByte gdnByte = new GdnByte(-1);
        assertEquals(-1, ((Integer)gdnByte.getValue()).intValue());
    }
    
    @Test
    public void testSetStringValue() {
        GdnByte gdnByte = new GdnByte("2");
        assertEquals(2,(((Integer)gdnByte.getValue()).intValue()),0.1);
    }
}