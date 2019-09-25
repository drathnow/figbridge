package zedi.pacbridge.utl.io;

public class Sha1Hasher extends MessageDigestHasher implements Hasher {
    
    public Sha1Hasher(int iterations) {
        super("SHA-1", iterations);
    }
}
