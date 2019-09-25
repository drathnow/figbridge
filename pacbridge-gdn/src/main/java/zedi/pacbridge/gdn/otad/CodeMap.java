package zedi.pacbridge.gdn.otad;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;


/**
 *  A code map is an array of bytes where each bit represents a code block in a code image being
 *  transfered by OTAD. When an OTAD is initiated, a code map is built representing all the
 *  code blocks that will be sent.  This is sent to the remote device that uses it to track what
 *  blocks have arrived.  If an OTAD is interrupted part way through a download, the remote device
 *  will sent the code map back in the response to the RequestSystemInfoMessage.  The code server
 *  can then pick up the OTAD where it left off.
 * 
 */
public class CodeMap implements Serializable {
    
    BitFieldArray bitFieldArray = new BitFieldArray();
    
    public CodeMap() {
    }
    
    public CodeMap(byte[] bytes) {
        setMapBytes(bytes);
    }

    public int firstMissingAddressesFromStartingAddress(int startAddress) {
        for (int i = 0; i < bitFieldArray.size(); i++) {
            int byteStartAddress = startAddress + (i * 8 * CodeBlock.MAX_CODEBLOCK_SIZE);
            for (int j = 0; j < 8; j++) {
                if (bitFieldArray.isBitSetInByte(j, i)) {
                    return (byteStartAddress + (j * CodeBlock.MAX_CODEBLOCK_SIZE));
                }
            }
        }
        return 0;
    }
        
    public void addMapByte(int aByte, int byteNumber) {
        bitFieldArray.setByteAtPosition(aByte, byteNumber);
    }
    
    public void setMapBytes(byte[] bytes) {
        bitFieldArray.clear();
        for (int i = 0; i < bytes.length; i++)
            bitFieldArray.addBitField(new BitField(bytes[i]));
    }
    
    public int getLength() {
        return bitFieldArray.size();
    }
    
    public byte[] asBytes() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(byteStream);
        try {
            serialize(dataOutput);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return byteStream.toByteArray();
    }
    
    public void serialize(DataOutput dataOutput) throws IOException {
        byte[] bytes = mapBytes();
        for (int i = 0; i < bytes.length; i++)
            dataOutput.writeByte(bytes[i]);
    }

    public void deserialize(DataInput dataInput) throws IOException {
        try {
            while (true)
                bitFieldArray.addBitField(new BitField(dataInput.readByte()));
        } catch (EOFException e) {
        }
    }

    public byte[] mapBytes() {
        return bitFieldArray.asByteArray();
    }

    public int lastAddressFromStartingAddress(int startAddress) {
        return startAddress + (bitFieldArray.size() * 7 * CodeBlock.MAX_CODEBLOCK_SIZE);
    }
    
    public byte[] getMapBytes() {
        return bitFieldArray.asByteArray();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        CodeMap codeMap = new CodeMap();
        codeMap.bitFieldArray = (BitFieldArray)bitFieldArray.clone();
        return codeMap;
    }
}
