package zedi.pacbridge.net;

import java.nio.ByteBuffer;

public interface PacketSerializer {
    public void serializeMessageToByteBuffer(Message message, Session session, ByteBuffer transmitByteBuffer);
    public Packet deserializePacketFromByteBuffer(ByteBuffer byteBuffer);
}
