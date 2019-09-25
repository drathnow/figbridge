package zedi.pacbridge.utl;

import java.io.File;

public interface FileChangeHandler {
    public void fileHasBeenModified(File monitoredFile);
}
