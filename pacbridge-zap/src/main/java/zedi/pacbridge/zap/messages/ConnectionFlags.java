package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

public class ConnectionFlags
{
	private static final int AUTH_MASK = 0x80;
	private static final int DATA_OUT_MASK = 0x40;
	private static final int DELAY_MASK = 0x20;

	private boolean authorized;
	private boolean delay;
	private boolean outBoundDataPending;

	public ConnectionFlags()
	{
	}

	public ConnectionFlags(int flags)
	{
		authorized = (flags & AUTH_MASK) == 0 ? false : true;
		outBoundDataPending = (flags & DATA_OUT_MASK) == 0 ? false : true;
		delay = (flags & DELAY_MASK) == 0 ? false : true;
	}

	public boolean isAuthorized()
	{
		return authorized;
	}

	public void setAuthorized(boolean authorized)
	{
		this.authorized = authorized;
	}

	public boolean isOutBoundDataPending()
	{
		return outBoundDataPending;
	}

	public void setOutBoundDataPending(boolean outBoundDataPending)
	{
		this.outBoundDataPending = outBoundDataPending;
	}

	public boolean isDelay()
	{
		return delay;
	}

	public void setDelay(boolean delay)
	{
		this.delay = delay;
	}

	public String toString()
	{
		return "{auth=" + isAuthorized() + ", outbnd=" + isOutBoundDataPending() + ", delay=" + isDelay() + "}";
	}

	public void serialize(ByteBuffer byteBuffer)
	{
		int value = 0;
		value |= isAuthorized() ? AUTH_MASK : 0;
		value |= isOutBoundDataPending() ? DATA_OUT_MASK : 0;
		value |= isDelay() ? DELAY_MASK : 0;
		byteBuffer.put((byte) value);
	}
}