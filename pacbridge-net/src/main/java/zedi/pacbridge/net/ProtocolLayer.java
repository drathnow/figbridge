package zedi.pacbridge.net;

public interface ProtocolLayer extends UpperLayer, LowerLayer, Tappable, ActivityTrackable {
    public boolean isActive();
}
