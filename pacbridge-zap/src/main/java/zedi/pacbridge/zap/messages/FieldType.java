package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import org.jdom2.Element;

import zedi.pacbridge.utl.NamedType;


public class FieldType extends NamedType implements Serializable, Comparable<FieldType> {
    public static final String ROOT_ELEMENT_NAME = "FieldType";
    public static final String TAG_TAG = "tag";
    public static final String TYPE_TAG = "type";
    
    private FieldDataType dataType;
    
   public FieldType(String name, Integer tag, FieldDataType dataType) {
        super(name, tag);
        this.dataType = dataType;
    }
    
    public Integer getTag() {
        return getNumber();
    }
    
    public FieldDataType getDataType() {
        return dataType;
    }
    
    public String toString() {
        return getName();
    };
    
    public static FieldType fieldTypeForElement(Element element) throws IllegalArgumentException {
        Integer tag = null;
        FieldDataType type = null;
        String name = null;
        name = element.getText();
        if (name == null || name.trim().length() == 0)
            throw new IllegalArgumentException("Missing value for FieldType element");

        try {
            tag = Integer.parseInt(element.getAttributeValue(TAG_TAG));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid FieldType element named '" 
                                                + name
                                                + "': tag value is invalid: '" 
                                                + element.getAttributeValue(TAG_TAG) 
                                                + "'");
        }
        
        try {
            type = FieldDataType.fieldDataTypeForName(element.getAttributeValue(TYPE_TAG));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid FieldType element named '" 
                                                + name
                                                + "': type value is invalid: '" 
                                                + element.getAttributeValue(TYPE_TAG) 
                                                + "'");
        }
        
        return new FieldType(name, tag, type);
    }

    @Override
    public int compareTo(FieldType otherFieldType) {
        return otherFieldType.getTag().compareTo(getTag());
    }
}