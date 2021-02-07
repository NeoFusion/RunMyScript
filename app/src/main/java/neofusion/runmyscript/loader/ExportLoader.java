/*
 * Copyright 2013 Evgeniy NeoFusion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neofusion.runmyscript.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

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