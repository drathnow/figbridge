package zedi.pacbridge.gdn.otad;

import java.util.List;
import java.util.Vector;


public class CodeBlockMap {
    public static final int BIT0_MASK = 0x01;
    public static final int BIT1_MASK = 0x02;
    public static final int BIT2_MASK = 0x04;
    public static final int BIT3_MASK = 0x08;
    public static final int BIT4_MASK = 0x10;
    public static final int BIT5_MASK = 0x20;
    public static final int BIT6_MASK = 0x40;
    public static final int BIT7_MASK = 0x80;
    protected Vector<Byte> blocks;

    private CodeBlockMap(CodeBlockMap codeMap) {
        blocks = new Vector<Byte>(codeMap.blocks);
    }

    public CodeBlockMap() {
        blocks = new Vector<Byte>();
    }

    public void addBlock(byte blockByte) {
        blocks.add(blockByte);
    }
    
    public byte firstBlock() {
        return (blocks.size() == 0) ? (byte)0 : ((Byte)blocks.get(0)).byteValue();
    }

    public CodeBlockMap(int aByte) {
        this();
        blocks.add(new Byte((byte)aByte));
    }

    public boolean isBit0Set() {
        return isBit0Set(0);
    }

    public boolean isBit0Set(int byteNumber) {
        return isBitSetInByte(BIT0_MASK, byteNumber);
    }

    public boolean isBit1Set() {
        return isBit1Set(0);
    }

    public boolean isBit1Set(int byteNumber) {
        return isBitSetInByte(BIT1_MASK, byteNumber);
    }

    public boolean isBit2Set() {
        return isBit2Set(0);
    }

    public boolean isBit2Set(int byteNumber) {
        return isBitSetInByte(BIT2_MASK, byteNumber);
    }

    public boolean isBit3Set() {
        return isBit3Set(0);
    }

    public boolean isBit3Set(int byteNumber) {
        return isBitSetInByte(BIT3_MASK, byteNumber);
    }

    public boolean isBit4Set() {
        return isBit4Set(0);
    }

    public boolean isBit4Set(int byteNumber) {
        return isBitSetInByte(BIT4_MASK, byteNumber);
    }

    public boolean isBit5Set() {
        return isBit5Set(0);
    }

    public boolean isBit5Set(int byteNumber) {
        return isBitSetInByte(BIT5_MASK, byteNumber);
    }

    public boolean isBit6Set() {
        return isBit6Set(0);
    }

    public boolean isBit6Set(int byteNumber) {
        return isBitSetInByte(BIT6_MASK, byteNumber);
    }

    public boolean isBit7Set() {
        return isBit7Set(0);
    }

    public boolean isBit7Set(int byteNumber) {
        return isBitSetInByte(BIT7_MASK, byteNumber);
    }

    protected boolean isBitSetInByte(int bitMask, int byteNumber) {
        byte aByte = byteForByteNumber(byteNumber).byteValue();
        return (aByte & bitMask) > 0;
    }

    public void setBit0(boolean on) {
        setBitsInByte(BIT0_MASK, on, 0);
    }

    public void setBit0(boolean on, int byteNumber) {
        setBitsInByte(BIT0_MASK, on, byteNumber);
    }

    public void setBit1(boolean on) {
        setBitsInByte(BIT1_MASK, on, 0);
    }

    public void setBit1(boolean on, int byteNumber) {
        setBitsInByte(BIT1_MASK, on, byteNumber);
    }

    public void setBit2(boolean on) {
        setBitsInByte(BIT2_MASK, on, 0);
    }

    public void setBit2(boolean on, int byteNumber) {
        setBitsInByte(BIT2_MASK, on, byteNumber);
    }

    public void setBit3(boolean on) {
        setBitsInByte(BIT3_MASK, on, 0);
    }

    public void setBit3(boolean on, int byteNumber) {
        setBitsInByte(BIT3_MASK, on, byteNumber);
    }

    public void setBit4(boolean on) {
        setBitsInByte(BIT4_MASK, on, 0);
    }

    public void setBit4(boolean on, int byteNumber) {
        setBitsInByte(BIT4_MASK, on, byteNumber);
    }

    public void setBit5(boolean on) {
        setBitsInByte(BIT5_MASK, on, 0);
    }

    public void setBit5(boolean on, int byteNumber) {
        setBitsInByte(BIT5_MASK, on, byteNumber);
    }

    public void setBit6(boolean on) {
        setBitsInByte(BIT6_MASK, on, 0);
    }

    public void setBit6(boolean on, int byteNumber) {
        setBitsInByte(BIT6_MASK, on, byteNumber);
    }

    public void setBit7(boolean on) {
        setBitsInByte(BIT7_MASK, on, 0);
    }

    public void setBit7(boolean on, int byteNumber) {
        setBitsInByte(BIT7_MASK, on, byteNumber);
    }

    public Object clone() {
        return new CodeBlockMap(this);
    }

    public void setBitsInByte(int bitMask, boolean on, int byteNumber) {
        byte theByte = byteForByteNumber(byteNumber).byteValue();
        if (on)
            theByte |= bitMask;
        else
            theByte &= ~bitMask;
        blocks.setElementAt(new Byte(theByte), byteNumber);
    }

    protected Byte byteForByteNumber(int byteNumber) {
        if (byteNumber >= blocks.size())
            for (int i = blocks.size(); i <= byteNumber; i++)
                blocks.add(new Byte((byte)0));
        return blocks.elementAt(byteNumber);
    }

    public List<Byte> blocks() {
        return blocks;
    }

    public int getBlocksSize() {
        return blocks.size();
    }

    public Vector<Byte> getBlocks() {
        return blocks;
    }
}
