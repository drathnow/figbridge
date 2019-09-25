package zedi.tools.topicreader;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

public class Main {

    public static void main(String[] args) {
        Properties arguments = CommandLineParser.argumentsFromCommandLine(args);
        if (isValidCommandLine(arguments) == false)
            usage();
        try {
            JmsImplementation implementator = new JmsImplementation(arguments.getProperty("hostName"), arguments.getProperty("qManName"));
            String destName = arguments.getProperty("destName");
            Destination destination = implementator.createDestination(destName);
            Connection connection = implementator.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(destination);
            MessageListener messageListener = new ConsoleMessageListener(arguments.containsKey("dumpHeader"));
            consumer.setMessageListener(messageListener);
            connection.start();
            synchronized (messageListener) {
                messageListener.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static boolean isValidCommandLine(Properties properties) {
        return (properties.containsKey("hostName")
                && properties.containsKey("qManName")
                && properties.containsKey("destName")
                && properties.getProperty("hostName") != null
                && properties.getProperty("qManName") != null
                && properties.getProperty("destName") != null);
    }
    
    private static void usage() {
        System.out.println("Usage: topicreader --hostName=<host> --qManName=<queue-manager-name> --destName=<dest-name> [--dumpHeader]");
        System.out.println("   --hostName     Name of host running the message server");
        System.out.println("   --qManName     Queue manager name");
        System.out.println("   --destName     Destination name. Use the native destination name.");
        System.out.println("                  Prefix topic with 'topic://' and queue with 'queue://'");
        System.exit(0);
    }
}
