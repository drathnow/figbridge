package zedi.pacbridge.eventgen.util;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import zedi.pacbridge.app.events.zios.ConfigureEvent;
import zedi.pacbridge.app.events.zios.DemandPollEvent;
import zedi.pacbridge.app.events.zios.ScrubEvent;
import zedi.pacbridge.app.events.zios.WriteIOPointsEvent;
import zedi.pacbridge.eventgen.EventPublisher;
import zedi.pacbridge.eventgen.InjectModel;
import zedi.pacbridge.eventgen.Main;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.values.ZapDataType;

public class StaticEventGenerator {
    private static final Logger logger = LoggerFactory.getLogger(StaticEventGenerator.class.getName());
    
//
//    public static final String WRITE_IO_POINTS_EVENT_XML = 
//            "<Event name='WriteIOPoints' qualifier='ZIOS'>"
//          + "    <EventId>" + nextEventId() + "</EventId>"
//          + "    <Nuid>" +  Main.getConfiguration().getUsername() + "</Nuid>"
//          + "    <WriteIOPoints>"
//          + "        <WriteValue>"
//          + "            <Index>100</Index>"
//          + "            <DataType>" + ZapDataType.Float.getName() + "</DataType>"
//          + "            <Value>1.2</Value>"
//          + "        </WriteValue>"
//          + "        <WriteValue>"
//          + "            <Index>200</Index>"
//          + "            <DataType>" + ZapDataType.Float.getName() + "</DataType>"
//          + "            <Value>2.3</Value>"
//          + "        </WriteValue>"
//          + "    </WriteIOPoints>"
//          + "</Event>";
          
    private FieldTypeLibrary fieldTypeLibrary;
    private EventPublisher eventPublisher;
    private StaticDeviceCache deviceCache;
    private ConfigureIoPointsEventGenerator ioPointsEventGenerator;
    private ConfigurePortsEventGenerator portsEventGenerator;
    private ConfigureSitesEventGenerator sitesEventGenerator;
    private ConfigureDevicesEventGenerator devicesEventGenerator;
    private ConfigureEventsEventGenerator eventsEventGenerator;

    @Inject
    @SuppressWarnings("cdi-ambiguous-dependency")
    public StaticEventGenerator(FieldTypeLibrary fieldTypeLibrary, 
                                EventPublisher eventPublisher, 
                                ConfigureIoPointsEventGenerator ioPointEventGenerator, 
                                ConfigurePortsEventGenerator portsEventGenerator, 
                                ConfigureSitesEventGenerator sitesEventGenerator,
                                ConfigureDevicesEventGenerator devicesEventGenerator,
                                ConfigureEventsEventGenerator eventsEventGenerator) {
        this.fieldTypeLibrary = fieldTypeLibrary;
        this.eventPublisher = eventPublisher;
        this.ioPointsEventGenerator = ioPointEventGenerator;
        this.portsEventGenerator = portsEventGenerator;
        this.sitesEventGenerator = sitesEventGenerator;
        this.devicesEventGenerator = devicesEventGenerator;
        this.eventsEventGenerator = eventsEventGenerator;
        this.deviceCache = new StaticDeviceCache();
    }

