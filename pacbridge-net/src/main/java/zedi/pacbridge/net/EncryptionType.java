package zedi.pacbridge.net;

import zedi.pacbridge.utl.NamedType;

public class EncryptionType extends NamedType {
    private static final int NONE_NUMBER = 0;

    public static final EncryptionType NONE = new EncryptionType("None", NONE_NUMBER);
    
    private EncryptionType(String name, Integer number) {
        super(name, number);
    }
    
    public static final EncryptionType encryptionTypeForNumber(Integer number){
        switch (number) {
            case NONE_NUMBER : return NONE;
        }
        throw new IllegalArgumentException("Unknown encryption type number: '" + number + "'");
    }

}
