package zedi.pacbridge.zap.messages;

import zedi.pacbridge.utl.NamedType;

public class FieldDataType extends NamedType {
    public static final int S8_NUMBER = 1;
    public static final int S16_NUMBER = 2;
    public static final int S32_NUMBER = 3;
    public static final int S48_NUMBER = 4;
    public static final int S64_NUMBER = 5;
    public static final int F32_NUMBER = 6;
    public static final int STRING_NUMBER = 7;
    
    public static final FieldDataType S8 = new FieldDataType("S8", S8_NUMBER); 
    public static final FieldDataType S16 = new FieldDataType("S16", S16_NUMBER);
    public static final FieldDataType S32 = new FieldDataType("S32", S32_NUMBER);
    public static final FieldDataType S48 = new FieldDataType("S48", S48_NUMBER);
    public static final FieldDataType S64 = new FieldDataType("S64", S64_NUMBER);
    public static final FieldDataType F32  = new FieldDataType("F32", F32_NUMBER);
    public static final FieldDataType STRING = new FieldDataType("String", STRING_NUMBER);
    
    private FieldDataType(String name, Integer number) {
        super(name, number);
    }
    
    public static final FieldDataType fieldTypeForNumber(Integer number) {
        switch (number.intValue()) {
            case S8_NUMBER : return S8;
            case S16_NUMBER : return S16;
            case S32_NUMBER : return S32;
            case S48_NUMBER : return S48;
            case S64_NUMBER : return S64;
            case F32_NUMBER : return F32;
            case STRING_NUMBER : return STRING;
        }
        throw new IllegalArgumentException("Unknown field type specified: " + number);
    }
    
    public static final FieldDataType fieldDataTypeForName(String name) {
        if (S8.getName().equalsIgnoreCase(name))
            return S8;
        if (S16.getName().equalsIgnoreCase(name))
            return S16;
        if (S32.getName().equalsIgnoreCase(name))
            return S32;
        if (S48.getName().equalsIgnoreCase(name))
            return S48;
        if (S64.getName().equalsIgnoreCase(name))
            return S64;
        if (F32.getName().equalsIgnoreCase(name))
            return F32;
        if (STRING.getName().equalsIgnoreCase(name))
            return STRING;
        throw new IllegalArgumentException("Unknown field type specified: " + name);
    }
}
