package zedi.fg.tester.net;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.utl.SiteAddress;

public class MockAuthenticationDelegate implements AuthenticationDelegate
{

	@Override
	public Device deviceForNuid(String identifier)
	{
		return null;
	}

	@Override
	public String systemId()
	{
		return null;
	}

	@Override
	public boolean hasOutgoingDataRequests(SiteAddress siteAddress)
	{
		return false;
	}

	@Override
	public String serverName()
	{
		return null;
	}
}
