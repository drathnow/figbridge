package zedi.pacbridge.zap.messages;

import zedi.pacbridge.utl.NamedType;

public class OtadStatus extends NamedType {
	private static final long serialVersionUID = 1L;

	public static final String OTAD_DOWNLOADING_NAME = "Downloading";
	public static final String OTAD_UNPACKING_HAME = "Unpacking";
	public static final String OTAD_INSTALLING_NAME = "Installing";
	public static final String OTAD_COMPLETE_NAME = "Complete";
	public static final String OTAD_FAILED_NAME = "Failed";

	public static final int OTAD_DOWNLOADING_NUMBER = 1;
    public static final int OTAD_UNPACKING_NUMBER = 2;
	public static final int OTAD_INSTALLING_NUMBER = 3;
	public static final int OTAD_COMPLETE_NUMBER = 4;
	public static final int OTAD_FAILED_NUMBER = 5;

	public static final OtadStatus DOWNLOADING = new OtadStatus(OTAD_DOWNLOADING_NAME, OTAD_DOWNLOADING_NUMBER);
    public static final OtadStatus UNPACKING = new OtadStatus(OTAD_UNPACKING_HAME, OTAD_UNPACKING_NUMBER);
	public static final OtadStatus INSTALLING = new OtadStatus(OTAD_INSTALLING_NAME, OTAD_INSTALLING_NUMBER);
	public static final OtadStatus COMPLETE = new OtadStatus(OTAD_COMPLETE_NAME, OTAD_COMPLETE_NUMBER);
	public static final OtadStatus FAILED = new OtadStatus(OTAD_FAILED_NAME, OTAD_FAILED_NUMBER);

	private OtadStatus(String name, Integer number) {
		super(name, number);
	}

	public static OtadStatus otadStatusForNumber(int number) {
		if (OTAD_DOWNLOADING_NUMBER  == number)
			return DOWNLOADING;
        if (OTAD_UNPACKING_NUMBER  == number)
            return UNPACKING;
		if (OTAD_INSTALLING_NUMBER  == number)
			return INSTALLING;
		if (OTAD_COMPLETE_NUMBER  == number)
			return COMPLETE;
		if (OTAD_FAILED_NUMBER  == number)
			return FAILED;
		throw new IllegalArgumentException("Unknown OTAD status '" + number + "'");
	}
}