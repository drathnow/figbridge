package zedi.pacbridge.net;

import java.nio.ByteBuffer;

/**
 * The <code>Packet</code> class abstracts the basic funtions of a network packet. Packets contain a packet
 * header and message portion. The header  
 *
 */
public interface Packet {
    public boolean containsUnsolicitedMessage();
    public Message getMessage();
    public PacketHeader getHeader();
    public void serialize(ByteBuffer buffer);
}
