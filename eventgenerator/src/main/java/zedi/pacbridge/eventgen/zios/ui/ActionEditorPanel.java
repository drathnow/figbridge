package zedi.pacbridge.eventgen.zios.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
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
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

import zedi.pacbridge.eventgen.InjectModel;
import zedi.pacbridge.utl.Utilities;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ActionEditorPanel extends JPanel {
    private final static Vector<ActionType> actionTypes = new Vector<ActionType>(Utilities.listOfObjecType(ActionType.class));

    private JList<FieldType> toBeSelectedList = new JList<FieldType>();
    private FieldTypeListModel toBeSelectedListModel = new FieldTypeListModel();
    private JList<FieldType> selectedList = new JList<FieldType>();
    private FieldTypeListModel selectedListModel = new FieldTypeListModel();
    private JComboBox<ActionType> actionTypeComboBox ;
    
    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%m%n")));
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        final Injector injector = Guice.createInjector(new InjectModel());
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JDialog dialog = new JDialog();
                    dialog.setTitle("Create Action");
                    dialog.setBounds(100, 100, 580, 425);
                    dialog.getContentPane().setLayout(new BorderLayout());
                    dialog.getContentPane().add(injector.getInstance(ActionEditorPanel.class), BorderLayout.CENTER);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    @Inject
    public ActionEditorPanel(FieldTypeLibrary fieldTypeLibrary) {
        this();
        for (FieldType fieldType : fieldTypeLibrary.getFieldTypes())
            toBeSelectedListModel.addElement(fieldType);
        toBeSelectedList.setModel(toBeSelectedListModel);
        selectedList.setModel(selectedListModel);
        toBeSelectedListModel.sort();
    }

    /**
     * Create the panel.
     */
    public ActionEditorPanel() {
        setLayout(null);
        
        JPanel toBeSelectedPanel = new JPanel();
        toBeSelectedPanel.setBounds(35, 100, 150, 250);
        add(toBeSelectedPanel);
        toBeSelectedPanel.setLayout(new BorderLayout(0, 0));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(237, 128, 78, 176);
        add(buttonPanel);
        buttonPanel.setLayout(new GridLayout(4, 0, 0, 20));
        
        JButton moveAllToSelectedButton = new JButton(">>");
        moveAllToSelectedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < toBeSelectedListModel.getSize(); i++)
                    selectedListModel.addElement(toBeSelectedListModel.elementAt(i));
                selectedListModel.sort();
                toBeSelectedListModel.clear();
            }
        });
        buttonPanel.add(moveAllToSelectedButton);
        
        JButton moveSelectedToSelectedButton = new JButton(">");
        moveSelectedToSelectedButton.addActionListener(new ActionListener() {
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
        buttonPanel.add(moveSelectedToSelectedButton);
        
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
        buttonPanel.add(removeSingleFromSelectedButton);
        
        JButton removeAllFromSelectedButton = new JButton("<<");
        removeAllFromSelectedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < selectedListModel.getSize(); i++)
                    toBeSelectedListModel.addElement(selectedListModel.elementAt(i));
                selectedListModel.clear();
                toBeSelectedListModel.sort();
            }
        });
        buttonPanel.add(removeAllFromSelectedButton);
        
        JScrollPane toBeSelectedScrollPane = new JScrollPane();
        toBeSelectedPanel.add(toBeSelectedScrollPane);
        
        toBeSelectedScrollPane.setViewportView(toBeSelectedList);

        toBeSelectedPanel.add(toBeSelectedScrollPane, BorderLayout.CENTER);
        
        
        JPanel selectedPanel = new JPanel();
        selectedPanel.setBounds(378, 100, 150, 250);
        add(selectedPanel);
        selectedPanel.setLayout(new BorderLayout(0, 0));
        
        JScrollPane selectedScrollPane = new JScrollPane();
        selectedPanel.add(selectedScrollPane);
        
        selectedScrollPane.setViewportView(selectedList);
        
        JLabel lblNewLabel = new JLabel("Action Type");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(35, 49, 78, 14);
        add(lblNewLabel);
        
        actionTypeComboBox = new JComboBox<ActionType>(actionTypes);
        actionTypeComboBox.setBounds(123, 46, 78, 20);
        add(actionTypeComboBox);
    }
    
    public ActionType getSelectedActionType() {
        return actionTypeComboBox.getItemAt(actionTypeComboBox.getSelectedIndex());
    }
    
    public List<FieldType> getSelectedFieldTypes() {
        return selectedListModel.getFieldTypeList();
    }
    
}
