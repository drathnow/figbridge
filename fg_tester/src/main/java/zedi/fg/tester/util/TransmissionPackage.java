package zedi.fg.tester.util;

public class TransmissionPackage
{
	public static enum TYPE
	{
		BYTES_RCV, BYTES_TRX;
	}

	private byte[] bytes;
	private int offset; 
	private int length;
	private TYPE type;

	public TransmissionPackage(TYPE type, byte[] bytes, int offset, int length)
	{
		this.bytes = bytes;
		this.length = length;
		this.type = type;
	}

	public byte[] getBytes()
	{
		return bytes;
	}

	public int getLength()
	{
		return length;
	}

	public int getOffset()
	{
		return offset;
	}
	
	public TYPE getType()
	{
		return type;
	}

}
