package zedi.pacbridge.zap.messages;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ZapReport;

public class ZapMessageDecoder
{
	public static final String DATE_FMT = "yyyy-MMM-dd HH:mm:ss z";

	protected static String REPORT_ITEM_FORMAT_HEADER = "    IO ID      Type                 Value              Alarm Status ";
	protected static String REPORT_ITEM_FORMAT_ULINE = "    ---------- ------------------   ------------------ ------------ ";
	private static String REPORT_ITEM_FORMAT_STRING = "    {0}        {1} {2} {3}";

	protected static String WRITE_VALUE_FORMAT_HEADER = "    IO IOD    Data Type              Value";
	protected static String WRITE_VALUE_FORMAT_ULINE = "    -------   ------------------     ----------------";
	private static String WRITE_VALUE_FORMAT_STRING = "    {0}       {1} {2}";

	private SimpleDateFormat localDateFormatter;
	private SimpleDateFormat utcDateFormatter;
	private DecimalFormat integerFormat;
	private MessageFormat reportItemMessageFormat;
	private MessageFormat writeValueMessageFormat;
	private FieldTypeLibrary fieldTypeLibrary;

	private Object object;

	public ZapMessageDecoder()
	{
		localDateFormatter = new SimpleDateFormat(DATE_FMT);
		localDateFormatter.setTimeZone(TimeZone.getDefault());
		utcDateFormatter = new SimpleDateFormat(DATE_FMT);
		utcDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		integerFormat = new DecimalFormat("####");
		reportItemMessageFormat = new MessageFormat(REPORT_ITEM_FORMAT_STRING);
		writeValueMessageFormat = new MessageFormat(WRITE_VALUE_FORMAT_STRING);
	}

	public ZapMessageDecoder(FieldTypeLibrary fieldTypeLibrary)
	{
		this();
		this.fieldTypeLibrary = fieldTypeLibrary;
	}

	public String decodePacketBytes(ByteBuffer byteBuffer)
	{
		((Buffer) byteBuffer).mark();
		
		//
		// First check to see if the first two bytes contain the length of the entire packet.  If it does
		// then we can assume it's the length.  If it doesn't, we'll assume this is a packet without the
		// length field.
		//
		int len = Unsigned.getUnsignedShort(byteBuffer);
		if (len != byteBuffer.limit() - 2)
			((Buffer) byteBuffer).reset();
		StringBuilder stringBuilder = new StringBuilder();
		ZapPacket packet = ZapPacket.packetFromByteBuffer(byteBuffer, fieldTypeLibrary);
		ZapSessionHeader header = (ZapSessionHeader) packet.getHeader();
		stringBuilder.append("ZAP Packet").append('\n')
		                .append("    Type           : " + header.headerType().getName()).append('\n')
		                .append("    Message Type   : " + header.messageType().getName()).append('\n')
		                .append("    Session ID     : " + header.getSessionId()).append('\n')
		                .append("    Sequence Number: " + header.getSequenceNumber()).append('\n')
		                .append('\n')
		                .append(formattedMessage(packet.getMessage()));
		return stringBuilder.toString();

	}

	/**
	 * Decodes a Zap Packet (not including the count field)
	 * 
	 * @param bytes
	 * 
	 * @return Decoded message string
	 */
	public String decodePacketBytes(byte[] bytes)
	{
		return decodePacketBytes(ByteBuffer.wrap(bytes));
	}

	public String decodePacketBytes(byte[] bytes, int offset, int length)
	{
		return decodePacketBytes(ByteBuffer.wrap(bytes, offset, length));
	}

	public String decodeMessageBytes(ZapMessageType messageType, byte[] bytes)
	{
		return decodeMessageBytes(messageType, ByteBuffer.wrap(bytes));
	}

