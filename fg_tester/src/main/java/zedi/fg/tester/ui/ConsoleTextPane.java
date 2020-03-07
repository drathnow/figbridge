package zedi.fg.tester.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

public class ConsoleTextPane extends JTextPane
{
	private final static Logger logger = Logger.getLogger(ConsoleTextPane.class.getName());

	protected final static int MAX_LENGTH = 1000000;
	private final DefaultStyledDocument outputDocument;
	private static SimpleAttributeSet infoAttributes;
	private static SimpleAttributeSet errorAttributes;
	private static SimpleAttributeSet warnAttributes;
	private static SimpleAttributeSet debugAttributes;
	private static SimpleAttributeSet traceAttributes;
	private PopupMenuListener popupMenuListener;

	public ConsoleTextPane()
	{
		outputDocument = new DefaultStyledDocument();
		setStyledDocument(outputDocument);
		setFont(new Font("Courier", Font.PLAIN, 12));
		infoAttributes = new SimpleAttributeSet();
		infoAttributes.addAttribute(StyleConstants.Foreground, Color.BLACK);
		errorAttributes = new SimpleAttributeSet();
		errorAttributes.addAttribute(StyleConstants.Foreground, Color.RED);
		warnAttributes = new SimpleAttributeSet();
		warnAttributes.addAttribute(StyleConstants.Foreground, Color.BLUE);
		debugAttributes = new SimpleAttributeSet();
		debugAttributes.addAttribute(StyleConstants.Foreground, Color.BLUE.darker().darker());
		traceAttributes = new SimpleAttributeSet();
		traceAttributes.addAttribute(StyleConstants.Foreground, Color.RED.darker().darker());
		JPopupMenu popupMenu = popupMenu();
		popupMenuListener = new PopupMenuListener(popupMenu);
		addMouseListener(popupMenuListener);
	}

	public void addOutput(String message, AttributeSet attributeSet)
	{
		synchronized (outputDocument)
		{
			try
			{
				outputDocument.insertString(outputDocument.getLength(), message, attributeSet);
				setCaretPosition(outputDocument.getLength());
				if (outputDocument.getLength() > MAX_LENGTH)
					outputDocument.remove(0, outputDocument.toString().indexOf("\n"));
			} catch (BadLocationException ble)
			{
				ble.printStackTrace();
			}
		}
	}

	public void clear()
	{
		try
		{
			getDocument().remove(0, getDocument().getLength());
		} catch (BadLocationException ex)
		{
			logger.error("Unexpected exception during clear", ex);
		}
	}

	public void addInfoOutput(String outputText)
	{
		addOutput(outputText, infoAttributes);
	}

	public void addErrorOutput(String outputText)
	{
		addOutput(outputText, errorAttributes);
	}

	public void addWarnOutput(String outputText)
	{
		addOutput(outputText, warnAttributes);
	}

	public void addDebugOutput(String outputText)
	{
		addOutput(outputText, debugAttributes);
	}

	public void addTraceOutput(String loggingString)
	{
		addOutput(loggingString, traceAttributes);
	}

	private JPopupMenu popupMenu()
	{
		JPopupMenu menu = new JPopupMenu();
		menu.add(new CopyMenuItem());
		menu.add(new SelectAllMenuItem());
		menu.add(new JPopupMenu.Separator());
		menu.add(new ClearMenuItem());
		return menu;
	}

	private class ClearMenuItem extends JMenuItem implements ActionListener
	{
		public ClearMenuItem()
		{
			super("Clear");
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent)
		{
			clear();
		}
	}

	private class CopyMenuItem extends JMenuItem
	{

		public CopyMenuItem()
		{
			super("Copy");
			addActionListener(actionWithName(DefaultEditorKit.copyAction, ConsoleTextPane.this));
			setEnabled(ConsoleTextPane.this.getSelectedText() != null);
		}

		private Action actionWithName(String actionName, JTextPane textPane)
		{
			Action[] actions = textPane.getActions();
			for (int i = 0; i < actions.length; i++)
				if (actions[i].getValue(Action.NAME).equals(actionName))
					return actions[i];
			return null;
		}
	}

	private class SelectAllMenuItem extends JMenuItem
	{

		public SelectAllMenuItem()
		{
			super("SelectAll");
			addActionListener(actionWithName(DefaultEditorKit.selectAllAction, ConsoleTextPane.this));
		}

		private Action actionWithName(String actionName, JTextPane textPane)
		{
			Action[] actions = textPane.getActions();
			for (int i = 0; i < actions.length; i++)
				if (actions[i].getValue(Action.NAME).equals(actionName))
					return actions[i];
			return null;
		}
	}

	private class PopupMenuListener extends MouseAdapter
	{

		protected JPopupMenu popupMenu;

		public PopupMenuListener(JPopupMenu popupMenu)
		{
			this.popupMenu = popupMenu;
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				popupMenu.validate();
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
