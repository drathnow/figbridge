package zedi.fg.tester.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WhenConfigurationFileSerializerIsAskedToSerializeToFile
{
    
    @Before
    public void setUp() 
    {
        new File("foo.xml").delete();
    }
    
    @After
    public void tearDown() 
    {
        new File("foo.xml").delete();
    }
    
    @Test
    public void shouldWriteXmlToFile() throws Exception
    {
        Configuration oldConfig = new Configuration();
        oldConfig.setListeningAddress("1.2.3.4");
        oldConfig.setPort(3100);
        
        ConfigurationFileSerializer serializer = new ConfigurationFileSerializer(new File("foo.xml"));
        serializer.saveConfiguration(oldConfig);
        
        Configuration newConfig = serializer.loadConfiguration();
        assertNotNull(newConfig);
        assertEquals(oldConfig.getListeningAddress(), newConfig.getListeningAddress());
        assertEquals(oldConfig.getPort(), newConfig.getPort());
    }
    
}
