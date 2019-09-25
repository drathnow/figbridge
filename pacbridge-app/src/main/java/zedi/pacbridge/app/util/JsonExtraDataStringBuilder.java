package zedi.pacbridge.app.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonExtraDataStringBuilder {

    private Map<String, String> map;
    
    public JsonExtraDataStringBuilder() {
        this.map = new HashMap<String, String>();
    }
    
    public void put(String key, String value) {
        map.put(key, value);
    }
    
    public String toJsonString() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(new JSONObject(map));
        return jsonArray.toString();
    }
}
