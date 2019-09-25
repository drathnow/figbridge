package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MockFadMessageTransmitter implements FadMessageTransmitter {

    public List<ByteBuffer> transmittedBuffers = new ArrayList<ByteBuffer>();

    @Override
    public void transmitByteBuffer(ByteBuffer byteBuffer) throws IOException {
        saveBufferToList(byteBuffer, transmittedBuffers);
    }

    private void saveBufferToList(ByteBuffer byteBuffer, List<ByteBuffer> bufferList) {
        ByteBuffer buffer = ByteBuffer.allocate(byteBuffer.remaining());
        buffer.put(byteBuffer);
        bufferList.add(buffer);
    }
}
