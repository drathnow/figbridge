package zedi.pacbridge.app.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.junit.Test;


public class AverageValueTest {
	
	@Test
	public void testSerialize() throws Exception {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(arrayOutputStream);
		
		AverageValue averageValue = new AverageValue();
		
		averageValue.addValue(2);
		averageValue.addValue(3);
		averageValue.addValue(5);
		
		assertEquals(3, averageValue.getAverage());
		assertEquals(5, averageValue.getMax());
		
		averageValue.serialize(dataOutputStream);
		
		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
		DataInputStream dataInputStream = new DataInputStream(arrayInputStream);
		
		AverageValue newAverageValue = new AverageValue();
		newAverageValue.deserialize(dataInputStream);
		
		assertEquals(10, averageValue.total);
		assertEquals(3, averageValue.count);
		assertEquals(5, averageValue.max);
	}

	@Test
	public void testGetValues() throws Exception {
		AverageValue averageValue = new AverageValue();
		
		averageValue.addValue(2);
		averageValue.addValue(3);
		averageValue.addValue(5);
		
		assertEquals(3, averageValue.getAverage());
		assertEquals(5, averageValue.getMax());
	}
	
	@Test
	public void testReset() throws Exception {
		AverageValue averageValue = new AverageValue();
		
		averageValue.addValue(2);
		averageValue.addValue(3);
		averageValue.addValue(5);
		
		averageValue.reset();
		
		assertEquals(0, averageValue.getAverage());
		assertEquals(0, averageValue.getMax());
	}
	
	@Test
	public void testGetTotal() throws Exception {
		AverageValue averageValue = new AverageValue();
		
		averageValue.addValue(2);
		averageValue.addValue(3);
		averageValue.addValue(5);
		
		assertEquals(10, averageValue.getTotal());
	}
}
