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

package neofusion.runmyscript.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        mTextView = root.findViewById(R.id.textResult);
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

    @NonNull
    @Override
    public Loader<ProcessResult> onCreateLoader(int id, @Nullable Bundle args) {
        return new ProcessLoader(getActivity(), mCmd);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ProcessResult> loader, ProcessResult data) {
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
    public void onLoaderReset(@NonNull Loader<ProcessResult> loader) {
    }
}