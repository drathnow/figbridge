package zedi.pacbridge.app.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * Calculates the average value for a set of values.  This class is not thread safe.  If you
 * require thread safe average values, use SynchronizedAverageValue.
 *
 */
public class AverageValue implements Serializable {
	
	static final long serialVersionUID = 1001;
	
	long count;
	long total;
	long max;
	
	public void addValue(long value) {
		total += value;
		count++;
		max = Math.max(value, max);
	}

	public long getAverage() {
		return count == 0 ? 0 : total/count;
	}

	public long getMax() {
		return max;
	}

	public void reset() {
		count = 0;
		total = 0;
		max = 0;
	}

	public long getTotal() {
		return total;
	}

	public void serialize(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeLong(count);
		dataOutputStream.writeLong(total);
		dataOutputStream.writeLong(max);
	}

	public void deserialize(DataInputStream dataInputStream) throws IOException {
		count = dataInputStream.readLong();
		total = dataInputStream.readLong();
		max = dataInputStream.readLong();
	}
}
