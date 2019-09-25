package zedi.figbridge.slapper;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;

public class ClassPathGenerator {

    public static void main(String[] args) {
        try {
            URLClassLoader classLoader = (URLClassLoader)ClassPathGenerator.class.getClassLoader();
            for (URL url : classLoader.getURLs()) {
                System.out.println("Url:" + url.toString());
            }
            StringBuffer stringBuffer = new StringBuffer();
            File file = new File("build.xml");
            Element element = JDomUtilities.elementForInputStream(new FileInputStream(file));
            List<Element> targetElements = element.getChildren("target");
            for (Element targetElement : targetElements) {
                if (targetElement.getAttributeValue("name").equals("zipit")) {
                    List<Element> zipFileElements = targetElement.getChild("zip").getChildren("zipfileset");
                    for (Element zipfileElement : zipFileElements) {
                        String name = zipfileElement.getAttributeValue("includes");
                        stringBuffer.append(name).append(" ");
                    }
                }
            }
            System.out.println("Class-Path: " + stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
