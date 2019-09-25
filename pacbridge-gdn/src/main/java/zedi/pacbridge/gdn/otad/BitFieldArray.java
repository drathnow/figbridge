package zedi.pacbridge.gdn.otad;

import java.util.ArrayList;
import java.util.List;

public class BitFieldArray {

    List<BitField> bitFields;

    public BitFieldArray() {
        bitFields = new ArrayList<BitField>();
    }
    
    public BitFieldArray(byte[] byteArray) {
        this();
        for (int i = 0; i < byteArray.length; i++)
            bitFields.add(new BitField(byteArray[i]));
    }

    public BitFieldArray(int size) {
        this();
        for (int i = 0; i < size; i++)
            bitFields.add(new BitField(0));
    }

    public boolean isBitSetInByte(int bitNumber, int byteNumber) {
        BitField byteMap = (BitField)bitFields.get(byteNumber);
        return byteMap.isBitSet(bitNumber);
    }

    public void setBitInByte(int bitNumber, int byteNumber) {
        BitField byteMap = (BitField)bitFields.get(byteNumber);
        byteMap.setBit(bitNumber);
    }
    
    public void clearBitInByte(int bitNumber, int byteNumber) {
        BitField byteMap = (BitField)bitFields.get(byteNumber);
        byteMap.clearBit(bitNumber);
    }
    
    public void setBitsInByte(int bitMask, boolean on, int byteNumber) {
        int theByte = byteMapForByteNumber(byteNumber).byteValue();
        if (on)
            theByte |= bitMask;
        else
            theByte &= ~bitMask;
        bitFields.set(byteNumber, new BitField(theByte));
    }    

    protected BitField byteMapForByteNumber(int byteNumber) {
        if (byteNumber >= bitFields.size())
            for (int i = bitFields.size(); i <= byteNumber; i++)
                bitFields.add(new BitField((byte)0));
        return (BitField)bitFields.get(byteNumber);
    }
    
    public byte[] asByteArray() {
        byte[] byteArray = new byte[bitFields.size()];
        for (int i = 0; i < byteArray.length; i++)
            byteArray[i] = (byte)((BitField)bitFields.get(i)).byteValue();
        return byteArray;
    }

    public void addBitField(BitField byteMap) {
        bitFields.add(byteMap);
    }

    public int size() {
        return bitFields.size();
    }

    public void clear() {
        bitFields.clear();
    }

    public int getByteNumber(int byteNumber) {
        if (byteNumber > bitFields.size())
            throw new IllegalArgumentException("Byte number out of range");
        return ((BitField)bitFields.get(byteNumber)).byteValue();
    }

    public void setByteAtPosition(int aByte, int byteNumber) {
        if (byteNumber >= bitFields.size())
            for (int i = bitFields.size(); i <= byteNumber; i++)
                bitFields.add(new BitField(0));
        bitFields.set(byteNumber, new BitField(aByte));
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        BitFieldArray bitFieldArray = new BitFieldArray();
        for (BitField field : bitFields)
            bitFieldArray.addBitField(new BitField(field.byteValue()));
        return bitFieldArray;
    }
}
