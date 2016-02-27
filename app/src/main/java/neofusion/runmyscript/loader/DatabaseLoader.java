package neofusion.runmyscript.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import neofusion.runmyscript.database.ScriptItemDataSource;
import neofusion.runmyscript.model.LoadResult;
import neofusion.runmyscript.model.ScriptItem;

public class DatabaseLoader extends AsyncTaskLoader<LoadResult> {
    private LoadResult mLoadResult;
    private ScriptItemDataSource mScriptItemDataSource;

    public DatabaseLoader(Context context) {
        super(context);
        mScriptItemDataSource = new ScriptItemDataSource(context);
    }

    @Override
    public LoadResult loadInBackground() {
        try {
            ArrayList<ScriptItem> items = mScriptItemDataSource.getAll();
            mLoadResult = new LoadResult(true, items);
        } catch (Exception e) {
            mLoadResult = new LoadResult(false, e);
        }
        return mLoadResult;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (takeContentChanged() || mLoadResult == null) {
            forceLoad();
        } else {
            deliverResult(mLoadResult);
        }
    }
}