	public String decodeMessageBytes(ZapMessageType messageType, ByteBuffer byteBuffer)
	{
		Message message = null;
		switch (messageType.getNumber())
		{
			case ZapMessageType.BUNDLED_REPORT_NUMBER:
				message = BundledReportMessage.bundledReportMessageFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.HEART_BEAT_MESSAGE_NUMBER:
				message = HeartBeatMessage.heartBeatMessageFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.HEART_BEAT_RESPONSE_MESSAGE_NUMBER:
				message = HeartBeatResponseMessage.heartBeatMessageFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.ACK_MESSAGE_NUMBER:
				message = AckMessage.ackMessageForByteBuffer(byteBuffer);
				break;

			case ZapMessageType.WRITE_IO_POINT_NUMBER:
				message = WriteIoPointsControl.messageFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.DEMAND_POLL_NUMBER:
				message = DemandPollControl.messageFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.OTAD_REQUEST_NUMBER:
				message = OtadRequestControl.fromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.OTAD_STATUS_UPDATE_NUMBER:
				message = OtadStatusMessage.messageFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.SERVER_CHALLENGE_MESSAGE_NUMBER:
				message = ServerChallenge.serverChallengeFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.CHALLENGE_RESPONSE_MESSAGE_V2_NUMBER:
				message = ChallengeResponseMessageV2.clientChallengeResponseFromByteBuffer(byteBuffer);
				break;

			case ZapMessageType.SCRUB_NUMBER:
				message = ScrubControl.scrubControlFromByteBuffer(byteBuffer);
				break;
		}

		if (message != null)
			return formattedMessage(message);
		return "Can't decode message bytes: Message type not currently supported: '" + messageType + "'";
	}

	public String formattedMessage(Message message)
	{
		switch (message.messageType().getNumber())
		{
			case ZapMessageType.BUNDLED_REPORT_NUMBER:
				return decodeMessage((BundledReportMessage) message);
			case ZapMessageType.HEART_BEAT_MESSAGE_NUMBER:
				return decodeMessage((HeartBeatMessage) message);
			case ZapMessageType.HEART_BEAT_RESPONSE_MESSAGE_NUMBER:
				return decodeMessage((HeartBeatResponseMessage) message);
			case ZapMessageType.ACK_MESSAGE_NUMBER:
				return decodeMessage((AckMessage) message);
			case ZapMessageType.WRITE_IO_POINT_NUMBER:
				return decodeMessage((WriteIoPointsControl) message);
			case ZapMessageType.DEMAND_POLL_NUMBER:
				return decodeMessage((DemandPollControl) message);
			case ZapMessageType.OTAD_REQUEST_NUMBER:
				return decodeMessage((OtadRequestControl) message);
			case ZapMessageType.OTAD_STATUS_UPDATE_NUMBER:
				return decodeMessage((OtadStatusMessage) message);
			case ZapMessageType.SERVER_CHALLENGE_MESSAGE_NUMBER:
				return decodeMessage((ServerChallenge) message);
			case ZapMessageType.CHALLENGE_RESPONSE_MESSAGE_NUMBER:
			case ZapMessageType.CHALLENGE_RESPONSE_MESSAGE_V2_NUMBER:
				return decodeMessage((ChallengeResponseMessage) message);
			case ZapMessageType.AUTHENTICATION_RESPONSE_MESSAGE_NUMBER:
				return decodeMessage((AuthenticationResponseMessage) message);
			case ZapMessageType.SCRUB_NUMBER:
				return decodeMessage((ScrubControl) message);
			case ZapMessageType.CONFIGURE_NUMBER:
				return decodeMessage((ConfigureControl) message);
			case ZapMessageType.CONFIGURE_UPDATE_MESSAGE_NUMBER :
				return decodeMessage((ConfigureUpdateMessage) message);
		}

		return "Message type not currently supported: '" + message.messageType() + "'";
	}

	private String decodeMessage(ConfigureUpdateMessage message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Object Type  : ").append(message.getObjectType().getName()).append('\n');
		List<Action> actions = message.getActions();

		for (Action action : actions)
		{
			stringBuilder.append("    Action Type  : ").append(action.getActionType().getName()).append('\n');
			List<Field<?>> fields = action.getFields();
			for (Field<?> field : fields) 
			{
				stringBuilder.append("        Field: ")
					.append(field.getFieldType().getName())
					.append("=")
					.append(field.getValue().toString())
					.append('\n');
			}
		}

		return stringBuilder.toString();
	}

