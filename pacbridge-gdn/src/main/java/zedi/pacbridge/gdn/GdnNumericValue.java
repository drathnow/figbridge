package zedi.pacbridge.gdn;

import java.io.Serializable;

public abstract class GdnNumericValue<TType extends Number> extends GdnValue<TType> implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    protected GdnNumericValue(GdnDataType dataType) {
        super(dataType);
    }
    
    protected void setValue(TType value) {
        super.setValue(value);
    }
    
    public boolean isNumeric() {
        return true;
    }
}
