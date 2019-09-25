package zedi.pacbridge.net;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ProtocolStackTest extends BaseTestCase {

    @Test
    public void shouldWireUpStack() throws Exception {
        SessionLayer sessionManager = mock(SessionLayer.class);
        PacketLayer packetLayer = mock(PacketLayer.class);
        CompressionLayer compressionLayer = mock(CompressionLayer.class);
        SecurityLayer securityLayer = mock(DefaultSecurityLayer.class);
        FramingLayer framingLayer = mock(FramingLayer.class);
        NetworkAdapter networkAdapter = mock(NetworkAdapter.class);
    }
}