	private String decodeMessage(ConfigureControl message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Object Type  : ").append(message.getObjectType().getName()).append('\n');
		List<Action> actions = message.getActions();

		for (Action action : actions)
		{
			stringBuilder.append("    Action Type  : ").append(action.getActionType().getName()).append('\n');
			List<Field<?>> fields = action.getFields();
			for (Field<?> field : fields) 
			{
				stringBuilder.append("        Field: ")
					.append(field.getFieldType().getName())
					.append("=")
					.append(field.getValue().toString())
					.append('\n');
			}
		}

		return stringBuilder.toString();
	}

	private String decodeMessage(AuthenticationResponseMessage message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		Date deviceTime = new Date(message.getDeviceTime() * 1000);
		Date serverTime = new Date(message.getServerTime() * 1000);
		stringBuilder.append("Message Type    : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Connection flags: ").append(message.getConnectionFlags().toString()).append('\n');
		stringBuilder.append("Server Name     : ").append(message.getServerName()).append('\n');
		stringBuilder.append("Server Hash     : ");
		if (message.getServerHash() == null)
			stringBuilder.append("N/A").append('\n');
		else
			stringBuilder.append(HexStringEncoder.bytesAsHexString(message.getServerHash())).append('\n');

		stringBuilder.append("Session Key     : ");
		if (message.getSessionKey() == null)
			stringBuilder.append("N/A").append('\n');
		else
			stringBuilder.append(HexStringEncoder.bytesAsHexString(message.getSessionKey())).append('\n');

		stringBuilder.append("Device Time     : ").append(utcDateFormatter.format(deviceTime)).append('\n');
		stringBuilder.append("Server Time     : ").append(utcDateFormatter.format(serverTime)).append('\n');
		return stringBuilder.toString();
	}

	private String decodeMessage(ChallengeResponseMessage message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		Date deviceTime = new Date(message.getDeviceTime() * 1000);

		stringBuilder.append("Message Type    : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Firmware Version: ").append(message.getFirmwareVersion() == null ? "N/A" : message.getFirmwareVersion()).append('\n');
		stringBuilder.append("Encryption Type : ").append(message.getEncryptionType()).append('\n');
		stringBuilder.append("Compression Type: ").append(message.getCompressionType()).append('\n');
		stringBuilder.append("Device Time     : ").append(utcDateFormatter.format(deviceTime)).append('\n');
		stringBuilder.append("Client Salt     : ").append(HexStringEncoder.bytesAsHexString(message.getClientSalt())).append('\n');
		stringBuilder.append("Client Hash     : ").append(HexStringEncoder.bytesAsHexString(message.getClientHash())).append('\n');
		stringBuilder.append("Username        : ").append(message.getUsername()).append('\n');
		return stringBuilder.toString();
	}

	private String decodeMessage(ScrubControl message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type   : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Scrub IO Points: ").append(message.isScrubIoPoints()).append('\n');
		stringBuilder.append("Scrub Report   : ").append(message.isScrubReports()).append('\n');
		stringBuilder.append("Scrub Events   : ").append(message.isScrubEvents()).append('\n');
		stringBuilder.append("Scrub All      : ").append(message.isScrubAll()).append('\n');
		return stringBuilder.toString();
	}

	private String decodeMessage(ServerChallenge message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type  : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Salt          : ").append(HexStringEncoder.bytesAsHexString(message.getServerSaltValue())).append('\n');
		return stringBuilder.toString();
	}

	private String decodeMessage(OtadStatusMessage message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type  : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Version       : ").append(message.getVersion()).append('\n');
		stringBuilder.append("EventId       : ").append(message.getEventId()).append('\n');
		stringBuilder.append("OTAD Status   : ").append(message.getOtadStatusType().getName()).append('\n');
		stringBuilder.append("Optional Data : ").append(message.getOptionalData()).append("\n\n");
		return stringBuilder.toString();
	}

