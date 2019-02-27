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

import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

import neofusion.runmyscript.R;
import neofusion.runmyscript.backup.BackupException;
import neofusion.runmyscript.loader.ImportLoader;
import neofusion.runmyscript.model.ImportResult;

public class ImportFragment extends Fragment implements LoaderManager.LoaderCallbacks<ImportResult> {
    private static final String ARG_FILE = "file";

    private String mFile;
    private TextView mTextMessage;
    private View mProgressContainer;

    public static ImportFragment newInstance(String file) {
        ImportFragment fragment = new ImportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE, file);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFile = getArguments().getString(ARG_FILE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mTextMessage = root.findViewById(R.id.message);
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

    @NonNull
    @Override
    public Loader<ImportResult> onCreateLoader(int id, Bundle args) {
        return new ImportLoader(getActivity(), mFile);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ImportResult> loader, ImportResult data) {
        if (data.isSuccess()) {
            setTextShown(true, getString(R.string.message_import_finished));
        } else {
            String errorMessage;
            Exception e = data.getException();
            if (e instanceof FileNotFoundException) {
                errorMessage = getString(R.string.error_file_not_found);
            } else if (e instanceof XmlPullParserException) {
                errorMessage = getString(R.string.error_xml_parse);
            } else if (e instanceof SQLException) {
                errorMessage = getString(R.string.error_database);
            } else if (e instanceof IOException) {
                errorMessage = e.getMessage();
            } else if (e instanceof BackupException) {
                errorMessage = e.getMessage();
            } else {
                errorMessage = getString(R.string.error_unknown);
            }
            setTextShown(true, String.format(getString(R.string.message_import_error), errorMessage));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ImportResult> loader) {
    }
}