    public void publishConfigureSitesEvent() throws Exception {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 0);
        ConfigureEvent event = sitesEventGenerator.eventForSiteAddress(siteAddress);
        eventPublisher.publishEvent(event);
    }
    
    public void publishWriteIOPointsEvent() throws Exception {
        Element element = JDomUtilities.elementForXmlString(writeIoPoints());
        WriteIOPointsEvent event = WriteIOPointsEvent.writeIoPointsEventForElement(element, deviceCache);
        eventPublisher.publishEvent(event);
    }
    
    public void publishDemandPollIndex() throws Exception {
        Element element = JDomUtilities.elementForXmlString(demandPollEventForIndex());
        DemandPollEvent event = DemandPollEvent.demandPollEventEventForElement(element, deviceCache);
        eventPublisher.publishEvent(event);
    }
    
    public void publishDemandPollPollSet() throws Exception {
        Element element = JDomUtilities.elementForXmlString(demandPollEventForPollset());
        DemandPollEvent event = DemandPollEvent.demandPollEventEventForElement(element, deviceCache);
        eventPublisher.publishEvent(event);
    }
    
    public void publishDemandPoll(Long index, Integer pollsetId) {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 0);
        DemandPollEvent event = new DemandPollEvent(nextEventId(), siteAddress, null, index, pollsetId);
        eventPublisher.publishEvent(event);
    }

    public void publishConfigureDeviceEventsEvent() throws Exception {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 0);
        ConfigureEvent event = devicesEventGenerator.eventForSiteAddress(siteAddress);
        eventPublisher.publishEvent(event);
    }

    public void publishConfigurePortsEvent() throws Exception {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 0);
        ConfigureEvent event = portsEventGenerator.eventForSiteAddress(siteAddress);
        eventPublisher.publishEvent(event);
    }
    
    public void publishConfigureIOPointsEvent() throws Exception {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 0);
        ConfigureEvent event = ioPointsEventGenerator.eventForSiteAddress(siteAddress);
        eventPublisher.publishEvent(event);
    }
    
    public void publishScrubEvent(boolean ioPoints, boolean events, boolean reports, boolean all) {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 17);
        ScrubEvent event = new ScrubEvent(1234L, siteAddress, null, ioPoints, reports, events, all);
        eventPublisher.publishEvent(event);
    }
    
    public void publishConfigureEventsEvent() throws Exception {
        NuidSiteAddress siteAddress = new NuidSiteAddress( Main.getConfiguration().getUsername(), 0);
        ConfigureEvent event = eventsEventGenerator.eventForSiteAddress(siteAddress);
        eventPublisher.publishEvent(event);
    }
    
    public static Long nextEventId() {
        return Math.abs(UUID.randomUUID().getLeastSignificantBits());
    }

    public static Long nextCorrelationId() {
        return Math.abs(UUID.randomUUID().getLeastSignificantBits());
    }

    
    private String demandPollEventForIndex()  { 
          return "<Event name='DemandPoll' qualifier='ZIOS'>"
          + "    <EventId>" + nextEventId() + "</EventId>"
          + "    <Nuid>" +  Main.getConfiguration().getUsername() + "</Nuid>"
          + "    <DemandPoll>"
          + "        <Index>100</Index>"
          + "        <PollsetNumber>0</PollsetNumber>"
          + "    </DemandPoll>"
          + "</Event>";
    }
    
    private String demandPollEventForPollset()  { 
          return "<Event name='DemandPoll' qualifier='ZIOS'>"
          + "    <EventId>" + nextEventId() + "</EventId>"
          + "    <Nuid>" +  Main.getConfiguration().getUsername() + "</Nuid>"
          + "    <DemandPoll>"
          + "        <Index>0</Index>"
          + "        <PollsetNumber>1</PollsetNumber>"
          + "    </DemandPoll>"
          + "</Event>";
    }
            
            
    private String writeIoPoints() { 
          return "<Event name='WriteIOPoints' qualifier='ZIOS'>"
          + "    <EventId>" + nextEventId() + "</EventId>"
          + "    <Nuid>" +  Main.getConfiguration().getUsername() + "</Nuid>"
          + "    <WriteIOPoints>"
          + "        <WriteValue>"
          + "            <Index>100</Index>"
          + "            <DataType>" + ZapDataType.Float.getName() + "</DataType>"
          + "            <Value>1.2</Value>"
          + "        </WriteValue>"
          + "        <WriteValue>"
          + "            <Index>200</Index>"
          + "            <DataType>" + ZapDataType.Float.getName() + "</DataType>"
          + "            <Value>2.3</Value>"
          + "        </WriteValue>"
          + "    </WriteIOPoints>"
          + "</Event>";
    }
          
    
    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%m%n")));
        Injector injector = Guice.createInjector(new InjectModel());
        StaticEventGenerator instance = injector.getInstance(StaticEventGenerator.class);
        try {
            instance.publishConfigureSitesEvent();
        } catch (Exception e) {
            logger.error("ACK!!! Couldn't publish", e);
        }
        System.out.println("Done!");
    }
}
