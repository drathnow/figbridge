package zedi.fg.tester.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.annotation.XmlRootElement;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;

@XmlRootElement(name = "FgTester")
public class Configuration
{
	private String listeningAddress;
	private Integer port;
	
	public Configuration()
	{
		this.listeningAddress = "192.168.1.1";
		this.port = 3100;
	}
	
	public String getListeningAddress()
	{
		return listeningAddress;
	}
	
	public Integer getPort()
	{
		return port;
	}

	public void serialize(InputStream inputStream) throws Exception 
	{
		Element element = JDomUtilities.elementForInputStream(inputStream);
		listeningAddress = element.getChild("Listener").getChild("Address").getText();
		port = Integer.parseInt(element.getChild("Listener").getChild("Port").getText());
	}
	
	public void serialize(OutputStream outputStream) throws IOException
	{
		Element rootElement = new Element("FgTester");
		Element listenerElement = new Element("Listener");
		listenerElement.addContent(new Element("Address").setText(listeningAddress));
		listenerElement.addContent(new Element("Port").setText(port.toString()));
		rootElement.addContent(listenerElement);
		outputStream.write(JDomUtilities.xmlStringForElement(rootElement).getBytes());
	}
}
