package zedi.pacbridge.eventgen.zios.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

import zedi.pacbridge.zap.messages.FieldType;

class FieldTypeListModel extends AbstractListModel<FieldType> {
    private static final Comparator<FieldType> COMPARATOR = new Comparator<FieldType>() {
        @Override
        public int compare(FieldType fieldType1, FieldType fieldType2) {
            return fieldType1.getName().compareTo(fieldType2.getName());
        }
        
    };
    
    private List<FieldType> fieldTypes;
    
    
    public FieldTypeListModel(){
        this.fieldTypes = new ArrayList<>();
    }

    public int getSize(){
        return fieldTypes.size();
    }

    public FieldType getElementAt(int index){
        return (FieldType)fieldTypes.get(index);
    }

    public List<FieldType> getFieldTypeList(){
        return fieldTypes;
    }

    public void setList(List<FieldType> array){
        this.fieldTypes = array;
    }

    public void sort() {
        Collections.sort(fieldTypes, COMPARATOR);
        fireContentsChanged(this, 0, fieldTypes.size());
    }

    public void addElement(FieldType fieldType) {
        fieldTypes.add(fieldType);
    }

    public void clear() {
        fieldTypes.clear();
        fireContentsChanged(this, 0, fieldTypes.size());
    }

    public void removeElement(FieldType fieldType) {
        fieldTypes.remove(fieldType);
    }

    public FieldType elementAt(int i) {
        return getElementAt(i);
    }    
}
