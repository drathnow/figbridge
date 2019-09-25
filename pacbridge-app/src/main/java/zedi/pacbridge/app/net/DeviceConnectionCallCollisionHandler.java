package zedi.pacbridge.app.net;

import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;

public class DeviceConnectionCallCollisionHandler implements CallCollisionHandler {

	public static final SiteAddress COLLIDED_ADDRESS = new NuidSiteAddress("Collided-Address");
	
	@Override
	public void handleCallCollision(Connection newConnection, Connection existingConnection) {
		((DeviceConnection)existingConnection).setSiteAddress(COLLIDED_ADDRESS);
		existingConnection.close();
	}
}
