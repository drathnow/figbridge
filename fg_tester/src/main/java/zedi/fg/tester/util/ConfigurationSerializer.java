package zedi.fg.tester.util;

public interface ConfigurationSerializer
{
    void saveConfiguration(Configuration configuration) throws Exception;
    Configuration loadConfiguration() throws Exception;
}
