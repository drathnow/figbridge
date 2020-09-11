package zedi.pacbridge.messagedecoder;

import java.awt.Dimension;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.varia.LevelRangeFilter;

import zedi.pacbridge.app.events.zios.ZiosFieldTypeLibrary;
import zedi.pacbridge.messagedecoder.ui.MainWindow;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.swingutl.beans.TextPaneAppender;
import zedi.swingutl.beans.utl.SwingUtl;

public class Main
{
    static NotificationCenter notificationCenter = new NotificationCenter();
    static MainController mainController = new MainController(notificationCenter, loadFieldTypeLibrary());

    public static void main( String[] args )
    {
        PatternLayout layout = new PatternLayout("%m%n");
        SwingUtl.initLookAndFeel();
        MainWindow mainWindow = new MainWindow(notificationCenter);
        TextPaneAppender traceAppender = new TextPaneAppender(mainWindow.getTraceTextPane(), layout);

        LevelRangeFilter traceRangeFilter = new LevelRangeFilter();
        traceRangeFilter.setLevelMin(Level.TRACE);
        traceRangeFilter.setLevelMax(Level.TRACE);
        traceRangeFilter.setAcceptOnMatch(true);

        traceAppender.addFilter(traceRangeFilter);
        Logger.getRootLogger().addAppender(traceAppender);

        TextPaneAppender outputAppender = new TextPaneAppender(mainWindow.getOutputTextPane(), layout);
        LevelRangeFilter outputRangeFilter = new LevelRangeFilter();
        outputRangeFilter.setLevelMin(Level.INFO);
        outputRangeFilter.setLevelMax(Level.FATAL);
        outputRangeFilter.setAcceptOnMatch(true);

        outputAppender.addFilter(outputRangeFilter);
        Logger.getRootLogger().addAppender(outputAppender);
        Logger.getRootLogger().setLevel(Level.TRACE);
        mainWindow.setSize(new Dimension(700, 500));
        SwingUtl.centerFrameInScreen(mainWindow);
        mainWindow.setVisible(true);
    }
    
    private static FieldTypeLibrary loadFieldTypeLibrary()
    {
        InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
        assert (inputStream != null);
        return new ZiosFieldTypeLibrary(inputStream);
    }
    
}
