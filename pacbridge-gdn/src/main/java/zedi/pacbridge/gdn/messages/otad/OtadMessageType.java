package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;

public class OtadMessageType implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    static final OtadMessageType NullMessage = new OtadMessageType("NullMessage", 0);
    public static final OtadMessageType RequestSystemInfo = new OtadMessageType("RequestSystemInfo", 1);
    public static final OtadMessageType SetCodeMap = new OtadMessageType("SetCodeMap",2);
    public static final OtadMessageType WriteCodeBlock = new OtadMessageType("WriteCodeBlock", 4);
    public static final OtadMessageType LoadImage = new OtadMessageType("LoadImage", 5);
    
    private String name;
    private Integer number;
    
    private OtadMessageType(String name, Integer number) {
        this.name = name;
        this.number = number;
    }
 
    public int getTypeNumber() {
        return number;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name + "(" + number + ")";
    }
    
    public static final OtadMessageType messageTypeForNumber(Integer number) {
        if (number == RequestSystemInfo.getTypeNumber())
            return RequestSystemInfo;
        if (number == SetCodeMap.getTypeNumber())
            return SetCodeMap;
        if (number == WriteCodeBlock.getTypeNumber())
            return WriteCodeBlock;
        if (number == LoadImage.getTypeNumber())
            return LoadImage;
        if (number == NullMessage.getTypeNumber())
            return NullMessage;
        throw new IllegalArgumentException("Unrecognized OTAD message number: " + number);
    }
}