	private String decodeMessage(OtadRequestControl message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type  : ").append(message.messageType().getName()).append('\n');
		stringBuilder.append("Flags         : ").append(message.getFlags().toString()).append('\n');
		stringBuilder.append("OTAD File     : ").append(message.getOtadFileUrl()).append('\n');
		stringBuilder.append("MD5 Hash      : ").append(message.getMd5Hash()).append('\n');
		stringBuilder.append("Timeout (Secs): ").append(message.getTimeoutSeconds()).append('\n');
		stringBuilder.append("Retries       : ").append(message.getRetries()).append("\n\n");
		return stringBuilder.toString();
	}

	private String decodeMessage(WriteIoPointsControl message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		List<WriteValue> writeValues = message.getWriteValues();
		stringBuilder.append("Message Type: Write IO Points").append('\n');
		stringBuilder.append("Value Count : ").append(writeValues.size()).append("\n\n");
		stringBuilder.append(WRITE_VALUE_FORMAT_HEADER).append('\n');
		stringBuilder.append(WRITE_VALUE_FORMAT_ULINE).append('\n');
		for (Iterator<WriteValue> iter = writeValues.iterator(); iter.hasNext();)
		{
			WriteValue writeValue = iter.next();
			String decimalFormatString = writeValue.getValue().toString();
			Integer index = new Integer(writeValue.getIoId().intValue());
			String ljValueString = leftJustifiedPaddedString(writeValue.getValue().dataType().getName(), 20);
			String rjPaddedString = rightJustifiedPaddedString(decimalFormatString, 17);
			String lgIntegerString = leftJustifiedPaddedString(integerFormat.format(index), 4);
			Object[] args = new Object[] { lgIntegerString, ljValueString, rjPaddedString };
			stringBuilder.append(writeValueMessageFormat.format(args)).append('\n');
		}
		stringBuilder.append('\n');
		return stringBuilder.toString();
	}

	private String decodeMessage(DemandPollControl message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("EventId       : ").append(message.getEventId()).append('\n');
		stringBuilder.append("Index         : ").append(message.getIndex()).append('\n');
		stringBuilder.append("Pollset Number: ").append(message.getPollSetNumber()).append("\n\n");
		return stringBuilder.toString();
	}

	private String decodeMessage(AckMessage message)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type      : ACK").append('\n')
		                .append("Acked Message     : ").append(message.getAckedMessageType()).append('\n')
		                .append("Additional Details: ").append(message.additionalDetails().toString()).append("\n\n");

