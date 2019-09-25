package zedi.pacbridge.app.events;

import java.text.ParseException;

import org.jdom2.Element;

import zedi.pacbridge.net.DataType;
import zedi.pacbridge.net.DataTypeFactory;
import zedi.pacbridge.net.Value;

public class WriteValueElement extends EventElement {
    public static final String ROOT_ELEMENT_NAME = "WriteValue";
    public static final String VALUE_TAG = "Value";
    public static final String DATA_TYPE_TAG = "DataType";
    public static final String INDEX_TAG = "Index";    

    private Long index;
    private Value value;

    public WriteValueElement(Long index, Value value) {
        this.index = index;
        this.value = value;
    }

    public DataType getDataType() {
        return value.dataType();
    }

    public Long getIndex() {
        return index;
    }

    public Value getValue() {
        return value;
    }
    
    public Element asElement() {
        Element element = new Element(ROOT_ELEMENT_NAME);
        element.addContent(new Element(INDEX_TAG).setText(index.toString()));
        element.addContent(new Element(DATA_TYPE_TAG).setText(value.dataType().getName()));
        element.addContent(new Element(VALUE_TAG).setText(value.toString()));
        return element;
    }
    
    public static WriteValueElement writeValueForElement(Element element, DataTypeFactory dataTypeFactory) throws InvalidEventFormatException {
        String dataTypeString = requiredValueFromElement(DATA_TYPE_TAG, element);
        DataType dataType = dataTypeFactory.dataTypeForName(dataTypeString);
        Long index = Long.valueOf(requiredValueFromElement(INDEX_TAG, element));
        String valueString = requiredValueFromElement(VALUE_TAG, element);
        Value value;
        try {
            value = dataType.valueForString(valueString);
        } catch (ParseException e) {
            throw new InvalidEventFormatException("Unable to parse value string", e);
        }
        return new WriteValueElement(index, value);
    }
}
