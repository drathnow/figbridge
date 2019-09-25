package zedi.pacbridge.eventgen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.ibm.disthub2.spi.ConfigurationNotLockedException;

import zedi.pacbridge.eventgen.Main;
import zedi.pacbridge.eventgen.NotificationNames;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;

public class Configuration implements Notifiable {
    public static final String ROOT_ELEMENT_NAME = "EventGenerator";
    public static final String USERNAME_TAG = "Username";
    public static final String USERNAMES_TAG = "Usernames";
    public static final String JMS_HOST_TAG = "JmsHost";
    public static final String JMS_QMANAGER_TAG = "JmsQManager";
    public static final String EVENT_TOPIC_NAME_TAG = "EventTopicName";
    public static final String DEFUALT_TAG = "default";

    public static class Username {
        boolean isDefault;
        String username;

        public Username(boolean isDefault, String username) {
            this.isDefault = isDefault;
            this.username = username;
        }

        public boolean isDefualt() {
            return isDefault;
        }
        
        public String getUsername() {
            return username;
        }
    }

    private File loadedFromFile;
    private List<Username> usernames;
    private Username defaultUsername;
    private String jmsQManager;
    private String jmsHost;
    private String eventTopicName;
    
    public Configuration(File configFile) throws Exception {
        this(new FileInputStream(configFile));
        this.loadedFromFile = configFile;
    }
    
    public Configuration(InputStream inputStream) throws IOException, JDOMException {
        byte[] bytes = new byte[inputStream.available()];
        usernames = new ArrayList<>();
        inputStream.read(bytes);
        String xmlString = new String(bytes);
        Element element = JDomUtilities.jdomDocumentForXmlString(xmlString).getRootElement();
        jmsQManager = element.getChildText(JMS_QMANAGER_TAG);
        jmsHost = element.getChildText(JMS_HOST_TAG);
        eventTopicName = element.getChildText(EVENT_TOPIC_NAME_TAG);
        
        Element usernamesElement = element.getChild(USERNAMES_TAG);
        if (usernamesElement != null) {
            for (Element e : usernamesElement.getChildren(USERNAME_TAG)) {
                boolean isDef = Boolean.parseBoolean(e.getAttributeValue(DEFUALT_TAG, "false"));
                Username u = new Username(isDef, e.getText());
                if (isDef) {
                    if (defaultUsername == null)
                        defaultUsername = u;
                    else {
                        u.isDefault = false;
                    }
                }
                usernames.add(u);
            }
        }
    }
    
    public List<Username> getUsernames() {
        return usernames;
    }
    
    public void setDefaultUsername(String username) {
        for (Username u : usernames) {
            if (u.username.equals(username)) {
                u.isDefault = true;
                defaultUsername.isDefault = false;
                defaultUsername = u;
            }
        }
    }
    
    public String getUsername() {
        return defaultUsername.username;
    }
    
    public String getJmsQManager() {
        return jmsQManager;
    }
    
    public String getJmsHost() {
        return jmsHost;
    }
    
    public String getEventTopicName() {
        return eventTopicName;
    }
    
    public String asXmlString() {
        Element rootElement = new Element(ROOT_ELEMENT_NAME);
        Element usernamesElement = new Element(USERNAMES_TAG);
        for (Username u : usernames) {
            Element usernameElement = new Element(USERNAME_TAG);
            usernameElement.setText(u.username);
            if (u.isDefault)
                usernameElement.setAttribute("default", "true");
            usernamesElement.addContent(usernameElement);
        }
        rootElement.addContent(usernamesElement);
        rootElement.addContent(new Element(JMS_QMANAGER_TAG).setText(jmsQManager));
        rootElement.addContent(new Element(JMS_HOST_TAG).setText(jmsHost));
        rootElement.addContent(new Element(EVENT_TOPIC_NAME_TAG).setText(eventTopicName));
        return JDomUtilities.xmlStringForElement(rootElement);
    }
    
    public void save(File file) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        String xmlString = asXmlString();
        fos.write(xmlString.getBytes(), 0, asXmlString().length());
        fos.close();
    }

    @Override
    public void handleNotification(Notification notification) {
        if (notification.getName().equals(NotificationNames.DEFAULT_USERNAME_CHANGED)) {
            if (loadedFromFile != null)
                try {
                    Main.getConfiguration().save(loadedFromFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        
    }
}