		return stringBuilder.toString();
	}

	public String decodeMessage(HeartBeatMessage message)
	{
		Date deviceTime = new Date(message.getDeviceTime() * 1000);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type: HeartBeat").append('\n')
		                .append("Device Time: ").append(utcDateFormatter.format(deviceTime));
		return stringBuilder.toString();
	}

	public String decodeMessage(HeartBeatResponseMessage message)
	{
		Date deviceTime = new Date(message.getDeviceTime() * 1000);
		Date serverTime = new Date(message.getServerTime() * 1000);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type: HeartBeat").append('\n')
		                .append("Device Time: ").append(utcDateFormatter.format(deviceTime)).append('\n')
		                .append("Server Time: ").append(utcDateFormatter.format(serverTime)).append("\n\n");
		return stringBuilder.toString();
	}

	public String decodeMessage(BundledReportMessage message)
	{
		Map<Integer, ZapReport> reportMap = message.reportsMap();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Message Type: Bundled Report").append('\n');
		stringBuilder.append("Report ID(s): ");
		for (Iterator<Integer> iter = message.reportIds().iterator(); iter.hasNext();)
		{
			Integer reportId = iter.next();
			stringBuilder.append(reportId);
			if (reportMap.get(reportId) == null)
				stringBuilder.append("(Missing)");
			if (iter.hasNext())
				stringBuilder.append(", ");
		}
		stringBuilder.append('\n').append('\n');

		for (Iterator<Integer> iter = reportMap.keySet().iterator(); iter.hasNext();)
		{
			Integer reportId = iter.next();
			ZapReport report = reportMap.get(reportId);
			stringBuilder.append("Report ID   : ").append(reportId).append('\n');
			stringBuilder.append(decodeMessage(report));
		}
		stringBuilder.append('\n');
		return stringBuilder.toString();
	}

	private String formatTemplate(List<IoPointTemplate> templates, ReadingCollection readingCollection)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
		                .append(timestampStringForReportMessage(readingCollection.timestamp()))
		                .append('\n')
		                .append('\n');
		stringBuilder.append(REPORT_ITEM_FORMAT_HEADER).append('\n');
		stringBuilder.append(REPORT_ITEM_FORMAT_ULINE).append('\n');
		Iterator<IoPointReading> readingIterator = readingCollection.ioPointReadings().iterator();
		for (Iterator<IoPointTemplate> iter = templates.iterator(); iter.hasNext();)
		{
			IoPointTemplate template = iter.next();
			IoPointReading reading = readingIterator.next();
			stringBuilder
			                .append(stringForTemplateAndReading(template, reading))
			                .append('\n');
		}
		return stringBuilder.toString();
	}

	private String stringForTemplateAndReading(IoPointTemplate template, IoPointReading reading)
	{
		StringBuilder stringBuffer = new StringBuilder();
		String decimalFormatString = reading.value().toString();
		Integer index = new Integer(template.index().intValue());
		String ljGdnValueString = leftJustifiedPaddedString(template.dataType().getName(), 20);
		String rjPaddedString = rightJustifiedPaddedString(decimalFormatString, 17);
		ZapAlarmStatus alarmStatus = reading.alarmStatus();
		String lgIntegerString = leftJustifiedPaddedString(integerFormat.format(index), 4);
		Object[] args = new Object[] { lgIntegerString, ljGdnValueString, rjPaddedString, alarmStatus };
		stringBuffer.append(reportItemMessageFormat.format(args));
		return stringBuffer.toString();
	}

	private String timestampStringForReportMessage(Date timestamp)
	{
		return "    Timestamp: " + localDateFormatter.format(timestamp);
	}

	private String reasonCodeStringForReportMessage(ZapReport reportMessage)
	{
		try
		{
			return "Reason code : " + reportMessage.reasonCode();
		} catch (IllegalArgumentException e)
		{
		}
		return "Unknown reason code: " + reportMessage.reasonCode();
	}

	private String pollsetIdStringForReportMessage(ZapReport reportMessage)
	{
		return "Pollset ID  : " + reportMessage.pollsetNumber();
	}

	private String uniqueIdStringFromReport(ZapReport report)
	{
		return "Unique ID   : " + report.reportId();
	}

	private String rightJustifiedPaddedString(String aString, int aWidth)
	{
		StringBuilder stringBuffer = new StringBuilder();
		for (int i = aString.length(); i < aWidth; i++)
			stringBuffer.append(' ');
		stringBuffer.append(aString);
		return stringBuffer.toString();
	}

	private String leftJustifiedPaddedString(String aString, int aWidth)
	{
		StringBuilder stringBuffer = new StringBuilder(aString);
		for (int i = stringBuffer.length(); i < aWidth; i++)
			stringBuffer.append(' ');
		return stringBuffer.toString();
	}

	public String decodeMessage(ZapReport report)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(reasonCodeStringForReportMessage(report)).append('\n');
		stringBuilder.append(pollsetIdStringForReportMessage(report)).append('\n');
		stringBuilder.append(uniqueIdStringFromReport(report)).append('\n');
		stringBuilder.append('\n');

		List<IoPointTemplate> templates = report.ioPointTemplate();
		for (Iterator<ReadingCollection> iterator = report.readingCollections().iterator(); iterator.hasNext();)
			stringBuilder.append(formatTemplate(templates, iterator.next())).append('\n');
		stringBuilder.append('\n');
		return stringBuilder.toString();
	}

}
