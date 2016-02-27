package neofusion.runmyscript.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import neofusion.runmyscript.backup.BackupHelper;
import neofusion.runmyscript.database.ScriptItemDataSource;
import neofusion.runmyscript.model.ExportResult;
import neofusion.runmyscript.model.ScriptItem;

public class ExportLoader extends AsyncTaskLoader<ExportResult> {
    private BackupHelper mBackupHelper;
    private ExportResult mExportResult;
    private ScriptItemDataSource mScriptItemDataSource;

    public ExportLoader(Context context) {
        super(context);
        mScriptItemDataSource = new ScriptItemDataSource(context);
        mBackupHelper = new BackupHelper(context);
    }

    @Override
    public ExportResult loadInBackground() {
        try {
            ArrayList<ScriptItem> items = mScriptItemDataSource.getAll();
            String exportFilePath = mBackupHelper.exportToFile(items);
            mExportResult = new ExportResult(true, exportFilePath);
        } catch (Exception e) {
            mExportResult = new ExportResult(false, e);
        }
        return mExportResult;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mExportResult == null) {
            forceLoad();
        } else {
            deliverResult(mExportResult);
        }
    }
}