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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import neofusion.runmyscript.model.ProcessResult;

public class ProcessLoader extends AsyncTaskLoader<ProcessResult> {
    private Process mProcess = null;
    private ProcessResult mProcessResult;
    private String[] mCmd;

    public ProcessLoader(Context context, String[] cmd) {
        super(context);
        mCmd = cmd;
    }

    @Override
    public ProcessResult loadInBackground() {
        boolean success = false;
        StringBuilder stringBuilder = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder(mCmd);
        processBuilder.redirectErrorStream(true);
        try {
            mProcess = processBuilder.start();
            InputStream in = mProcess.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mProcess != null) {
                mProcess.destroy();
            }
        }
        mProcessResult = new ProcessResult(success, stringBuilder.toString());
        return mProcessResult;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mProcessResult == null) {
            forceLoad();
        } else {
            deliverResult(mProcessResult);
        }
    }

    @Override
    protected void onStopLoading() {
        destroyProcess();
        super.onStopLoading();
    }

    private void destroyProcess() {
        if (mProcess != null) {
            try {
                mProcess.exitValue();
            } catch (IllegalThreadStateException e) {
                mProcess.destroy();
            }
        }
    }
}