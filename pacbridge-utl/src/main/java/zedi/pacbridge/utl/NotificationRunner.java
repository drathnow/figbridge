package zedi.pacbridge.utl;


class NotificationRunner implements Runnable {
    private String notificationName; 
    private Object attachment;
    private NotificationCenter notificationCenter;
    
    public NotificationRunner(NotificationCenter notificationCenter, String notificationName, Object attachment) {
    	this.notificationCenter = notificationCenter;
        this.notificationName = notificationName;
        this.attachment = attachment;
    }

    @Override
    public void run() {
    	notificationCenter.postNotification(notificationName, attachment);
    }

	public String getNotificationName() {
		return notificationName;
	}

	public Object getAttachment() {
		return attachment;
	}

	public NotificationCenter getNotificationCenter() {
		return notificationCenter;
	}
    
    
}