package zedi.pacbridge.net;


public interface UpperLayer extends DataReceiver {
    public void setLowerLayer(LowerLayer lowerLayer);
}