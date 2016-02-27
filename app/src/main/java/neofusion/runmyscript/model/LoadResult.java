package neofusion.runmyscript.model;

import java.util.ArrayList;

public class LoadResult {
    private boolean mSuccess;
    private ArrayList<ScriptItem> mScriptItems;
    private Exception mException;

    public LoadResult(boolean success, ArrayList<ScriptItem> scriptItems) {
        mSuccess = success;
        mScriptItems = scriptItems;
    }

    public LoadResult(boolean success, Exception exception) {
        mSuccess = success;
        mException = exception;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public ArrayList<ScriptItem> getScriptItems() {
        return mScriptItems;
    }

    public Exception getException() {
        return mException;
    }
}