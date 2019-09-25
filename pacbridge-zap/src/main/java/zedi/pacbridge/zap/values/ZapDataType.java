package zedi.pacbridge.zap.values;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;

import zedi.pacbridge.net.DataType;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.NamedType;


public class ZapDataType extends NamedType implements DataType, Serializable {
    private static final long serialVersionUID = 1001;

    public static final int UNBOUNDED_SIZE = -1;
    
    public static final int EMPTY_VALUE = 0;
    public static final int DISCRETE = 1;
    public static final int BYTE = 2;
    public static final int UNSIGNED_BYTE = 3;
    public static final int INTEGER = 4;
    public static final int UNSIGNED_INTEGER = 5;
    public static final int LONG = 6;
    public static final int UNSIGNED_LONG = 7;
    public static final int FLOAT = 8;
    public static final int DOUBLE = 9;
    public static final int STRING = 10;
    public static final int BINARY = 11;
    public static final int LONG_LONG = 12;
    public static final int UNSIGNED_LONG_LONG = 13;
    
    public static final String EMPTY_VALUE_NAME = "EmptyValue";
    public static final String DISCRETE_NAME = "Discrete";
    public static final String BYTE_NAME = "Byte";
    public static final String UNSIGNED_BYTE_NAME = "UnsignedByte";
    public static final String SHORT_NAME = "Short";
    public static final String UNSIGNED_SHORT_NAME = "UnsignedShort";
    public static final String INTEGER_NAME = "Integer";
    public static final String UNSIGNED_INTEGER_NAME = "UnsignedInteger";
    public static final String LONG_NAME = "Long";
    public static final String UNSIGNED_LONG_NAME = "UnsignedLong";
    public static final String FLOAT_NAME = "Float"; 
    public static final String DOUBLE_NAME = "Double";
    public static final String BINARY_NAME = "Binary";
    public static final String STRING_NAME = "String";
    
    public static final ZapDataType EmptyValue = new ZapDataType(EMPTY_VALUE_NAME, EMPTY_VALUE, 0, new StringConverter());
    public static final ZapDataType Discrete = new ZapDataType(DISCRETE_NAME, DISCRETE, 1, new NumericConverter<Integer>(0, 1, ZapDiscrete.class));
    public static final ZapDataType Byte = new ZapDataType(BYTE_NAME, BYTE, 1, new NumericConverter<Integer>(-128, 127, ZapByte.class));
    public static final ZapDataType UnsignedByte = new ZapDataType(UNSIGNED_BYTE_NAME, UNSIGNED_BYTE, 1, new NumericConverter<Integer>(0, 255, ZapUnsignedByte.class));
    public static final ZapDataType Integer = new ZapDataType(INTEGER_NAME, INTEGER, 2, new NumericConverter<Integer>(java.lang.Short.MIN_VALUE, java.lang.Short.MAX_VALUE, ZapShort.class));
    public static final ZapDataType UnsignedInteger = new ZapDataType(UNSIGNED_INTEGER_NAME, UNSIGNED_INTEGER, 2, new NumericConverter<Integer>(0, 65535, ZapUnsignedShort.class));
    public static final ZapDataType Long = new ZapDataType(LONG_NAME, LONG, 4, new NumericConverter<Integer>(java.lang.Integer.MIN_VALUE, java.lang.Integer.MAX_VALUE, ZapLong.class));
    public static final ZapDataType UnsignedLong = new ZapDataType(UNSIGNED_LONG_NAME, UNSIGNED_LONG, 4, new NumericConverter<Long>(0L, (long)0xFFFFFFFFL, ZapUnsignedLong.class));
    public static final ZapDataType Float = new ZapDataType(FLOAT_NAME, FLOAT, 4, new NumericConverter<Float>(java.lang.Float.NEGATIVE_INFINITY, java.lang.Float.POSITIVE_INFINITY, ZapFloat.class)); 
    public static final ZapDataType Double = new ZapDataType(DOUBLE_NAME, DOUBLE, 8, new NumericConverter<Double>(java.lang.Double.MIN_VALUE, java.lang.Double.MAX_VALUE, ZapDouble.class));
    public static final ZapDataType Binary = new ZapDataType(BINARY_NAME, BINARY, UNBOUNDED_SIZE, new BlobConverter());
    public static final ZapDataType String = new ZapDataType(STRING_NAME, STRING, UNBOUNDED_SIZE, new StringConverter());
    
    private static interface Converter<T> extends Serializable {
        public ZapValue convert(ZapDataType type, String s) throws ParseException;
    }
    
