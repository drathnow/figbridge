package zedi.pacbridge.app.controls;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.SiteAddress;

public interface ControlResponseStrategyFactory{
    public ControlResponseStrategy responseStrategyForControl(Control control, SiteAddress siteAddress);
}