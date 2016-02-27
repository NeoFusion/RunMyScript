package neofusion.runmyscript.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

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