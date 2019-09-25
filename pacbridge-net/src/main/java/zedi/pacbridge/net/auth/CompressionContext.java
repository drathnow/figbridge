package zedi.pacbridge.net.auth;

import zedi.pacbridge.net.CompressionType;

public class CompressionContext {

    private CompressionType compressionType;
    
    public CompressionContext(CompressionType compressionType) {
        this.compressionType = compressionType;
    }


    public CompressionType compressionType() {
        return compressionType;
    }
}
