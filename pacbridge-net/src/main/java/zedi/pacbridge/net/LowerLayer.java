package zedi.pacbridge.net;


public interface LowerLayer extends DataTransmitter {
    public void setUpperLayer(UpperLayer upperLayer);
}
