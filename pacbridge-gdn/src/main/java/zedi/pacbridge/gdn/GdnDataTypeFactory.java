package zedi.pacbridge.gdn;

import zedi.pacbridge.net.DataType;
import zedi.pacbridge.net.DataTypeFactory;

public class GdnDataTypeFactory implements DataTypeFactory {

    @Override
    public DataType dataTypeForName(String name) {
        return GdnDataType.dataTypeForName(name);
    }

}
