package zedi.pacbridge.app.controls;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.SiteAddress;

public class ProcessedControlAttachement {
    private SiteAddress siteAddress;
    private Control control;

    public ProcessedControlAttachement(SiteAddress siteAddress, Control control) {
        this.siteAddress = siteAddress;
        this.control = control;
    }
    
    public Control getControl() {
        return control;
    }
    
    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
}
