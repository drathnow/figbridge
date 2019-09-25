package zedi.pacbridge.gdn.messages;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.net.Message;


public abstract class AddIoPointControl extends IoPointControl implements Message, Control {
    protected Integer rtuAddress;
    protected Integer f1;
    protected Integer f2;
    protected Integer f3;
    protected Integer f4;

    protected AddIoPointControl(GdnMessageType messageType) {
        super(messageType);
    }

    protected AddIoPointControl(GdnMessageType messageType, GdnDataType dataType, Integer index, Integer pollsetNumber, Integer rtuAddress, Integer f1, Integer f2, Integer f3, Integer f4) {
        super(messageType, index, pollsetNumber);
        this.dataType = dataType;
        this.rtuAddress = rtuAddress;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
    }

    public Integer getRtuAddress() {
        return rtuAddress;
    }

    public Integer getF1() {
        return f1;
    }

    public Integer getF2() {
        return f2;
    }

    public Integer getF3() {
        return f3;
    }

    public Integer getF4() {
        return f4;
    }
}