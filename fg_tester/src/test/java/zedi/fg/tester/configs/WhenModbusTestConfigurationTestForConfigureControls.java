package zedi.fg.tester.configs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.app.events.zios.ZiosFieldTypeLibrary;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class WhenModbusTestConfigurationTestForConfigureControls
{
	private ZiosFieldTypeLibrary fieldTypeLibrary;

	@Before
	public void setUp() 
	{
		InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
		assert(inputStream != null);
		fieldTypeLibrary = new ZiosFieldTypeLibrary(inputStream);
	}
	@Test
	public void shouldBuildControl() 
	{
		ModbusTestConfiguration configuration = new ModbusTestConfiguration(fieldTypeLibrary);
		List<ConfigureControl> controls = configuration.configureControls();
		
		assertEquals(8, controls.size());
		ConfigureControl control = controls.get(4);
		assertEquals(ObjectType.DEVICE, control.getObjectType());
		
		List<Action> actions = control.getActions();
		
		assertEquals(2,  actions.size());
		Action action = actions.get(0);
		assertEquals(ActionType.ADD, action.getActionType());
		List<Field<?>> fields = action.getFields();
		assertTrue(fieldIsPresentInFieldList("CorrelationId", fields));
	}
	
	private boolean fieldIsPresentInFieldList(String name, List<Field<?>> fields)
	{
		for (Field<?> field : fields)
			if (field.getFieldType().getName().equals(name))
				return true;
		return false;
	}
}
