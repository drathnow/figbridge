package zedi.pacbridge.zap.reporting;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapValue;
import zedi.pacbridge.zap.values.ZapValueDeserializer;

public class IoPointReading {
    private ZapValue value;
    private ReadingFlags readingFlags;

    public IoPointReading(ReadingFlags readingFlags, ZapValue value) {
        this.readingFlags = readingFlags;
        this.value = value;
    }
    
    public boolean isEmptyValue() {
        return readingFlags.isEmptyValue();
    }
    
    public boolean isNullValue() {
        return readingFlags.isNullValue();
    }
    
    public ZapValue value() {
        return value;
    }
    
    public ZapAlarmStatus alarmStatus() {
        return readingFlags.alarmStatus();
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(readingFlags.asByteValue());
        value.serialize(byteBuffer);
    }
    
    public static IoPointReading ioPointReadingFromByteBuffer(ZapDataType dataType, ByteBuffer byteBuffer) {
        ZapValue value = null;
        ReadingFlags readingFlags = ReadingFlags.readingFlagsFromByteBuffer(byteBuffer);
        if (readingFlags.isEmptyValue() == false) {
            if (readingFlags.isNullValue() == false)
                value = ZapValueDeserializer.valueFromByteBuffer(dataType, byteBuffer);
        }
        return new IoPointReading(readingFlags, value);
    }

    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        if (value == null) {
            obj.put("Value", isNullValue() ? "Null" : "Empty");
        } else 
            obj.put("Value", value.toString());
        obj.put("ReadingFlags", readingFlags.asJsonObject());
        return obj;
    }
}
