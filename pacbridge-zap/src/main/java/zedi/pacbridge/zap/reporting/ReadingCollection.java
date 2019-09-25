package zedi.pacbridge.zap.reporting;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.Utilities;
import zedi.pacbridge.utl.io.Unsigned;

public class ReadingCollection {
    
    private Date timestamp;
    private List<IoPointReading> readings;

    public ReadingCollection(Date timestamp, List<IoPointReading> readings) {
        this.timestamp = timestamp;
        this.readings = readings;
    }
    
    public Date timestamp() {
        return timestamp;
    }
    
    public List<IoPointReading> ioPointReadings() {
        return readings;
    }

    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt((int)(timestamp.getTime()/1000L));
        for (Iterator<IoPointReading> iter = readings.iterator(); iter.hasNext(); ) 
            iter.next().serialize(byteBuffer);
    }
    
    public static ReadingCollection readingCollectionFromByteBuffer(List<IoPointTemplate> templates, ByteBuffer byteBuffer) {
        List<IoPointReading> readings = new ArrayList<>();
        Date timestamp = new Date(Unsigned.getUnsignedInt(byteBuffer)*1000L);
        for (Iterator<IoPointTemplate> iter = templates.iterator(); iter.hasNext(); ) {
            IoPointTemplate template = iter.next();
            IoPointReading reading = IoPointReading.ioPointReadingFromByteBuffer(template.dataType(), byteBuffer);
            readings.add(reading);
        }
        return new ReadingCollection(timestamp, readings);
    }

    public JSONObject asJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("Timestamp", Utilities.ISO8601DateFormatterUtc.format(timestamp));
        JSONArray array = new JSONArray();
        for (IoPointReading reading : readings)
            array.put(reading.asJsonObject());
        obj.put("Readings", array);
        return obj;
    }
}
