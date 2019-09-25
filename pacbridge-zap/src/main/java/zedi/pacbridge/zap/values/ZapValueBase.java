package zedi.pacbridge.zap.values;

import java.io.Serializable;

public abstract class ZapValueBase implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    private ZapDataType dataType;

    protected ZapValueBase() {
    }
    
    protected ZapValueBase(ZapDataType dataType) {
        this.dataType = dataType;
    }
    
    public ZapDataType dataType() {
        return dataType;
    }
}
