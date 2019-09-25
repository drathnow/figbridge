package zedi.pacbridge.eventgen.zios.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class MainFrame extends JFrame implements ActionListener {
    
    public MainFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent quitEvent) {
        this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

}
