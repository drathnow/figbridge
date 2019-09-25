package zedi.pacbridge.gdn;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.ParseException;

import zedi.pacbridge.net.DataType;
import zedi.pacbridge.utl.HexStringDecoder;


public class GdnDataType implements DataType, Serializable {
    private static final long serialVersionUID = 1001;
    public static final int UNBOUNDED_SIZE = -1;

    public static final int NUMBER_FOR_TYPE_EMPTY_VALUE = 0;
    public static final int NUMBER_FOR_TYPE_DISCRETE = 1;
    public static final int NUMBER_FOR_TYPE_BYTE = 2;
    public static final int NUMBER_FOR_TYPE_UNSIGNED_BYTE = 3;
    public static final int NUMBER_FOR_TYPE_INTEGER = 4;
    public static final int NUMBER_FOR_TYPE_UNSIGNED_INTEGER = 5;
    public static final int NUMBER_FOR_TYPE_LONG = 6;
    public static final int NUMBER_FOR_TYPE_UNSIGNED_LONG = 7;
    
    public static final int NUMBER_FOR_TYPE_FLOAT = 8;
    public static final int NUMBER_FOR_TYPE_BLOB = 11;

    public static final GdnDataType EmptyValue = new GdnDataType("EmptyValue", NUMBER_FOR_TYPE_EMPTY_VALUE, 0, null);
    public static final GdnDataType Discrete = new GdnDataType("Discrete", NUMBER_FOR_TYPE_DISCRETE, 1, new NumericConverter<Integer>(0, 1, GdnDiscrete.class));
    public static final GdnDataType Byte = new GdnDataType("Byte", NUMBER_FOR_TYPE_BYTE, 1, new NumericConverter<Integer>(-128, 127, GdnByte.class));
    public static final GdnDataType UnsignedByte = new GdnDataType("UnsignedByte", NUMBER_FOR_TYPE_UNSIGNED_BYTE, 1, new NumericConverter<Integer>(-128, 127, GdnUnsignedByte.class));
    public static final GdnDataType Integer = new GdnDataType("Integer", NUMBER_FOR_TYPE_INTEGER, 2, new NumericConverter<Integer>(java.lang.Short.MIN_VALUE, java.lang.Short.MAX_VALUE, GdnInteger.class));
    public static final GdnDataType UnsignedInteger = new GdnDataType("UnsignedInteger", NUMBER_FOR_TYPE_UNSIGNED_INTEGER, 2, new NumericConverter<Integer>(java.lang.Short.MIN_VALUE, java.lang.Short.MAX_VALUE, GdnUnsignedInteger.class));
    public static final GdnDataType Long = new GdnDataType("Long", NUMBER_FOR_TYPE_LONG, 4, new NumericConverter<Integer>(java.lang.Integer.MIN_VALUE, java.lang.Integer.MAX_VALUE, GdnLong.class));
    public static final GdnDataType UnsignedLong = new GdnDataType("UnsignedLong", NUMBER_FOR_TYPE_UNSIGNED_LONG, 4, new NumericConverter<Long>(0L, (long)0xFFFFFFFFL, GdnUnsignedLong.class));
    public static final GdnDataType Float = new GdnDataType("Float", NUMBER_FOR_TYPE_FLOAT, 4, new NumericConverter<Float>(java.lang.Float.NEGATIVE_INFINITY, java.lang.Float.POSITIVE_INFINITY, GdnFloat.class)); 
    public static final GdnDataType Binary = new GdnDataType("Binary", NUMBER_FOR_TYPE_BLOB, UNBOUNDED_SIZE, new BlobConverter());
    
    private static interface Converter<T> extends Serializable {
        public GdnValue<?> convert(GdnDataType type, String s) throws ParseException;
    }
    
    private static class NumericConverter<T extends Number> implements Converter<T> {
        private static final long serialVersionUID = 1001;
        private Number lowerLimit;
        private Number upperLimit;
        private Class<? extends GdnValue<?>> factoryClass;

        NumericConverter(Number lowerLimit, Number upperLimit, Class<? extends GdnValue<?>> factoryClass) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.factoryClass = factoryClass;
        }

        public GdnValue<?> convert(GdnDataType type, String s) throws ParseException {
            DecimalFormat format = new DecimalFormat();
            Number value = format.parse(s);
            if (isValidValueForMe(value) == false)
                throw new ParseException("Value " + value.toString() + " is invalid for " + type.getName(), 0);
            try {
                Constructor<?> constructor = factoryClass.getConstructor(Number.class);
                return (GdnValue<?>)constructor.newInstance(value);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(factoryClass.getName() + " does not have required constructor");
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Unable to invoke GdnValue type constructor");
            }
        }
        
        private boolean isValidValueForMe(Number value) {
            return value.doubleValue() >= lowerLimit.doubleValue() && value.doubleValue() <= upperLimit.doubleValue();  
        }
    }
    

    private static class BlobConverter implements Converter<byte[]> {
        private static final long serialVersionUID = 1001;
        public GdnValue<?> convert(GdnDataType type, String s) throws ParseException {
            return new GdnBlob(HexStringDecoder.hexStringAsBytes(s));
        }
    }

    private String name;
    private Integer typeNumber;
    private Integer size;
    private Converter<?> converter;
    
    private GdnDataType(String name, Integer typeNumber, Integer size, Converter<?> converter) {
        this.name = name;
        this.size = size;
        this.typeNumber = typeNumber;
        this.converter = converter;
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public Integer getNumber() {
        return typeNumber;
    }
    
    @Override
    public String toString() {
        return name + '(' + typeNumber + ')';
    }

    @SuppressWarnings("rawtypes")
    public GdnValue valueForString(String string) throws ParseException {
        return typeNumber == NUMBER_FOR_TYPE_EMPTY_VALUE ? new GdnEmptyValue() : converter.convert(this, string);
    }
    
    public static GdnDataType dataTypeForName(String name) {
        if (name.equals(EmptyValue.name))
            return EmptyValue;
        if (name.equals(Discrete.name))
            return Discrete;
        if (name.equals(Byte.name))
            return Byte;
        if (name.equals(UnsignedByte.name))
            return UnsignedByte;
        if (name.equals(Integer.name))
            return Integer;
        if (name.equals(UnsignedInteger.name))
            return UnsignedInteger;
        if (name.equals(Long.name))
            return Long;
        if (name.equals(UnsignedLong.name))
            return UnsignedLong;
        if (name.equals(Float.name))
            return Float;
        if (name.equals(Binary.name))
            return Binary;
        return null;
    }
    
    public static GdnDataType dataTypeForTypeNumber(Integer typeNumber) {
        switch (typeNumber) {
            case GdnDataType.NUMBER_FOR_TYPE_EMPTY_VALUE :
                return EmptyValue;
            case GdnDataType.NUMBER_FOR_TYPE_DISCRETE :
                return Discrete;
            case GdnDataType.NUMBER_FOR_TYPE_BYTE :
                return Byte;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE :
                return UnsignedByte;
            case GdnDataType.NUMBER_FOR_TYPE_INTEGER :
                return Integer;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER :
                return UnsignedInteger;
            case GdnDataType.NUMBER_FOR_TYPE_LONG :
                return Long;
            case GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG :
                return UnsignedLong;
            case GdnDataType.NUMBER_FOR_TYPE_FLOAT :
                return Float;
            case GdnDataType.NUMBER_FOR_TYPE_BLOB :
                return Binary;

            default :
                return null;
        }
    }
}
