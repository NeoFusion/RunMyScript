package neofusion.runmyscript.fragment;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.IOException;

import neofusion.runmyscript.R;
import neofusion.runmyscript.backup.BackupException;
import neofusion.runmyscript.loader.ExportLoader;
import neofusion.runmyscript.model.ExportResult;

public class ExportFragment extends Fragment implements LoaderManager.LoaderCallbacks<ExportResult> {
    private TextView mTextMessage;
    private View mProgressContainer;

    public ExportFragment() {
    }

    public static ExportFragment newInstance() {
        return new ExportFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        mProgressContainer = root.findViewById(R.id.progressContainer);
        mTextMessage = (TextView) root.findViewById(R.id.message);
        setTextShown(false, null);
        getLoaderManager().initLoader(0, null, this);
    }

    private void setTextShown(boolean shown, String text) {
        if (shown) {
            mTextMessage.setText(text);
            mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mTextMessage.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            mProgressContainer.setVisibility(View.GONE);
            mTextMessage.setVisibility(View.VISIBLE);
        } else {
            mTextMessage.setText("");
            mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            mTextMessage.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mProgressContainer.setVisibility(View.VISIBLE);
            mTextMessage.setVisibility(View.GONE);
        }
    }

    public boolean hasRunningLoaders() {
        return getLoaderManager().hasRunningLoaders();
    }

    @Override
    public Loader<ExportResult> onCreateLoader(int id, Bundle args) {
        return new ExportLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ExportResult> loader, ExportResult data) {
        if (data.isSuccess()) {
            String message = String.format(getString(R.string.message_export_finished), data.getPath());
            setTextShown(true, message);
        } else {
            String errorMessage;
            Exception e = data.getException();
            if (e instanceof IOException) {
                errorMessage = e.getMessage();
            } else if (e instanceof BackupException) {
                errorMessage = e.getMessage();
            } else if (e instanceof SQLException) {
                errorMessage = getString(R.string.error_database);
            } else {
                errorMessage = getString(R.string.error_unknown);
            }
            setTextShown(true, String.format(getString(R.string.message_export_error), errorMessage));
        }
    }

    @Override
    public void onLoaderReset(Loader<ExportResult> loader) {
    }
}