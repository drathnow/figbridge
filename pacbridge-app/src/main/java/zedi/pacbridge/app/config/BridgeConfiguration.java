package zedi.pacbridge.app.config;

import java.util.List;
import java.util.Set;

import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.messages.FieldType;

public interface BridgeConfiguration {
    public Integer getSystemId();
    public String getBridgeName();
    public Set<SiteAddress> addressExclusionList();
    public List<NetworkConfig> getNetworkConfigurations();
    public List<FieldType> getFieldTypes();
}
