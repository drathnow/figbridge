package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.util.Collection;

public interface FieldTypeLibrary extends Serializable {
    public FieldType fieldTypeForName(String name);
    public FieldType fieldTypeForTag(Integer tag);
    public Collection<FieldType> getFieldTypes();
}
