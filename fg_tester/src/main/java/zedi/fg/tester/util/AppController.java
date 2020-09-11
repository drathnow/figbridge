package zedi.fg.tester.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import zedi.fg.tester.configs.TestConfigurator;
import zedi.fg.tester.net.ConnectionListener;
import zedi.fg.tester.ui.MainFrame;
import zedi.fg.tester.util.AppController.EventType;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.DemandPollControl;
import zedi.pacbridge.zap.messages.ScrubControl;
import zedi.pacbridge.zap.messages.TimedEventType;

public class AppController implements Notifiable
{
    private static final Logger logger = Logger.getLogger(AppController.class);

    private ConnectionListener connectionListener;
    private NotificationCenter notificationCenter;
    private Configuration configuration;
    private MainFrame mainFrame;
    private static AtomicLong eventId = new AtomicLong(1);
    private TestConfigurator configurator;

    public AppController(NotificationCenter notificationCenter,
                    Configuration configuration,
                    ConnectionListener connectionListener,
                    TestConfigurator configurator)
    {
        this.connectionListener = connectionListener;
        this.notificationCenter = notificationCenter;
        this.configuration = configuration;
        this.configurator = configurator;

        this.notificationCenter.addObserver(this, Constants.FG_CONNECTED);
        this.notificationCenter.addObserver(this, Constants.FG_DISCONNECTED);
    }

    public void setMainFrame(MainFrame mainFrame)
    {
        this.mainFrame = mainFrame;
    }

    public void startConnectionListener()
    {
        try
        {
            connectionListener.start();
        } catch (Exception e)
        {
            logger.error("Unable to start listener", e);
            connectionListener = null;
        }
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void saveConfiguration()
    {
        try
        {
            configuration.save();
        } catch (Exception e)
        {
            logger.error("Unable to save configuration", e);
        }
    }

    public void stopConnectionListener()
    {
        if (connectionListener != null)
            connectionListener.close();
        connectionListener = null;
    }

    public void shutdown()
    {
        if (connectionListener != null)
            stopConnectionListener();
    }

    @Override
    public void handleNotification(Notification notification)
    {
        if (notification.getName().equals(Constants.FG_CONNECTED))
            mainFrame.enableMenus();
        if (notification.getName().equals(Constants.FG_DISCONNECTED))
            mainFrame.disableMenu();
    }

    public void sendScrub(boolean selected, boolean events, boolean reports, boolean ioPoints, boolean all)
    {
        int opt = 0;

        if (all)
            opt = ScrubControl.MSG_SCRUB_ALL;
        else
        {
            if (events)
                opt |= ScrubControl.MSG_SCRUB_EVENTS;

            if (reports)
                opt |= ScrubControl.MSG_SCRUB_REPORTS;

            if (ioPoints)
                opt |= ScrubControl.MSG_SCRUB_IO_POINTS;
        }

        ScrubControl scrubControl = new ScrubControl(eventId.getAndIncrement(), opt);
        try
        {
            connectionListener.sendMessageWithSession(scrubControl);
        } catch (IOException e)
        {
            logger.info("Unable to send control", e);
        }
    }

    public void sendDemanPoll(Long index, Integer pollset)
    {
        DemandPollControl demandPoll = new DemandPollControl(eventId.getAndIncrement(), index, pollset);
        try
        {
            connectionListener.sendMessageWithSession(demandPoll);
        } catch (IOException e)
        {
            logger.error("Unable to send demand poll", e);
        }
    }

    public void setupModbusTest()
    {
        configurator.setupModbusTestConfiguration();
    }

    public void setupAIDITest()
    {
        configurator.setupAIDITestConfiguration();
    }

    public void setupReportSystemIOsTest()
    {
        configurator.setupReportSystemIOsTestConfiguration();
    }

    public void addEvent(String name, TimedEventType eventType, Integer startTime, Integer interval, Integer duration, Integer pollsetId)
    {
        configurator.addEvent(name, eventType, startTime, interval, duration, pollsetId);
    }

    public void deleteEvent(Integer id)
    {
        configurator.deleteEvent(id);
    }

    public void addSite(String name)
    {
        configurator.addSite(name);
    }

    public void setupSNFTest()
    {
        configurator.setupSNFTestConfiguration();
    }

    public void setupA100Test()
    {
        configurator.setupA100TestConfiguration();
    }
}
