package zedi.pacbridge.utl.io;

public interface Hasher {
    public void update(byte[] bytes);
    public void reset();
    public byte[] hashedValue();
    public byte[] hashedValue(int length);
}