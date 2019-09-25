package zedi.pacbridge.app.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import zedi.pacbridge.app.Constants;

public class MessageIDGenerator {
    static final Lock lock = new ReentrantLock();
    static final String NOT_INITIALIZED = "<not initialized>-";
    static long id = System.currentTimeMillis();
    static StringBuffer stringBuffer = new StringBuffer(NOT_INITIALIZED);
    static int savedLength;
    
    static {
        lock.lock();
        stringBuffer.setLength(0);
        stringBuffer.append(Constants.BRIDGE_NAME);
        stringBuffer.append('-');
        savedLength = stringBuffer.toString().length();
        lock.unlock();
    }

    public static synchronized String nextMessageId() {
        lock.lock();
        try {
            String uid = stringBuffer.append(id++).toString();
            stringBuffer.setLength(savedLength);
            return uid;
        } finally {
            lock.unlock();
        }
    }
}
