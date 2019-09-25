package zedi.figdevice.emulator.config;

import java.net.InetSocketAddress;

import org.jdom2.Element;

import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;

public class Configuration {
    public static final String NUID_TAG = "Nuid";
    public static final String BRIDGE_TAG = "Bridge";
    public static final String ADDRESS_TAG = "Address";
    public static final String PORT_TAG = "Port";
    public static final String REPORT_GENERATORS_TAG = "ReportGenerator";
    
    private InetSocketAddress socketAddress;
    private BundledReportMessageGenerator messageGenerator;
    
    private Configuration(InetSocketAddress socketAddress, BundledReportMessageGenerator messageGenerator) {
        this.socketAddress = socketAddress;
        this.messageGenerator = messageGenerator;
    }

    public InetSocketAddress getBridgeAddress() {
        return socketAddress;
    }

    public BundledReportMessageGenerator reportGenerator() {
        return messageGenerator;
    }

    public static Configuration configurationFromElement(Element element) {
        String address = element.getChild(BRIDGE_TAG).getChild(ADDRESS_TAG).getText();
        Integer port = Integer.parseInt(element.getChild(BRIDGE_TAG).getChild(PORT_TAG).getText());
        Element generatorsElement = element.getChild(REPORT_GENERATORS_TAG);
        InetSocketAddress bridgeAddress = new InetSocketAddress(address, port);
        BundledReportMessageGenerator messageGenerator = ReportMessageGeneratorFactory.reportGeneratorForElement(generatorsElement);
        return new Configuration(bridgeAddress, messageGenerator);
    }

}
