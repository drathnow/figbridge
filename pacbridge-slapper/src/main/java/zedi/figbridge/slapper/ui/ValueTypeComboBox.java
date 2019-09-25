package zedi.figbridge.slapper.ui;

import javax.swing.JComboBox;

import zedi.figdevice.emulator.utl.ValueType;

public class ValueTypeComboBox extends JComboBox<ValueType> {

    private static final ValueType[] valueTypes = new ValueType[] { ValueType.FIXED, ValueType.RANDOM };
    
    public ValueTypeComboBox() {
        super(valueTypes);
        setSelectedItem(null);
    }

}
