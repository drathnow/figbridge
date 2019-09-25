package zedi.pacbridge.app.zap;

import zedi.pacbridge.utl.NamedType;

public class ZapAuthenticationMode extends NamedType {

    private static final Integer NONE_NUMBER = 1;
    private static final Integer PROMISCUOUS_NUMBER = 2; 
    
    private static final String NONE_NAME= "None";
    private static final String PROMISCUOUS_NAME = "Promiscuous"; 

    public static final ZapAuthenticationMode None = new ZapAuthenticationMode(NONE_NAME, NONE_NUMBER);
    public static final ZapAuthenticationMode Promiscuous = new ZapAuthenticationMode(PROMISCUOUS_NAME, PROMISCUOUS_NUMBER);

    private ZapAuthenticationMode(String name, Integer number) {
        super(name, number);
    }
    
    public static ZapAuthenticationMode authenticationModeForName(String name) {
        if (NONE_NAME.equalsIgnoreCase(name))
            return None;
        if (PROMISCUOUS_NAME.equalsIgnoreCase(name))
            return Promiscuous;
        throw new IllegalArgumentException("Invalid value for Authentication Mode: '" + name + "'");
    }
}
