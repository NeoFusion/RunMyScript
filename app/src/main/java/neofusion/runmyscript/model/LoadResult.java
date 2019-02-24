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