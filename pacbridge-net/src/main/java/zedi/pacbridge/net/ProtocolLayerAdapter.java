package zedi.pacbridge.net;

import java.io.IOException;
import java.nio.ByteBuffer;


public class ProtocolLayerAdapter implements LowerLayer, UpperLayer, Tappable {

    @Override
    public void setUpperLayer(UpperLayer iProtocolLayer) {
    }

    @Override
    public void setLowerLayer(LowerLayer iProtocolLayer) {
    }

    @Override
    public void transmitData(ByteBuffer byteBuffer) throws IOException {
    }

    @Override
    public void handleReceivedData(ByteBuffer byteBuffer) throws ProtocolException {
    }

    @Override
    public void close() {
    }

    @Override
    public void addLayerTap(LayerTap protocolTap) {
    }
}
