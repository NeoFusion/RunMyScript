package neofusion.runmyscript.model;

public class ProcessResult {
    private boolean mSuccess;
    private String mResult;

    public ProcessResult(boolean success, String result) {
        mSuccess = success;
        mResult = result;
    }

    public boolean getSuccess() {
        return mSuccess;
    }

    public String getResult() {
        return mResult;
    }
}