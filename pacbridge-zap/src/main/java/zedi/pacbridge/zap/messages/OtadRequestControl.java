package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class OtadRequestControl extends ZapMessage implements Control, Serializable {
	private static final long serialVersionUID = 1001L;
	
	public static Integer FIXED_SIZE = 20;
	public static final Integer VERSION1 = 1;
	public static final Integer OTAD_FLAGS_FORCE_RESTART_MASK = 0x01;
    public static final Integer OTAD_FLAGS_USE_AUTHENTICATION_MASK = 0x02;
    
    public static class OtadFlags implements Serializable {
        private boolean forceRestart;
        private boolean useAuthentication;
        
        public OtadFlags() {
            this.forceRestart = false;
            this.useAuthentication = false;
        }
        
        public void setForceRestart(boolean forceRestart) {
            this.forceRestart = forceRestart;
        }
        
        public boolean isForceRestartEnabled() {
            return forceRestart;
        }
        
        public void setUseAuthentication(boolean useAuthentication) {
            this.useAuthentication = useAuthentication;
        }
        
        public boolean isUseAuthentication() {
            return useAuthentication;
        }
        
        public void serialize(ByteBuffer byteBuffer) {
            int flags = 0;
            if (forceRestart) 
                flags |= OTAD_FLAGS_FORCE_RESTART_MASK;
            if (useAuthentication)
                flags |= OTAD_FLAGS_USE_AUTHENTICATION_MASK;
            byteBuffer.put((byte)flags);
        }

        @Override
        public String toString() {
            return "{forceRestart=" + forceRestart + ", useAuthentication=" + useAuthentication + "}"; 
        }
        
        public static OtadFlags fromByteBuffer(ByteBuffer byteBuffer) {
            int theByte = byteBuffer.get();
            boolean forceRestart = (theByte & OTAD_FLAGS_FORCE_RESTART_MASK) != 0; 
            boolean useAuth = (theByte & OTAD_FLAGS_USE_AUTHENTICATION_MASK) != 0;
            OtadFlags flags = new OtadFlags();
            flags.setForceRestart(forceRestart);
            flags.setUseAuthentication(useAuth);
            return flags;
        }
    }

    private String otadFileUrl;
	private String md5Hash;
	private Integer retries;
	private Integer retryIntervalSeconds;
	private Integer timeoutSeconds;
	private OtadFlags flags;
	private Long eventId;
	
	public OtadRequestControl(long eventId, OtadFlags flags, String otadFileUrl, String md5Hash, int retries, int retryIntervalSeconds, int timeoutSeconds) {
		super(ZapMessageType.OtadRequest);
		if (otadFileUrl == null || md5Hash == null || flags == null)
			throw new NullPointerException();
		if (retries > 65535 || timeoutSeconds > 65535)
			throw new IllegalArgumentException();
		this.eventId = eventId;
		this.flags = flags;
		this.otadFileUrl = otadFileUrl;
		this.md5Hash = md5Hash;
		this.retries = retries;
		this.retryIntervalSeconds = retryIntervalSeconds;
		this.timeoutSeconds = timeoutSeconds;
	}

	public String getOtadFileUrl() {
		return otadFileUrl;
	}

	public String getMd5Hash() {
		return md5Hash;
	}

	public Integer getRetries() {
		return retries;
	}

	public Integer getRetryIntervalSeconds() {
        return retryIntervalSeconds;
    }
	
	public Integer getTimeoutSeconds() {
		return timeoutSeconds;
	}
	
	public OtadFlags getFlags() {
        return flags;
    }

	@Override
	public void serialize(ByteBuffer byteBuffer) {
		byteBuffer.put(VERSION1.byteValue());
		byteBuffer.putLong(eventId);
		flags.serialize(byteBuffer);
		byteBuffer.putShort(timeoutSeconds.shortValue());
		byteBuffer.putShort(retries.shortValue());
        byteBuffer.putShort(retryIntervalSeconds.shortValue());
		byteBuffer.putShort((short)otadFileUrl.length());
		byteBuffer.put(otadFileUrl.getBytes());
		byteBuffer.putShort((short)md5Hash.length());
		byteBuffer.put(md5Hash.getBytes());
	}

	@Override
	public Integer size() {
		return FIXED_SIZE + md5Hash.length() + otadFileUrl.length() ;
	}

	@Override
	public Long getEventId() {
		return eventId;
	}
	
	public static OtadRequestControl fromByteBuffer(ByteBuffer byteBuffer) {
	    byteBuffer.get();
	    long eventId = byteBuffer.getLong();
	    OtadFlags flags = OtadFlags.fromByteBuffer(byteBuffer);
	    int timeout = Unsigned.getUnsignedShort(byteBuffer);
	    int retries = Unsigned.getUnsignedShort(byteBuffer);
        int retryIntervalSeconds = Unsigned.getUnsignedShort(byteBuffer);
	    int count = Unsigned.getUnsignedShort(byteBuffer);
	    byte bytes[] = new byte[count];
	    byteBuffer.get(bytes);
	    String url = new String(bytes);
        count = Unsigned.getUnsignedShort(byteBuffer);
        bytes = new byte[count];
        byteBuffer.get(bytes);
        String md5Hash = new String(bytes);
        return new OtadRequestControl(eventId, flags, url, md5Hash, retries, retryIntervalSeconds, timeout);
	}

}
