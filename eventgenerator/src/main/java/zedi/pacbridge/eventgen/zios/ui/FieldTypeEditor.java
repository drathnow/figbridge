package zedi.pacbridge.eventgen.zios.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import zedi.pacbridge.zap.messages.FieldType;

public class FieldTypeEditor extends JPanel {
    private JTextField valueTextField;
    private FieldType fieldType;
    private JLabel fieldNameLabel;
    
    /**
     * Create the panel.
     */
    public FieldTypeEditor() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
        fieldNameLabel = new JLabel("New label");
        fieldNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_fieldNameLabel = new GridBagConstraints();
        gbc_fieldNameLabel.anchor = GridBagConstraints.EAST;
        gbc_fieldNameLabel.insets = new Insets(0, 0, 0, 5);
        gbc_fieldNameLabel.gridx = 0;
        gbc_fieldNameLabel.gridy = 0;
        add(fieldNameLabel, gbc_fieldNameLabel);
        
        valueTextField = new JTextField();
        GridBagConstraints gbc_valueTextField = new GridBagConstraints();
        gbc_valueTextField.gridwidth = 2;
        gbc_valueTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_valueTextField.gridx = 1;
        gbc_valueTextField.gridy = 0;
        add(valueTextField, gbc_valueTextField);
        valueTextField.setColumns(10);
    }

    public FieldTypeEditor(FieldType fieldType) {
        this();
        this.fieldType = fieldType;
        this.fieldNameLabel.setText(fieldType.getName());
    }
    
}
