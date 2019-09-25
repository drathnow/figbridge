package zedi.pacbridge.utl.io;


public class Md5Hasher extends MessageDigestHasher implements Hasher{
    
    public Md5Hasher(int iterations) {
        super("MD5", iterations);
    }
}
