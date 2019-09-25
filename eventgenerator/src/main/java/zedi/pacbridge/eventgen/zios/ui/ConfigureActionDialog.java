package zedi.pacbridge.eventgen.zios.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import zedi.pacbridge.utl.Utilities;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureActionDialog extends JDialog {
    private final static Vector<ObjectType> objecTypes = new Vector<ObjectType>(Utilities.listOfObjecType(ObjectType.class));

    private final JPanel contentPanel = new JPanel();
    private JList<FieldType> toBeSelectedList;
    private FieldTypeListModel toBeSelectedListModel;
    private JList<FieldType> selectedList;
    private FieldTypeListModel selectedListModel;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            ConfigureActionDialog dialog = new ConfigureActionDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Inject
    public ConfigureActionDialog(FieldTypeLibrary fieldTypeLibrary) {
        this();
        toBeSelectedListModel = new FieldTypeListModel();
        selectedListModel = new FieldTypeListModel();
        for (FieldType fieldType : fieldTypeLibrary.getFieldTypes())
            toBeSelectedListModel.addElement(fieldType);
        toBeSelectedListModel.sort();
        toBeSelectedList.setModel(toBeSelectedListModel);
        selectedList.setModel(selectedListModel);
    }
    

    /**
     * Create the dialog.
     */
    public ConfigureActionDialog() {
        setTitle("Create Action");
        setBounds(100, 100, 580, 425);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            JPanel toSelectPanel = new JPanel();
            toSelectPanel.setBounds(20, 93, 150, 250);
            contentPanel.add(toSelectPanel);
            toSelectPanel.setLayout(new BorderLayout(0, 0));
            {
                JScrollPane scrollPane = new JScrollPane();
                toSelectPanel.add(scrollPane, BorderLayout.CENTER);
                {
                    toBeSelectedList = new JList();
                    toBeSelectedList.setLocation(0, 31);
                    scrollPane.setViewportView(toBeSelectedList);
                }
            }
            {
                JPanel selectedPanel = new JPanel();
                selectedPanel.setBounds(334, 93, 150, 250);
                contentPanel.add(selectedPanel);
                selectedPanel.setLayout(new BorderLayout(0, 0));
                {
                    JScrollPane scrollPane = new JScrollPane();
                    selectedPanel.add(scrollPane, BorderLayout.CENTER);
                    {
                        selectedList = new JList();
                        scrollPane.setViewportView(selectedList);
                    }
                }
            }
            {
                JPanel selectButtonsPanel = new JPanel();
                selectButtonsPanel.setBounds(212, 128, 79, 166);
                contentPanel.add(selectButtonsPanel);
                selectButtonsPanel.setLayout(new GridLayout(4, 0, 0, 20));
                {
                    JButton moveAllToSelectedButton = new JButton(">>");
                    moveAllToSelectedButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            for (int i = 0; i < toBeSelectedListModel.getSize(); i++)
                                selectedListModel.addElement(toBeSelectedListModel.elementAt(i));
                            selectedListModel.sort();
                            toBeSelectedListModel.clear();
                        }
                    });
                    selectButtonsPanel.add(moveAllToSelectedButton);
                }
                {
                    JButton moveSingleToSelectedButton = new JButton(">");
                    moveSingleToSelectedButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            List<FieldType> selectedValuesList = toBeSelectedList.getSelectedValuesList();
                            for (FieldType fieldType : selectedValuesList) {
                                toBeSelectedListModel.removeElement(fieldType);
                                selectedListModel.addElement(fieldType);
                            }
                            selectedListModel.sort();
                            toBeSelectedListModel.sort();
                            toBeSelectedList.clearSelection();
                        }
                    });
                    selectButtonsPanel.add(moveSingleToSelectedButton);
                }
                {
                    JButton removeSingleFromSelectedButton = new JButton("<");
                    removeSingleFromSelectedButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            List<FieldType> selectedValuesList = selectedList.getSelectedValuesList();
                            for (FieldType fieldType : selectedValuesList) {
                                selectedListModel.removeElement(fieldType);
                                toBeSelectedListModel.addElement(fieldType);
                            }
                            selectedListModel.sort();
                            toBeSelectedListModel.sort();
                            selectedList.clearSelection();
                        }
                    });
                    selectButtonsPanel.add(removeSingleFromSelectedButton);
                }
                {
                    JButton moveAllLeftButton = new JButton("<<");
                    moveAllLeftButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            for (int i = 0; i < selectedListModel.getSize(); i++)
                                toBeSelectedListModel.addElement(selectedListModel.elementAt(i));
                            selectedListModel.clear();
                            toBeSelectedListModel.sort();
                        }
                    });
                    selectButtonsPanel.add(moveAllLeftButton);
                }
            }
        }
        
        JComboBox<ObjectType> comboBox = new JComboBox<ObjectType>(objecTypes);
        comboBox.setBounds(103, 40, 150, 20);
        contentPanel.add(comboBox);
        
        JLabel lblObjecttype = new JLabel("ObjectType");
        lblObjecttype.setHorizontalAlignment(SwingConstants.RIGHT);
        lblObjecttype.setBounds(10, 43, 83, 14);
        contentPanel.add(lblObjecttype);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
}
