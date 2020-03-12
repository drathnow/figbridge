package zedi.fg.tester.util;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ConfigurationFileSerializer implements ConfigurationSerializer
{
    private File configFile;
    
    public ConfigurationFileSerializer(File configFile)
    {
        this.configFile = configFile;
    }

    @Override
    public void saveConfiguration(Configuration configuration) throws Exception
    {
        JAXBContext contextObj = JAXBContext.newInstance(Configuration.class); 
        Marshaller marshaller = contextObj.createMarshaller();  
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(configuration, new FileOutputStream(configFile));
    }

    @Override
    public Configuration loadConfiguration() throws Exception
    {
        JAXBContext contextObj = JAXBContext.newInstance(Configuration.class); 
        Unmarshaller unmarshaller = contextObj.createUnmarshaller();
        return (Configuration)unmarshaller.unmarshal(configFile);
    }    
}
