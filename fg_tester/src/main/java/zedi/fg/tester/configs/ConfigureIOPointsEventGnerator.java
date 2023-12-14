package zedi.fg.tester.configs;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import zedi.pacbridge.app.events.zios.ZiosFieldTypeLibrary;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.values.ZapDataType;

public class ConfigureIOPointsEventGnerator
{
	private static String TAG_NAME = "XA_KA";
	private static String ACTION_STRING = 
			"    <Action type=\"add\">"
			+ "      <IOPointClass>2</IOPointClass>"
			+ "      <DataType>4</DataType>"
			+ "      <PollSetId>8</PollSetId>"
			+ "      <ExternalDeviceId>138</ExternalDeviceId>"
			+ "      <Tag>XA_KA</Tag>"
			+ "      <SiteId>2</SiteId>"
			+ "      <SourceAddress>MBS;40251/4</SourceAddress>"
			+ "      <SensorClassName>RTU</SensorClassName>"
			+ "      <IsReadOnly>0</IsReadOnly>"
			+ "      <AlarmMask>20</AlarmMask>"
			+ "      <AlarmSetHysteresis>0</AlarmSetHysteresis>"
			+ "      <AlarmClearHysteresis>0</AlarmClearHysteresis>"
			+ "      <HighSet>1</HighSet>"
			+ "      <HighHysteresis>0</HighHysteresis>"
			+ "      <CorrelationId>559311</CorrelationId>"
			+ "    </Action>";
	
	private static String UDATE_STRING = 
	                "<Action type=\"update\">\n"
	                + "            <Id>100453</Id>\n"
	                + "            <IOPointClass>2</IOPointClass>\n"
	                + "            <DataType>3</DataType>\n"
	                + "            <PollSetId>1</PollSetId>\n"
	                + "            <ExternalDeviceId>1</ExternalDeviceId>\n"
	                + "            <Tag>TOTAL FLOW</Tag>\n"
	                + "            <SiteId>2</SiteId>\n"
	                + "            <SourceAddress>MBS;41233</SourceAddress>\n"
	                + "            <SensorClassName>RTU</SensorClassName>\n"
	                + "            <IsReadOnly>0</IsReadOnly>\n"
	                + "            <AlarmMask>0</AlarmMask>\n"
	                + "            <AlarmSetHysteresis>0</AlarmSetHysteresis>\n"
	                + "            <AlarmClearHysteresis>0</AlarmClearHysteresis>\n"
	                + "            <CorrelationId>577323</CorrelationId>\n"
	                + "        </Action>";
	
	private long CorrelationId = 1;
    private FieldTypeLibrary fieldTypeLibrary;
	
	public ConfigureIOPointsEventGnerator(FieldTypeLibrary fieldTypeLibrary)
	{
	    this.fieldTypeLibrary = fieldTypeLibrary;
	}
	
	public List<Action> addIOPointActionsForSiteId(long siteId, int count) throws JDOMException, IOException, ParseException
	{
		List<Action> actions = new ArrayList<>();
		
		for (int i = 0; i < count; i++)
		{
			Element addElement = JDomUtilities.elementForXmlString(ACTION_STRING);
			addElement.getChild("Tag").setText(TAG_NAME + String.format("%04d", i));
			addElement.getChild("SiteId").setText(""+siteId);
            addElement.getChild("CorrelationId").setText(""+CorrelationId++);
			actions.add(Action.actionFromElement(addElement, fieldTypeLibrary));
		}
		
		return actions;
	}
	
    public Action updateOPointActionForIOPoint(long siteId, long ioId, String tag, ZapDataType dataType) throws JDOMException, IOException, ParseException
    {
        Element updateElement = JDomUtilities.elementForXmlString(UDATE_STRING);
        updateElement.getChild("Tag").setText(tag);
        updateElement.getChild("Id").setText(""+ioId);
        updateElement.getChild("SiteId").setText(""+siteId);
        updateElement.getChild("DataType").setText(dataType.getNumber().toString());
        updateElement.getChild("CorrelationId").setText(""+CorrelationId++);
        return Action.actionFromElement(updateElement, fieldTypeLibrary);
    }

}
