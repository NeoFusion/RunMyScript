package neofusion.runmyscript.model;

public class ImportResult {
    private boolean mSuccess;
    private Exception mException;

    public ImportResult(boolean success) {
        mSuccess = success;
    }

    public ImportResult(boolean success, Exception exception) {
        mSuccess = success;
        mException = exception;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public Exception getException() {
        return mException;
    }
}