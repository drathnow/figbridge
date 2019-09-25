package zedi.figbridge.slapper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import zedi.figbridge.slapper.config.FigDeviceConfig;

public class FigDevicesConfigListModel extends AbstractListModel<FigDeviceConfig> {

    private List<FigDeviceConfig> reportConfigs = new ArrayList<>();
    
    public FigDevicesConfigListModel(List<FigDeviceConfig> reportConfigs) {
        this.reportConfigs = reportConfigs;
    }

    @Override
    public int getSize() {
        return reportConfigs.size();
    }

    @Override
    public FigDeviceConfig getElementAt(int index) {
        return (index >= 0 && index < reportConfigs.size()) ? reportConfigs.get(index) : null;
    }

}
