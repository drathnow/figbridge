package zedi.pacbridge.zap.reporting;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;

/**
 * The Reading flags tell you what's coming in the reading's value field.
 * 
 * The top bit, bit 7, is a null value indicator bit.  If this bit is set to 1, it indicates that the value 
 * field is null, that is, there are no bytes in the value field.  If the value is 0, the value field contains a 
 * value as specified in the associated IO Point Template object.  Bits 0 to 4 contain the alarm status of 
 * the IO point.  If bit 7 is not set (zero), bit 6 indicates whether the value field contains a value or
 * is null.  This situation will happen if a value could not be obtained and the device wants to indicate
 * this by passing a null value with an alarm status.
 *   
 * <------- Byte 0 -------->
 * 7                       0
 * +--+--+--+--+--+--+--+--+
 * |  |  |  |  |  |  |  |  |
 * +--+--+--+--+--+--+--+--+
 *  ^   ^       <-alrm St-> 
 *  |   |                   
 *  |   1: Value is null and alarm status unknown            
 *  |                       
 * 1: Value is null, Alarm Status is valid        
 * 0: Value is not Null, Alarm Status is valid
 */
public class ReadingFlags {
    private static Integer ALARM_STATUS_MASK = 0x0F;
    private static Integer NULL_VALUE_MASK = 0x80;
    private static Integer EMPTY_VALUE_FLAG = 0x40;

    private boolean nullValue; 
    private boolean emptyValue;
    private ZapAlarmStatus alarmStatus;
    
    public ReadingFlags(ZapAlarmStatus alarmStatus) {
        this(false, false, alarmStatus);
    }
    
    public ReadingFlags(boolean isNullValue, boolean isEmptyValue, ZapAlarmStatus alarmStatus) {
        this.nullValue = isNullValue;
        this.emptyValue = isEmptyValue;
        this.alarmStatus = alarmStatus;
    }
    
    public boolean isNullValue() {
        return nullValue;
    }
    
    public boolean isEmptyValue() {
        return emptyValue;
    }
    
    public ZapAlarmStatus alarmStatus() {
        return alarmStatus;
    }

    public byte asByteValue() {
        byte theByte = 0;
        if (nullValue)
            theByte = NULL_VALUE_MASK.byteValue();
        else {
            if (emptyValue == false)
                theByte |= alarmStatus.getNumber().byteValue();
            else 
                theByte |= EMPTY_VALUE_FLAG;
        }
        return theByte;
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(asByteValue());
    }

    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("NullValue", isNullValue());
        obj.put("EmptyValue", isEmptyValue());
        obj.put("AlarmStatus", alarmStatus.getName());
        return obj;
    }
    
    public static ReadingFlags readingFlagsFromByteBuffer(ByteBuffer byteBuffer) {
        boolean nullValue = false;
        boolean emptyValue = false;
        ZapAlarmStatus alarmStatus = null;
        int theByte = Unsigned.getUnsignedByte(byteBuffer);
        emptyValue = (theByte & EMPTY_VALUE_FLAG) != 0;
        if (emptyValue == false) {
            nullValue = (theByte & NULL_VALUE_MASK) != 0;
            alarmStatus = ZapAlarmStatus.alarmStatusForAlarmStatusNumber(theByte & ALARM_STATUS_MASK); 
        }
        return new ReadingFlags(nullValue, emptyValue, alarmStatus);
    }
}
