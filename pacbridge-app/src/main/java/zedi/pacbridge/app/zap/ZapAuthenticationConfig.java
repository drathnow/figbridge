package zedi.pacbridge.app.zap;

import org.jdom2.Element;

public class ZapAuthenticationConfig {

    public static final String ROOT_ELEMENT_TAG = "Authentication"; 
    public static final String MODE_TAG = "Mode";
    public static final String MATCH_NAME_RE_TAG = "MatchNameRE";

    private ZapAuthenticationMode authenticationMode;
    private String matchNameRe;
    
    private ZapAuthenticationConfig(ZapAuthenticationMode authenticationMode, String matchNameRe) {
        this.authenticationMode = authenticationMode;
        this.matchNameRe = matchNameRe;
    }

    public ZapAuthenticationMode getAuthenticationMode() {
        return authenticationMode;
    }

    public String getMatchNameRe() {
        return matchNameRe;
    }

    public static ZapAuthenticationConfig authenticationConfigForElement(Element element) {
        ZapAuthenticationMode authenticationMode = ZapAuthenticationMode.None;
        String matchNameRe = null;
        if (element != null) {
            String text = element.getChildText(MODE_TAG);
            if (text != null) {
                authenticationMode = ZapAuthenticationMode.authenticationModeForName(text);
                matchNameRe = element.getChildText(MATCH_NAME_RE_TAG);
            }
        }
        return new ZapAuthenticationConfig(authenticationMode, matchNameRe);
    }

}
