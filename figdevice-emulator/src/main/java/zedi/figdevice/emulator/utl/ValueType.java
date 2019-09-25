package zedi.figdevice.emulator.utl;

import zedi.pacbridge.utl.NamedType;

public class ValueType extends NamedType {
    public static final ValueType FIXED = new ValueType("Fixed", 1);
    public static final ValueType RANDOM = new ValueType("Random", 2);
    
    private ValueType(String name, Integer number) {
        super(name, number);
    }
    
    public static final ValueType valueTypeForName(String name) {
        if (FIXED.getName().equalsIgnoreCase(name))
            return FIXED;
        if (RANDOM.getName().equalsIgnoreCase(name))
            return RANDOM;
        throw new IllegalArgumentException("Unknown value type: " + name);
    }
}
