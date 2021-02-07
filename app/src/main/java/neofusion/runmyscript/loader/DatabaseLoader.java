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