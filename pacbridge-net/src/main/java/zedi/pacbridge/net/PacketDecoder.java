package zedi.pacbridge.net;

import java.nio.ByteBuffer;


/**
 * A {@code PacketDecoder} is used by the {@link PacketLayer} to transform a stream of bytes 
 * into {@link Packet} objects. 
 * <p>
 * Because {@code PacketDecoder} objects could be reused, it is important that they not maintain
 * any kind of state information.
 */
public interface PacketDecoder {
    Packet packetForByteBuffer(ByteBuffer byteBuffer);
}
