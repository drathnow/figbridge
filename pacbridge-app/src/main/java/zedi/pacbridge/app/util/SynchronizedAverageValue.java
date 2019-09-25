package zedi.pacbridge.app.util;

import java.io.Serializable;

/**
 * Synchronized sub class of AverageValue.
 */
public class SynchronizedAverageValue extends AverageValue implements Serializable {

	static final long serialVersionUID = 1001;
	
	@Override
	public void addValue(long value) {
		synchronized (this) {
			super.addValue(value);
		}
	}
	
	@Override
	public long getMax() {
		synchronized (this) {
			return super.getMax();
		}
	}
	
	@Override
	public long getAverage() {
		synchronized (this) {
			return super.getAverage();
		}
	}
	
	@Override
	public void reset() {
		synchronized (this) {
			super.reset();
		}
	}
	
	@Override
	public long getTotal() {
		synchronized (this) {
			return super.getTotal();
		}
	}
}
