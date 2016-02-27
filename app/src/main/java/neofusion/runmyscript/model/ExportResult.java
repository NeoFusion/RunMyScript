package neofusion.runmyscript.model;

public class ExportResult {
    private boolean mSuccess;
    private String mPath;
    private Exception mException;

    public ExportResult(boolean success, String path) {
        mSuccess = success;
        mPath = path;
    }

    public ExportResult(boolean success, Exception exception) {
        mSuccess = success;
        mException = exception;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getPath() {
        return mPath;
    }

    public Exception getException() {
        return mException;
    }
}