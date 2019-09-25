package zedi.pacbridge.utl.io;

import java.util.Random;

public class SaltGenerator {

    private static Random random = new Random(System.currentTimeMillis());
    
    public static byte[] generateSalt(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }
}
