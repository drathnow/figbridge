package zedi.pacbridge.web.dtos;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.net.InetSocketAddress;
import java.util.Date;

import org.junit.Test;

import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public class ConnectionDTOInfoCollectorTest extends BaseTestCase
{
	private static final Integer NETWORK_NUMBER = 17;
	private static final String IP_ADDRESS = "1.2.3.4";
	private static final InetSocketAddress SOCKET_ADDRESS = new InetSocketAddress(IP_ADDRESS, 0);
	private static final SiteAddress SITE_ADDRESS = new IpSiteAddress(IP_ADDRESS, NETWORK_NUMBER);
	private static final Integer BYTES_RCV = 100;
	private static final Integer BYTES_TRX = 200;

	@Test
	public void shouldCreateDTOWithCorrectValues() throws Exception {
		Long now = System.currentTimeMillis();
		String expectedDate = ConnectionDTOInfoCollector.dateFormat.format(new Date(now));
		Connection connection = mock(Connection.class);

		given(connection.getLastActivityTime()).willReturn(now);
		given(connection.getRemoteAddress()).willReturn(SOCKET_ADDRESS);
		given(connection.getSiteAddress()).willReturn(SITE_ADDRESS);
		given(connection.getBytesReceived()).willReturn(BYTES_RCV);
		given(connection.getBytesTransmitted()).willReturn(BYTES_TRX);

		ConnectionDTOInfoCollector collector = new ConnectionDTOInfoCollector();
		ConnectionDTO collectInfo = collector.collectInfo(connection);

		assertEquals(BYTES_RCV.intValue(), collectInfo.getBytesRcv().intValue());
		assertEquals(BYTES_TRX.intValue(), collectInfo.getBytesTrx().intValue());
		assertEquals(IP_ADDRESS, collectInfo.getIpAddress());
		assertEquals(NETWORK_NUMBER, collectInfo.getNetNo());
		assertEquals(expectedDate, collectInfo.getLastActivity());
	}
}