    private static class NumericConverter<T extends Number> implements Converter<T> {
        private static final long serialVersionUID = 1001;
        private Number lowerLimit;
        private Number upperLimit;
        private Class<? extends ZapValue> factoryClass;

        NumericConverter(Number lowerLimit, Number upperLimit, Class<? extends ZapValue> factoryClass) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.factoryClass = factoryClass;
        }

        public ZapValue convert(ZapDataType type, String s) throws ParseException {
            DecimalFormat format = new DecimalFormat();
            Number value = format.parse(s);
            if (isValidValueForMe(value) == false)
                throw new ParseException("Value " + value.toString() + " is invalid for " + type.getName(), 0);
            try {
                Constructor<?> constructor = factoryClass.getConstructor(Number.class);
                return (ZapValue)constructor.newInstance(value);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(factoryClass.getName() + " does not have required constructor");
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Unable to invoke ZapValue type constructor");
            }
        }
        
        private boolean isValidValueForMe(Number value) {
            return value.doubleValue() >= lowerLimit.doubleValue() && value.doubleValue() <= upperLimit.doubleValue();  
        }
    }
    
    private static class BlobConverter implements Converter<byte[]> {
        private static final long serialVersionUID = 1001;
        public ZapValue convert(ZapDataType type, String s) throws ParseException {
            return new ZapBlob(HexStringDecoder.hexStringAsBytes(s));
        }
    }

    private static class StringConverter implements Converter<String> {
        private static final long serialVersionUID = 1001;
        public ZapValue convert(ZapDataType type, String s) throws ParseException {
            return new ZapString(s);
        }
    }

    private Integer size;
    private Converter<?> converter;

    private ZapDataType(String name, Integer typeNumber, Integer size, Converter<?> converter) {
        super(name, typeNumber);
        this.size = size;
        this.converter = converter;
    }
    
    public ZapValue valueForString(String string) throws ParseException {
        return getNumber() == EMPTY_VALUE ? new ZapEmptyValue() : converter.convert(this, string);
    }
    
    public Integer getSize() {
        return size;
    }
    
    @Override
    public String toString() {
        return getName() + '(' + getNumber() + ')';
    }

    public static ZapDataType dataTypeForName(String name) {
        if (name.equalsIgnoreCase(EmptyValue.getName()))
            return EmptyValue;
        if (name.equalsIgnoreCase(Discrete.getName()))
            return Discrete;
        if (name.equalsIgnoreCase(Byte.getName()))
            return Byte;
        if (name.equalsIgnoreCase(UnsignedByte.getName()))
            return UnsignedByte;
        if (name.equalsIgnoreCase(Integer.getName()) || name.equalsIgnoreCase(SHORT_NAME))
            return Integer;
        if (name.equalsIgnoreCase(UnsignedInteger.getName()) || name.equalsIgnoreCase(UNSIGNED_SHORT_NAME))
            return UnsignedInteger;
        if (name.equalsIgnoreCase(Long.getName()))
            return Long;
        if (name.equalsIgnoreCase(UnsignedLong.getName()))
            return UnsignedLong;
        if (name.equalsIgnoreCase(Float.getName()))
            return Float;
        if (name.equalsIgnoreCase(Double.getName()))
            return Double;
        if (name.equalsIgnoreCase(Binary.getName()))
            return Binary;
        if (name.equalsIgnoreCase(String.getName()))
            return String;
        throw new IllegalArgumentException("Unknown data type name: " + name);
    }
    
    public static ZapDataType dataTypeForTypeNumber(Integer typeNumber) {
        switch (typeNumber) {
            case ZapDataType.EMPTY_VALUE :
                return EmptyValue;
            case ZapDataType.DISCRETE :
                return Discrete;
            case ZapDataType.BYTE :
                return Byte;
            case ZapDataType.UNSIGNED_BYTE :
                return UnsignedByte;
            case ZapDataType.INTEGER :
                return Integer;
            case ZapDataType.UNSIGNED_INTEGER :
                return UnsignedInteger;
            case ZapDataType.LONG :
                return Long;
            case ZapDataType.UNSIGNED_LONG :
                return UnsignedLong;
            case ZapDataType.FLOAT :
                return Float;
            case ZapDataType.DOUBLE :
                return Double;
            case ZapDataType.BINARY :
                return Binary;
            case ZapDataType.STRING :
                return String;

            default :
                throw new IllegalArgumentException("Unknown data type number: " + typeNumber);
        }
    }
}
