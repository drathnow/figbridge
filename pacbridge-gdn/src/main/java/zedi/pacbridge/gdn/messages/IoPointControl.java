package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.gdn.GdnDataType;


public abstract class IoPointControl extends GdnMessageBase implements Serializable {
    static final long serialVersionUID = 1001;

    protected Integer pollsetNumber = 0;
    protected Integer index = 0;
    protected GdnDataType dataType;
    protected GdnIoPointClass ioPointClass;

    protected IoPointControl(GdnMessageType messageType) {
        super(messageType);
    }
    
    public IoPointControl(GdnMessageType messageType, GdnDataType dataType, Integer index, Integer pollsetNumber) {
        super(messageType);
        this.dataType = dataType;
        this.index = index;
        this.pollsetNumber = pollsetNumber;
    }
    
    public IoPointControl(GdnMessageType messageType, Integer index, Integer pollsetNumber) {
        super(messageType);
        this.index = index;
        this.pollsetNumber = pollsetNumber;
    }

    protected IoPointControl(GdnMessageType messageType, GdnDataType dataType) {
        super(messageType);
        this.dataType = dataType;
    }
    
    public Integer getIndex() {
        return index;
    }
    
    public Long getEventId() {
        return 0L;
    }

    public GdnIoPointClass getIoPointClass() {
        return ioPointClass;
    }
    
    public Integer getPollSetNumber() {
        return pollsetNumber;
    }

    public GdnDataType getDataType() {
        return dataType;
    }
    
    public void setIoPointClass(GdnIoPointClass ioPointClass) {
        this.ioPointClass = ioPointClass;
    }
        
    protected int typeNumberForSerialization() {
        int serializationNumber = dataType.getNumber();
        if (ioPointClass != null && ioPointClass == GdnIoPointClass.IoBoard)
            serializationNumber = serializationNumber | GdnIoPointClass.INTERNAL_ATTRIBUTE_VALUE;
        return serializationNumber;
    }
    
    protected void deserializeTypeNumber(int typeNumber) {
        dataType = GdnDataType.dataTypeForTypeNumber(typeNumber & GdnIoPointClass.VALUE_MASK);
        if ((typeNumber & GdnIoPointClass.INTERNAL_ATTRIBUTE_VALUE) != 0)
            setIoPointClass(GdnIoPointClass.IoBoard);
    }
}