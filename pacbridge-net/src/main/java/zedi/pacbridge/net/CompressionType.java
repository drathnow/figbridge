package zedi.pacbridge.net;

import zedi.pacbridge.utl.NamedType;

public class CompressionType extends NamedType {
    private static final int NONE_NUMBER = 0;

    public static final CompressionType NONE = new CompressionType("None", NONE_NUMBER);
    
    private CompressionType(String name, Integer number) {
        super(name, number);
    }
    
    public static final CompressionType compressionTypeForNumber(Integer number){
        switch (number) {
            case NONE_NUMBER : return NONE;
        }
        throw new IllegalArgumentException("Unknown compression type number: '" + number + "'");
    }
}
