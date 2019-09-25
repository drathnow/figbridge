package zedi.pacbridge.zap.messages;

import zedi.pacbridge.net.HeaderType;
import zedi.pacbridge.utl.NamedType;

public class ZapHeaderType extends NamedType implements HeaderType {
    static final int SESSION_HEADER_NUMBER = 1;
    
    public static final ZapHeaderType SESSION_HEADER = new ZapHeaderType("Session Header", SESSION_HEADER_NUMBER);
    
    private ZapHeaderType(String name, Integer number) {
        super(name, number);
    }

    @Override
    public Integer getTypeNumber() {
        return getNumber();
    }
    
}
