package zedi.pacbridge.domain.entities;

import java.io.Serializable;

public abstract class CoreEntity implements Serializable {
    
    public static final Integer ZERO_I = new Integer(0);
    public static final Integer ONE_I = new Integer(1);
    public static final Double ZERO_D = new Double(0);
    public static final Double ONE_D = new Double(1);
    public static final Float ZERO_F = new Float(0);
    public static final Float ONE_F = new Float(1);
    
    public abstract Object getPrimaryKey();

}
