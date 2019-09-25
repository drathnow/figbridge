package zedi.pacbridge.net;

/**
 * A {@code PacketEncoder} is used by the {@link PacketLayer} to transform {@link Message} objects
 * into {@link Packet} objects. 
 * <p>
 * Because {@code PacketEncoder} objects could be reused, it is important that they not maintain
 * any kind of state information.
 */
public interface PacketEncoder {
    Packet packetForMessageAndSession(Message message, Session session);
    Packet packetForMessage(Message message);
}
