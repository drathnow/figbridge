package zedi.pacbridge.net;

import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Region;

public abstract class ProtocolPacketBase {
    protected Region header;
    protected Region body;
    protected Region trailer;
    
    private int initBodyOffset;
    private int initBodyLength;
    private byte[] buffer;
    private boolean expandable;
    private Integer initBufferSize;
    
    private ProtocolPacketBase(byte[] buffer, int bodyOffset, int bodyLength, boolean expandable) {
        this.buffer = buffer;
        this.header = new Region(bodyOffset, 0);
        this.body = new Region(bodyOffset, bodyLength);
        this.trailer = new Region(bodyOffset+bodyLength, 0);
        this.initBodyOffset = bodyOffset;
        this.initBodyLength = bodyLength;
        this.expandable = expandable;
        this.initBufferSize = buffer.length;
    }
    
    public ProtocolPacketBase(byte[] buffer, int bodyOffset, int bodyLength) {
        this(buffer, bodyOffset, bodyLength, false);
    }
 
    public ProtocolPacketBase(int maxPacketSize, int bodyOffset, int bodyLength) {
        this(new byte[maxPacketSize], bodyOffset, bodyLength, true);
    }

    public ByteBuffer headerByteBuffer() {
        return ByteBuffer.wrap(buffer, header.offset(), header.length());
    }
    
    public ByteBuffer bodyByteBuffer() {
        return ByteBuffer.wrap(buffer, body.offset(), body.length());
    }
    
    public ByteBuffer trailerByteBuffer() {
        return ByteBuffer.wrap(buffer, trailer.offset(), trailer.length());
    }
    
    public Integer packetSize() {
        return buffer.length;
    }
    
    public void setBodyLength(int bodyLength) {
        body.setLength(bodyLength);
        header.setOffset(body.offset());
        header.setLength(0);
        trailer.setOffset(body.offset()+bodyLength);
        trailer.setLength(0);
    }

    public Integer bodyLength() {
        return body.length();
    }

    public void reset() {
        header.setOffset(initBodyOffset);
        header.setLength(0);
        body.setOffset(initBodyOffset);
        body.setLength(initBodyLength);
        trailer.setOffset(initBodyOffset+initBodyLength);
        trailer.setLength(0);
        if (buffer.length != initBufferSize)
            buffer = new byte[initBufferSize];
    }

    public void expand() {
        if (expandable == false)
            throw new UnsupportedOperationException("You cannot expand a wrapped buffer");
        buffer = new byte[buffer.length*2];
        header.setOffset(header.offset()*2);
        header.setLength(0);
        body.setOffset(body.offset()*2);
        body.setLength(body.length()*2);
        trailer.setOffset(body.offset()+body.length());
    }
    
    // for testing
    byte[] buffer() {
        return buffer;
    }
}
