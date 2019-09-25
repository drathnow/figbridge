package zedi.pacbridge.zap.values;

import zedi.pacbridge.net.DataType;
import zedi.pacbridge.net.DataTypeFactory;

public class ZapDataTypeFactory implements DataTypeFactory {

    @Override
    public DataType dataTypeForName(String name) {
        return ZapDataType.dataTypeForName(name);
    }

}
