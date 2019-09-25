package zedi.pacbridge.app.net;

public interface CallCollisionHandler {
	public void handleCallCollision(Connection newConnection, Connection existingConnection);
}
