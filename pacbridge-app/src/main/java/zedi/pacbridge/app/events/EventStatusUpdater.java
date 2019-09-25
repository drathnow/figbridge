package zedi.pacbridge.app.events;

public interface EventStatusUpdater {
    public void updateEvent(Long eventUpdate, String status, String message);
}
