package zedi.pacbridge.utl.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringEncoder;

public class Sha1HasherTest extends BaseTestCase {

    private static final byte[] EXPECTED_HASH1 = new byte[]{(byte)(byte)0x31, (byte)(byte)0xBB, (byte)(byte)0x21, (byte)(byte)0x0C, (byte)(byte)0xC0, (byte)(byte)0x02, (byte)(byte)0xCD, (byte)(byte)0xBD, (byte)(byte)0x5F, (byte)(byte)0x2C, (byte)(byte)0xAE, (byte)(byte)0xFE, (byte)(byte)0x71, (byte)(byte)0x7D, (byte)(byte)0x61, (byte)(byte)0x1D, (byte)(byte)0x40, (byte)(byte)0xC1, (byte)(byte)0xDE, (byte)(byte)0x05};
    private static final String STRING1 = "Hello";
    private static final String STRING2 = "World";
    
//    private static byte[] USERNAME = {(byte)0x64, (byte)0x65, (byte)0x76, (byte)0x69, (byte)0x63, (byte)0x65, (byte)0x31}; 
//    private static byte[] CLIENT_SALT = {(byte)0xf7, (byte)0x98, (byte)0xf0, (byte)0xef, (byte)0x88, (byte)0x57, (byte)0x4d, (byte)0xd1, (byte)0xfe, (byte)0x97, (byte)0x9b, (byte)0x09, (byte)0xf3, (byte)0xbb, (byte)0x0b, (byte)0x81};
//    private static byte[] SERVER_SALT = {(byte)0x0a, (byte)0x69, (byte)0x23, (byte)0x14, (byte)0x6f, (byte)0xb7, (byte)0x64, (byte)0x08, (byte)0x79, (byte)0xd5, (byte)0xf6, (byte)0x4a, (byte)0x98, (byte)0x8d, (byte)0xc1, (byte)0xda};
//    private static byte[] EXPECTED_HASH2 = {(byte)0xb9, (byte)0xcc, (byte)0xca, (byte)0xfc, (byte)0x02, (byte)0xaa, (byte)0xe3, (byte)0x28, (byte)0x6a, (byte)0x20, (byte)0x07, (byte)0x2e, (byte)0x8a, (byte)0xbb, (byte)0xbd, (byte)0xcb};
//
//    @Test
//    public void shouldGenerateHash2() throws Exception {
//        Sha1Hasher hasher = new Sha1Hasher(1024); 
//        hasher.update(USERNAME);
//        hasher.update(CLIENT_SALT);
//        hasher.update(SERVER_SALT);
//        byte[] hashedValue = hasher.hashedValue();
//        
//        System.out.println("Final Hash: " + HexStringEncoder.bytesAsHexString(hashedValue));
//        assertEquals(20, hashedValue.length);
//        assertTrue(Arrays.equals(EXPECTED_HASH2, hashedValue));
//
//    }
//    
    @Test
    public void shouldGenerateHash1() throws Exception {
        System.out.println("Hello Hex: " + HexStringEncoder.bytesAsHexString(STRING1.getBytes()));
        System.out.println("World Hex: " + HexStringEncoder.bytesAsHexString(STRING2.getBytes()));

        Sha1Hasher hasher = new Sha1Hasher(100);
        hasher.update(STRING1.getBytes());
        hasher.update(STRING2.getBytes());
        byte[] hashedValue = hasher.hashedValue();
        
        assertEquals(20, hashedValue.length);
        assertTrue(Arrays.equals(EXPECTED_HASH1, hashedValue));
    }
}
