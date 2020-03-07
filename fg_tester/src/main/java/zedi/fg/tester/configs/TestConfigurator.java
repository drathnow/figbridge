package zedi.fg.tester.configs;

import zedi.pacbridge.zap.messages.FieldTypeLibrary;

public class TestConfigurator
{
	private FieldTypeLibrary fieldTypeLibrary;
	private TestConfigurationSetupCoordinator setupCoordinator;
	
	public TestConfigurator(FieldTypeLibrary fieldTypeLibrary, TestConfigurationSetupCoordinator setupCoordinator)
	{
		this.fieldTypeLibrary = fieldTypeLibrary;
		this.setupCoordinator = setupCoordinator;
	}
	
	public void setupModbusTestConfiguration()
	{
		ModbusTestConfiguration config =  new ModbusTestConfiguration(fieldTypeLibrary);
		setupCoordinator.addConfigureControls(config.configureControls());
	}
}
