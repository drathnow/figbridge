package zedi.fg.tester.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;

@XmlRootElement(name = "FgTester")
public class Configuration
{
    private File configFile;
	private String listeningAddress;
	private Integer port;
	
	public Configuration(File configFile)
	{
	    this.configFile = configFile;
	}
	
	public String getListeningAddress()
	{
		return listeningAddress;
	}
	
	public Integer getPort()
	{
		return port;
	}

	public void load() throws Exception 
	{
	    
		Element element = JDomUtilities.elementForInputStream(new FileInputStream(configFile));
		listeningAddress = element.getChild("Listener").getChild("Address").getText();
		String portStr = element.getChild("Listener").getChild("Port").getText();
		if (portStr.length() > 0)
		    port = Integer.parseInt(element.getChild("Listener").getChild("Port").getText());
	}
	
	public void save() throws IOException
	{
		Element rootElement = new Element("FgTester");
		Element listenerElement = new Element("Listener");
		listenerElement.addContent(new Element("Address").setText(listeningAddress == null ? "" : listeningAddress));
		listenerElement.addContent(new Element("Port").setText(port == null ? "" : port.toString()));
		rootElement.addContent(listenerElement);
		FileOutputStream outputStream = new FileOutputStream(configFile);
		outputStream.write(JDomUtilities.xmlStringForElement(rootElement).getBytes());
		outputStream.close();
	}
}
