package zedi.swingutl.beans;

import javax.swing.JScrollPane;

public class ScrollingConsolePane extends JScrollPane {

    private ConsoleTextPane consoleTextPane;
    
    public ScrollingConsolePane() {
        consoleTextPane = new ConsoleTextPane();
        setViewportView(consoleTextPane);
    }
    
    public ConsoleTextPane getConsoleTextPane() {
        return consoleTextPane;
    }
}
