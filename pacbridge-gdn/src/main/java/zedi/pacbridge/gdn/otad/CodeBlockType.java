package zedi.pacbridge.gdn.otad;

public class CodeBlockType {

    private static final int FLASH_BASE_ADDRESS = 0x380000;
    private static final int FLASH_TYPE_NUMBER = 2;
    private static final int INTERNAL_EEPROM_BASE_ADDRESS = 0x00;
    private static final int INTERNAL_EEPROM_NUMBER = 0;
    
    public static final CodeBlockType Flash = new CodeBlockType("Flash", FLASH_BASE_ADDRESS, FLASH_TYPE_NUMBER);
    public static final CodeBlockType InternalEEProm = new CodeBlockType("Iternal EE PROM", INTERNAL_EEPROM_BASE_ADDRESS, INTERNAL_EEPROM_NUMBER);
    
    private String name;
    private int baseAddress;
    private int typeNumber;
    
    private CodeBlockType(String name, int baseAddress, int typeNumber) {
        this.name = name;
        this.typeNumber = typeNumber;
        this.baseAddress = baseAddress;
    }
    
    public String getName() {
        return name;
    }
    
    public int getTypeNumber() {
        return typeNumber;
    }

    public int getBaseAddress() {
        return baseAddress;
    }
    
    public static CodeBlockType codeBlockTypeForTypeNumber(int typeNumber) {
        switch (typeNumber) {
            case INTERNAL_EEPROM_NUMBER : return InternalEEProm;
            case FLASH_TYPE_NUMBER : return Flash;
        }
        throw new IllegalArgumentException("Unknown code block type number: " + typeNumber);
    }
    
    public static CodeBlockType codeBlockTypeForStartingAddress(int startingAddress) {
        return startingAddress >= FLASH_BASE_ADDRESS ? Flash : InternalEEProm;
    }
}
