package zedi.pacbridge.net;

public interface SequencedMessage extends Message {
    public Integer sequenceNumber();
}
