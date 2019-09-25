package zedi.pacbridge.zap.messages;

class TypeNumberEncoder {
    public static final Short TYPE_MASK = (short)0xf000;
    public static final Short TAG_MASK = (short)0x0fff;
    private static final Integer BIT_SHIFT = 12;
    
    public static short encodedNumberField(Field<?> field) {
        return encodedNumberFor(field.getFieldType().getDataType().getNumber(), field.getFieldType().getDataType().getNumber());
    }
    
    public static short encodedNumberFor(Integer typeNumber, Integer tagNumber) {
        return (short)((typeNumber << BIT_SHIFT) | (tagNumber & TAG_MASK));
    }

    public static Integer typeNumberFromEncodedValue(short value) {
        return (value & TYPE_MASK) >> BIT_SHIFT;
    }

    public static Integer tagNumberFromEncodedValue(short value) {
        return value & TAG_MASK;
    }
}
