package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

import zedi.pacbridge.net.HeaderType;

public class SwtHeaderType implements HeaderType, Serializable{
    static final int HEADER_VERSION10 = 10;
    static final int HEADER_VERSION12 = 12;
    
    public static final SwtHeaderType Header10 = new SwtHeaderType(false, HEADER_VERSION10); 
    public static final SwtHeaderType Header12 = new SwtHeaderType(true, HEADER_VERSION12);
    
    private boolean supportsSessions;
    private int typeNumber;
    
    private SwtHeaderType(boolean supportsSessions, int typeNumber) {
        this.supportsSessions = supportsSessions;
        this.typeNumber = typeNumber;
    }
    
    public boolean supportsSession() {
        return supportsSessions;
    }

    public Integer  getTypeNumber() {
        return typeNumber;
    }
    
    @Override
    public String getName() {
        return toString();
    }
    
    public static SwtHeaderType headerTypeForVersionNumber(int versionNumber) {
        switch (versionNumber) {
            case SwtHeaderType.HEADER_VERSION10 :
                return Header10;
            case SwtHeaderType.HEADER_VERSION12 :
                return Header12;
            default :
                throw new IllegalArgumentException("Invalid header version number: " + versionNumber); 
        }
    }
    
    @Override
    public String toString() {
        return "SWT Vesion: " + typeNumber; 
    }
}
