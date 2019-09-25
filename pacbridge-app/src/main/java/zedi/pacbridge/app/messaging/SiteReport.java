package zedi.pacbridge.app.messaging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.net.ReasonCode;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.ZapReasonCode;


public class SiteReport implements Serializable {

    public static final String NAMESPACE = null;
    public static final String ROOT_ELEMENT_NAME = "SiteReport";
    public static final String TAG_TIMESTAMP = "Timestamp";
    public static final String TAG_POLLSET_NUMBER = "PollSetNumber";
    public static final String TAG_NUID = "Nuid";
    public static final String TAG_MESSAGE_ID = "messageId";
    public static final String TAG_REASON = "reason";
    public static final String QUALIFIER_TAG = "qualifier";
    public static final String EVENT_ID_TAG = "EventId";
    public static final String IP_ADDRESS_TAG = "IpAddress";

    protected final ArrayList<SiteReportItem> reportItems;  
    protected EventQualifier eventQualifier;
    protected ReasonCode reasonCode;
    protected String messageId;
    protected String nuid;
    protected Date timestamp;
    protected Integer pollSetNumber;
    protected boolean bridged;
    protected Long eventId;
    protected String ipAddress;
    
    // This is a convenience attribute that is not part of an actual site report
    // It is used for utilities that require the time that a site report was logged or
    // created.
    private Date creationTime = new Date(System.currentTimeMillis());

    private SiteReport() {
        this.reportItems = new ArrayList<SiteReportItem>();
    }
    
    public SiteReport(EventQualifier eventQualifier, String nuid, Integer pollSetNumber, Date timestamp, ReasonCode reasonCode, Long eventId, String ipAddress) {
        this();
        this.eventQualifier = eventQualifier;
        this.nuid = nuid;
        this.pollSetNumber = pollSetNumber;
        this.timestamp = timestamp;
        this.reasonCode = reasonCode;
        this.eventId = eventId;
        this.ipAddress = ipAddress;
    }

    public SiteReport(EventQualifier eventQualifier, String nuid, Integer pollSetNumber, Date timestamp, ReasonCode reasonCode, Long eventId) {
        this(eventQualifier, nuid, pollSetNumber, timestamp, reasonCode, eventId, null);
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public ReasonCode getReasonCode() {
        return reasonCode;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public String getMessageId() {
        return messageId;
    }

    public String getNuid() {
        return nuid;
    }

    public List<SiteReportItem> getReportItems() {
        return new ArrayList<SiteReportItem>(reportItems);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getPollSetNumber() {
        return pollSetNumber;
    }

    public void addReportItem(SiteReportItem reportItem) {
        reportItems.add(reportItem);
    }
    
    public Date getCreationTime() {
        return creationTime;
    }
    
    public String asXmlString() {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(arrayOutputStream);
        try {
            serialize(dataOutputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize site report",e);
        }
        return new String(arrayOutputStream.toByteArray());
    }
    
    public void deserialize(InputStream inputStream) throws IOException {
    }

    public void serialize(OutputStream output) throws IOException {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            XmlSerializer serializer = factory.newSerializer();
            serializer.setOutput(new PrintWriter(output));
            if (NAMESPACE != null)
                serializer.setPrefix("", NAMESPACE);
            serializer.startTag(NAMESPACE, ROOT_ELEMENT_NAME);
            serializer.attribute(NAMESPACE, TAG_REASON, reasonCode.getName());
            serializer.attribute(NAMESPACE, QUALIFIER_TAG, eventQualifier.getName());
            
            if (messageId != null)
                serializer.attribute(NAMESPACE, TAG_MESSAGE_ID, messageId);

            if (eventId != null) {
                serializer.startTag(NAMESPACE, EVENT_ID_TAG);
                serializer.text(eventId.toString());
                serializer.endTag(NAMESPACE, EVENT_ID_TAG);
            }

            serializer.startTag(NAMESPACE, TAG_NUID);
            serializer.text(nuid);
            serializer.endTag(NAMESPACE, TAG_NUID);
            
            if (ipAddress != null) {
                serializer.startTag(NAMESPACE, IP_ADDRESS_TAG);
                serializer.text(ipAddress);
                serializer.endTag(NAMESPACE, IP_ADDRESS_TAG);
            }
            
            serializer.startTag(NAMESPACE, TAG_POLLSET_NUMBER);
            serializer.text(Integer.toString(pollSetNumber));
            serializer.endTag(NAMESPACE, TAG_POLLSET_NUMBER);
            serializer.startTag(NAMESPACE, TAG_TIMESTAMP);
            serializer.text(Long.toString(timestamp.getTime()));
            serializer.endTag(NAMESPACE, TAG_TIMESTAMP);

            for (Iterator<SiteReportItem> i = reportItems.iterator(); i.hasNext();)
                ((SiteReportItem)i.next()).serialize(serializer);

            serializer.endTag(NAMESPACE, ROOT_ELEMENT_NAME);
            serializer.endDocument();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
        
    private void takeValuesFromElement(Element element) {
        Attribute attribute;
        if ((attribute = element.getAttribute(TAG_REASON)) != null)
            reasonCode = ZapReasonCode.reasonCodeForName(attribute.getValue());
        if ((attribute = element.getAttribute(TAG_MESSAGE_ID)) != null)
            messageId = attribute.getValue();
        timestamp = new Date(Long.decode(element.getChild(TAG_TIMESTAMP).getText()));
        if (element.getChild(TAG_NUID) != null)
            nuid = element.getChild(TAG_NUID).getText();
        pollSetNumber = Integer.parseInt(element.getChild(TAG_POLLSET_NUMBER).getText());
        for (Iterator<Element> i = element.getChildren(SiteReportItem.TAG_REPORT_ITEM).iterator(); i.hasNext(); ) {
            SiteReportItem reportItem = SiteReportItem.reportItemForElement(i.next());
            addReportItem(reportItem);
        }
    }
    
    public static SiteReport siteReportFromXmlString(String xmlString) throws JDOMException, IOException {
        Element rootElement = JDomUtilities.elementForXmlString(xmlString);
        if (rootElement.getName().equals(ROOT_ELEMENT_NAME) == false)
            throw new IllegalArgumentException("Invalid XML string.  XML document must start with <siteReport> element");
        SiteReport siteReport = new SiteReport();
        siteReport.takeValuesFromElement(rootElement);
        return siteReport;
    }
}
