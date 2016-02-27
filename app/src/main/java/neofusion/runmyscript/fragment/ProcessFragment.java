package neofusion.runmyscript.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import neofusion.runmyscript.R;
import neofusion.runmyscript.loader.ProcessLoader;
import neofusion.runmyscript.model.ProcessResult;

public class ProcessFragment extends Fragment implements LoaderManager.LoaderCallbacks<ProcessResult> {
    private static final String ARG_CMD = "cmd";

    private String[] mCmd;
    private View mResultContainer;
    private View mProgressContainer;
    private View mEmptyView;
    private TextView mTextView;

    public ProcessFragment() {
    }

    public static ProcessFragment newInstance(String[] cmd) {
        ProcessFragment fragment = new ProcessFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_CMD, cmd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCmd = getArguments().getStringArray(ARG_CMD);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        mTextView = (TextView) root.findViewById(R.id.textResult);
        mResultContainer = root.findViewById(R.id.resultContainer);
        mProgressContainer = root.findViewById(R.id.progressContainer);
        mEmptyView = root.findViewById(R.id.empty);
        setResultShown(false);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void setResultShown(boolean shown) {
        if (shown) {
            mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mResultContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            mProgressContainer.setVisibility(View.GONE);
            mResultContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            mResultContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mProgressContainer.setVisibility(View.VISIBLE);
            mResultContainer.setVisibility(View.GONE);
        }
    }

    public boolean hasRunningLoaders() {
        return getLoaderManager().hasRunningLoaders();
    }

    @Override
    public Loader<ProcessResult> onCreateLoader(int id, Bundle args) {
        return new ProcessLoader(getActivity(), mCmd);
    }

    @Override
    public void onLoadFinished(Loader<ProcessResult> loader, ProcessResult data) {
        if (data.getSuccess()) {
            if (!data.getResult().isEmpty()) {
                mEmptyView.setVisibility(View.GONE);
                mTextView.setText(data.getResult());
            }
        } else {
            Toast.makeText(getActivity(), R.string.error_running_exec, Toast.LENGTH_SHORT).show();
        }
        setResultShown(true);
    }

    @Override
    public void onLoaderReset(Loader<ProcessResult> loader) {
    }
}