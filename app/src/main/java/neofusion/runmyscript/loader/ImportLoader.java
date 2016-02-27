package neofusion.runmyscript.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import neofusion.runmyscript.backup.BackupHelper;
import neofusion.runmyscript.database.ScriptItemDataSource;
import neofusion.runmyscript.model.ImportResult;
import neofusion.runmyscript.model.ScriptItem;

public class ImportLoader extends AsyncTaskLoader<ImportResult> {
    private BackupHelper mBackupHelper;
    private ImportResult mImportResult;
    private ScriptItemDataSource mScriptItemDataSource;
    private String mFile;

    public ImportLoader(Context context, String file) {
        super(context);
        mScriptItemDataSource = new ScriptItemDataSource(context);
        mBackupHelper = new BackupHelper(context);
        mFile = file;
    }

    @Override
    public ImportResult loadInBackground() {
        try {
            ArrayList<ScriptItem> scriptItems = mBackupHelper.importFromFile(mFile);
            mScriptItemDataSource.insert(scriptItems);
            mImportResult = new ImportResult(true);
        } catch (Exception e) {
            mImportResult = new ImportResult(false, e);
        }
        return mImportResult;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mImportResult == null) {
            forceLoad();
        } else {
            deliverResult(mImportResult);
        }
    }
}