package zedi.pacbridge.stp.fad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.utl.crc.Crc16Reflect;
import zedi.pacbridge.utl.crc.CrcCalculator;
import zedi.pacbridge.utl.crc.CrcException;


class PendingMessage {

    private Map<Integer, Segment> segmentMap = new TreeMap<Integer, Segment>();

    private int messageId;
    private int crc;
    private CrcCalculator crcCalculator;
    private int expectedNumberOfSegments;
    private Lock lock;

    public PendingMessage(int messageId) {
        this(messageId, new Crc16Reflect());
    }

    public PendingMessage(int messageId, CrcCalculator crcCalculator) {
        this.crcCalculator = crcCalculator;
        this.messageId = messageId;
        this.expectedNumberOfSegments = 0;
        this.lock = new ReentrantLock();
    }

    public int getMessageId() {
        return messageId;
    }

    public void addSegment(Segment fadSegment) {
        lock.lock();
        try {
            segmentMap.put(fadSegment.getSegmentId(), fadSegment);
            if (fadSegment.isLastSegment()) {
                crc = fadSegment.getCrc();
                expectedNumberOfSegments = fadSegment.getSegmentId() + 1;
            }
        } finally {
            lock.unlock();
        }
    }

    public int getCrc() {
        return crc;
    }

    public boolean isComplete() {
        lock.lock();
        try {
            return (expectedNumberOfSegments == 0) ? false : (segmentMap.size() == expectedNumberOfSegments);
        } finally {
            lock.unlock();
        }
    }

    public byte[] getMessage() throws CrcException {
        if (isComplete() == false)
            return null;
        try {
            lock.lock();
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            for (Segment segment : segmentMap.values())
                try {
                    arrayOutputStream.write(segment.getPayload());
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create message", e);
                }
            byte[] payload = arrayOutputStream.toByteArray();
            int calculatedCrc = crcCalculator.calculate(Fad.CRC_SEED, payload);
            if (calculatedCrc != crc)
                throw new CrcException("CRC error detected. Expected 0x" 
                        + Integer.toHexString(crc & 0xffff)
                        + " but was 0x"
                        + Integer.toHexString(calculatedCrc& 0xffff));
            return payload;
        } finally {
            lock.unlock();
        }
    }
}
