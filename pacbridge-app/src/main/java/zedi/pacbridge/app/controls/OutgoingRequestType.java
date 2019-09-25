package zedi.pacbridge.app.controls;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class OutgoingRequestType extends NamedType implements Serializable {
    private static final long serialVersionUID = 1001;
    private static final int CONTROL_NUMBER = 1;
    private static final int OTAD_NUMBER = 2;

    public static final OutgoingRequestType CONTROL = new OutgoingRequestType("Control", CONTROL_NUMBER);
    public static final OutgoingRequestType OTAD = new OutgoingRequestType("OTAD", OTAD_NUMBER);

    private OutgoingRequestType() {
        this(null, null);
    }

    private OutgoingRequestType(String name, Integer number) {
        super(name, number);
    }

    public static OutgoingRequestType outgoingRequestTypeForNumber(Integer number) {
        switch (number) {
            case CONTROL_NUMBER :
                return CONTROL;
            case OTAD_NUMBER :
                return OTAD;
        }
        throw new IllegalArgumentException("Unknown OutgoingRequestType number : '" + number + "'");
    }
}
