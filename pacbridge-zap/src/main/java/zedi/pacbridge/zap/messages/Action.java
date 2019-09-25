package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.io.Unsigned;


public class Action implements Serializable {
	private static final long serialVersionUID = 1001L;
	
    private static final Logger logger = LoggerFactory.getLogger(Action.class.getName());
    public static final String ROOT_ELEMENT_NAME = "Action";
    public static final String TYPE_TAG = "type";
    public static final Integer FIXED_SIZE = 3;
    
    private ActionType actionType;
    private List<Field<?>> fields;
    
    public Action(ActionType actionType, List<Field<?>> fields) {
        this.actionType = actionType;
        this.fields = fields;
    }
    
    public Element asElement() {
        Element element = new Element(ROOT_ELEMENT_NAME);
        element.setAttribute(TYPE_TAG, actionType.getName());
        for (Field<?> field : fields)
            element.addContent(field.asElement());
        return element;
    }
    
    public ActionType getActionType() {
        return actionType;
    }
    
    public List<Field<?>> getFields() {
        return fields;
    }
    
    /**
     * [actionType] - 1 byte.
     * [fieldCount] - 2 bytes
     * [field1]...[fieldn]
     * 
     * @param byteBuffer
     */
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(actionType.getNumber().byteValue()); 
        byteBuffer.putShort((short)fields.size());
        for (Field<?> field : fields)
            field.serialize(byteBuffer);
    }

    public Integer size() {
        int total = FIXED_SIZE;
        for (Field<?> field : fields)
            total += field.size();
        return total;
    }
    
    public static final Action actionFromElement(Element actionElement, FieldTypeLibrary library) throws ParseException {
        ActionType actionType = ActionType.actionTypeForName(actionElement.getAttributeValue(TYPE_TAG));
        List<Field<?>> fields = new ArrayList<Field<?>>();
        List<Element> elements = actionElement.getChildren();
        for (Element element : elements) {
            Field<?> field = Field.fieldForElement(element, library);
            if (field == null)
                logger.warn("Unable to convert element '" + actionElement.getText() + "' to Field.  Element will be discarded");
            else
                fields.add(field);
        }
        return new Action(actionType, fields);
    }
    
    public static final Action actionFromByteBuffer(ByteBuffer byteBuffer, FieldTypeLibrary library) {
        ActionType actionType = ActionType.actionTypeForNumber((int)byteBuffer.get());
        int count = Unsigned.getUnsignedShort(byteBuffer);
        List<Field<?>> fields = new ArrayList<Field<?>>();
        for (int i = 0; i < count; i++) {
            Field<?> field = Field.fieldFromByteBuffer(byteBuffer, library);
            if (field != null)
                fields.add(field);
        }
        return new Action(actionType, fields);
    }
}
