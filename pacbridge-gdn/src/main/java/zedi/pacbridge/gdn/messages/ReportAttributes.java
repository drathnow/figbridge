package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.GdnSerializable;
import zedi.pacbridge.utl.io.Unsigned;


/**
 * The ReportAttributes is a convenince class for serializing report IO point report
 * attributes.  IO Point reports attributes define what type of report it is and why it was
 * generated
 * 7        4              0
 * +--+--+--+--+--+--+--+--+
 * |  |  |  |  |  |  |  |  |
 * +--+--+--+--+--+--+--+--+
 * <ValType> <- ReasonCode->
 * 3 bits      5 bits
 */
public class ReportAttributes implements GdnSerializable {
    static final long serialVersionUID = 1001;

    protected static final int REASONCODE_BIT_MASK = 0x1f;
    protected static final int VALUETYPE_BIT_MASK = 0x70;
    protected static final int REASONCODE_FIELD_BIT_SIZE = 5;
    protected static final int VALUETYPE_FIELD_BIT_SIZE = 3;

    protected GdnReasonCode reasonCode;
    protected ValueType valueType;

    ReportAttributes() {
        this(GdnReasonCode.Scheduled, ValueType.Standard);
    }

    public ReportAttributes(int reasonCodeNumber, int valueTypeNumber) {
        this(GdnReasonCode.reascodeForReasonNumber(reasonCodeNumber), ValueType.valueTypeForTypeNumber(valueTypeNumber));
    }
    
    public ReportAttributes(GdnReasonCode reasonCode, ValueType valueType) {
        this.reasonCode = reasonCode;
        this.valueType = valueType;
    }

    public ReportAttributes(int reportAttributes) {
        parseReportAttributeByte(reportAttributes);
    }

    public GdnReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(GdnReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }
    
    public ValueType getValueType() {
        return valueType;
    }
    
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }
    
    public int rawValue() {
        return rawValue(valueType.getNumber(), reasonCode.getNumber());
    }
    
    private void parseReportAttributeByte(int aByteValue) {
        valueType = ValueType.valueTypeForTypeNumber((aByteValue >> REASONCODE_FIELD_BIT_SIZE));
        reasonCode = GdnReasonCode.reascodeForReasonNumber(aByteValue & REASONCODE_BIT_MASK);
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)rawValue());
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        parseReportAttributeByte((int)Unsigned.getUnsignedByte(byteBuffer));
    }
    
    public String toString() {
        return "Value Type: " + valueType.getName() + ", Reason code: " + reasonCode.getName();
    }

    public static ReportAttributes reportAttributesFromByteBuffer(ByteBuffer byteBuffer) {
        ReportAttributes reportAttributes = new ReportAttributes();
        reportAttributes.deserialize(byteBuffer);
        return reportAttributes;
    }
    
    static final int rawValue(int aValueType, int aReasonCode) {
        return (aValueType << REASONCODE_FIELD_BIT_SIZE) | aReasonCode;
    }
}