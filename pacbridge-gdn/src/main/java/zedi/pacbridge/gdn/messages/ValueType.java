package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;

public class ValueType extends NamedType implements Serializable {
    
    public static final ValueType Standard = new ValueType("Standard", 0);
    public static final ValueType Extended = new ValueType("Extended", 1);
    
    private ValueType(String name, int typeNumber) {
        super(name, typeNumber);
    }
    
    public static final ValueType valueTypeForTypeNumber(int typeNumber) {
        switch (typeNumber) {
            case 0 : return Standard;
            case 1 : return Extended;
        }
        throw new IllegalArgumentException("Unknow value type number: " + typeNumber);
    }
}
