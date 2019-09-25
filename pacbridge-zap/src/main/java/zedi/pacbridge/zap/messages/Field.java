package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.io.Unsigned;


public class Field<T> implements Serializable  {
    private static final Logger logger = LoggerFactory.getLogger(Field.class.getName());

    private static final IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
    private static final F32TypeSerializer f32TypeSerializer = new F32TypeSerializer();
    private static final StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();

    public static final IntegerTypeDeserializer integerTypeDeserializer = new IntegerTypeDeserializer();
    public static final F32TypeDeserializer f32TypeDeserializer = new F32TypeDeserializer();
    public static final StringTypeDeserializer stringTypeDeserializer = new StringTypeDeserializer();

    private static final Integer FIXED_SIZE = 3;

    private FieldType fieldType;
    private T value;
    private TypeSerializer<T> typeSerializer;
    private TypeDeserializer<T> typeDeserializer;

    private Field(FieldType fieldType, TypeSerializer<T> typeSerializer, TypeDeserializer<T> typeDeserializer) {
        this.fieldType = fieldType;
        this.typeSerializer = typeSerializer;
        this.typeDeserializer = typeDeserializer;
    }

    private Field(FieldType fieldType, T value, TypeSerializer<T> typeSerializer, TypeDeserializer<T> typeDeserializer) {
        this.fieldType = fieldType;
        this.typeSerializer = typeSerializer;
        this.typeDeserializer = typeDeserializer;
        this.value = value;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public T getValue() {
        return value;
    }
    
    /**
     * Serialize a Field into a ByteBuffer. The format of the serialized bytes
     * is [encodedType] - 2 bytes [field] - Variable length, depending on the
     * data type.
     * 
     * @param byteBuffer
     */
    public void serialize(ByteBuffer byteBuffer) {
        typeSerializer.serialize(byteBuffer, fieldType.getTag(), value);
    }

    public Element asElement() {
        Element element = new Element(fieldType.getName());
        typeSerializer.serialize(element, value);
        return element;
    }

    public int size() {
        return typeSerializer.serializedSizeOfValue(value);
    }
    
    private void takeValueFromElement(Element element) throws ParseException {
        value = typeDeserializer.deserialize(element);
    }

    private void deserialize(ByteBuffer byteBuffer, FieldDataType dataType) {
        value = typeDeserializer.deserialize(byteBuffer, dataType);
    }

    /**
     * Returns a field from a ByteBuffer.
     * 
     * @param byteBuffer
     *            A ByteBuffer
     * 
     * @return Field<?> - A field for the specifiec type. If the field tag name
     *         is a known tag name it will be a valid Field object. If the tag
     *         name is unknown, null will be returned and the approperiate
     *         number of bytes will be skipped in the byte buffer, depending on
     *         the type.
     * 
     */
    public static Field<?> fieldFromByteBuffer(ByteBuffer byteBuffer, FieldTypeLibrary library) {
        Field<?> field = null;
        short foo = byteBuffer.getShort();
        int tag = TypeNumberEncoder.tagNumberFromEncodedValue(foo);
        int type = TypeNumberEncoder.typeNumberFromEncodedValue(foo);
        FieldDataType dataType = FieldDataType.fieldTypeForNumber(type);
        if (dataType == null) {
            throw new IllegalArgumentException("Invalid data type in byte stream: '" + type + "'");
        } else {
            FieldType fieldType = library.fieldTypeForTag(tag);
            if (fieldType == null) {
                logger.warn("Unknown Configure Field tag encountered: '" + tag + ". Field discarded.");
                int skipBytes = 0;
                switch (type) {
                    case FieldDataType.S8_NUMBER :
                        skipBytes = 1;
                        break;

                    case FieldDataType.S16_NUMBER :
                        skipBytes = 2;
                        break;

                    case FieldDataType.S32_NUMBER :
                    case FieldDataType.F32_NUMBER :
                        skipBytes = 4;
                        break;

                    case FieldDataType.S48_NUMBER :
                        skipBytes = 6;
                        break;

                    case FieldDataType.S64_NUMBER :
                        skipBytes = 8;
                        break;

                    case FieldDataType.STRING_NUMBER :
                        skipBytes = Unsigned.getUnsignedShort(byteBuffer);
                        break;
                }
                byteBuffer.position(byteBuffer.position() + skipBytes);
            } else {
                field = fieldForFieldType(fieldType);
                field.deserialize(byteBuffer, dataType);
            }
        }
        return field;
    }

    public static Field<?> fieldForFieldTypeAndValue(FieldType fieldType, Object value) {
        Field<?> field = null;
        switch (fieldType.getDataType().getNumber()) {
            case FieldDataType.S8_NUMBER :
            case FieldDataType.S16_NUMBER :
            case FieldDataType.S32_NUMBER :
            case FieldDataType.S48_NUMBER :
            case FieldDataType.S64_NUMBER :
                field = new Field<Long>(fieldType, ((Number)value).longValue(), integerTypeSerializer, integerTypeDeserializer);
                break;

            case FieldDataType.F32_NUMBER :
                field = new Field<Float>(fieldType, (Float)value, f32TypeSerializer, f32TypeDeserializer);
                break;

            case FieldDataType.STRING_NUMBER :
                field = new Field<String>(fieldType, (String)value, stringTypeSerializer, stringTypeDeserializer);
                break;
        }
        return field;
    }
    
    
    public static final Field<?> fieldForElement(Element element, FieldTypeLibrary fieldTypeLibrary) throws ParseException {
        String name = element.getName();
        Field<?> field = null;
        FieldType fieldType = fieldTypeLibrary.fieldTypeForName(name);
        if (fieldType == null) {
            logger.warn("Unknown Configure Field name encountered: '" + name + ". Field discarded.");
        } else {
            field = fieldForFieldType(fieldType);
            field.takeValueFromElement(element);
        }
        return field;
    }

    private static Field<?> fieldForFieldType(FieldType fieldType) {
        Field<?> field = null;
        switch (fieldType.getDataType().getNumber()) {
            case FieldDataType.S8_NUMBER :
            case FieldDataType.S16_NUMBER :
            case FieldDataType.S32_NUMBER :
            case FieldDataType.S48_NUMBER :
            case FieldDataType.S64_NUMBER :
                field = new Field<Long>(fieldType, integerTypeSerializer, integerTypeDeserializer);
                break;

            case FieldDataType.F32_NUMBER :
                field = new Field<Float>(fieldType, f32TypeSerializer, f32TypeDeserializer);
                break;

            case FieldDataType.STRING_NUMBER :
                field = new Field<String>(fieldType, stringTypeSerializer, stringTypeDeserializer);
                break;
        }
        return field;
    }
}