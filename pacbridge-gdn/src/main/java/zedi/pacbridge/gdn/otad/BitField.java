package zedi.pacbridge.gdn.otad;

import java.io.Serializable;

public class BitField implements Serializable {
    static final long serialVersionUID = 1001;

    int theByte;
    
    public BitField() {
    }
    
    public BitField(int theByte) {
        if (theByte > 255)
            throw new IllegalArgumentException("Byte value must be between 0 and 255");
        this.theByte = theByte;
    }
    
    BitField(BitField bitField) {
        this.theByte = bitField.theByte;
    }
    
    public void setBit(int bitNumber) {
        if (bitNumber > 7)
            throw new IllegalArgumentException("Bit number must be between 90 and 7");
        int mask = 1 << bitNumber;
        theByte |= mask;
    }
    
    public void clearBit(int bitNumber) {
        if (bitNumber > 7)
            throw new IllegalArgumentException("Bit number must be between 90 and 7");
        int mask = 1 << bitNumber;
        theByte &= ~mask;            
    }
    
    public int byteValue() {
        return theByte;
    }

    public boolean isBitSet(int bitNumber) {
        int mask = 1 << bitNumber;
        return (theByte & mask) > 0;
    }

    public boolean isBitCleared(int bitNumber) {
        int mask = 1 << bitNumber;
        return (theByte & mask) == 0;
    }
}
