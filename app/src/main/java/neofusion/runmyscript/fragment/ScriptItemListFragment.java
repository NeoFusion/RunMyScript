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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import neofusion.runmyscript.R;
import neofusion.runmyscript.backup.BackupException;
import neofusion.runmyscript.backup.BackupHelper;
import neofusion.runmyscript.database.ScriptItemDataSource;
import neofusion.runmyscript.fragment.dialog.AboutDialogFragment;
import neofusion.runmyscript.fragment.dialog.AddScriptDialogFragment;
import neofusion.runmyscript.fragment.dialog.ClearDialogFragment;
import neofusion.runmyscript.fragment.dialog.CopyScriptDialogFragment;
import neofusion.runmyscript.fragment.dialog.DeleteScriptDialogFragment;
import neofusion.runmyscript.fragment.dialog.EditScriptDialogFragment;
import neofusion.runmyscript.fragment.dialog.ExportDialogFragment;
import neofusion.runmyscript.fragment.dialog.ImportDialogFragment;
import neofusion.runmyscript.fragment.dialog.ProtectDialogFragment;
import neofusion.runmyscript.fragment.dialog.RunScriptDialogFragment;
import neofusion.runmyscript.loader.DatabaseLoader;
import neofusion.runmyscript.model.LoadResult;
import neofusion.runmyscript.model.ScriptItem;

public class ScriptItemListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<LoadResult> {
    private static final int REQUEST_ADD_SCRIPT_DIALOG = 1;
    private static final int REQUEST_RUN_SCRIPT_DIALOG = 2;
    private static final int REQUEST_EDIT_SCRIPT_DIALOG = 3;
    private static final int REQUEST_COPY_SCRIPT_DIALOG = 4;
    private static final int REQUEST_DELETE_SCRIPT_DIALOG = 5;
    private static final int REQUEST_IMPORT_DIALOG = 6;
    private static final int REQUEST_EXPORT_DIALOG = 7;
    private static final int REQUEST_CLEAR_DIALOG = 8;
    private static final int REQUEST_PROTECT_DIALOG = 9;

    public static final int REQUEST_STORAGE_FOR_IMPORT = 0;
    public static final int REQUEST_STORAGE_FOR_EXPORT = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private OnFragmentInteractionListener mInteractionListener;
    private ScriptItemDataSource mScriptItemDataSource;
    private View mRoot;
    private View mListProgressContainer;
    private View mListContainer;
    private ListView mList;
    private ArrayAdapter<ScriptItem> mAdapter;

    private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mInteractionListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
        mRoot = getView();
        if (mRoot == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        mListProgressContainer = mRoot.findViewById(R.id.listProgressContainer);
        mListContainer = mRoot.findViewById(R.id.listContainer);
        mList = mListContainer.findViewById(android.R.id.list);
        mScriptItemDataSource = new ScriptItemDataSource(getActivity());
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(mAdapter);
        setItemListShown(false);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInteractionListener = null;
    }

    private void addItem(ScriptItem scriptItem) {
        try {
            mScriptItemDataSource.insert(scriptItem);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        updateList();
    }

    private void runItem(ScriptItem scriptItem) {
        String[] cmd;
        switch (scriptItem.getType()) {
            case ScriptItem.TYPE_SINGLE_COMMAND:
                cmd = new String[3];
                if (scriptItem.getSu()) {
                    cmd[0] = "su";
                } else {
                    cmd[0] = "sh";
                }
                cmd[1] = "-c";
                cmd[2] = scriptItem.getPath();
                break;
            case ScriptItem.TYPE_PATH_TO_FILE:
                if (scriptItem.getSu()) {
                    cmd = new String[3];
                    cmd[0] = "su";
                    cmd[1] = "-c";
                    cmd[2] = "sh " + scriptItem.getPath();
                } else {
                    cmd = new String[2];
                    cmd[0] = "sh";
                    cmd[1] = scriptItem.getPath();
                }
                break;
            default:
                return;
        }
        if (mInteractionListener != null) {
            mInteractionListener.runProcess(cmd);
        }
    }

    private void editItem(ScriptItem scriptItem) {
        if (scriptItem.getId() != 0) {
            try {
                mScriptItemDataSource.update(scriptItem);
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
            }
            updateList();
        }
    }

    private void copyItem(ScriptItem scriptItem) {
        String newName = scriptItem.getName() + " (copy)";
        int nameMaxLength = getResources().getInteger(R.integer.name_max_length);
        if (newName.length() > nameMaxLength) {
            newName = newName.substring(0, nameMaxLength);
        }
        scriptItem.setName(newName);
        try {
            mScriptItemDataSource.insert(scriptItem);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        updateList();
    }

    private void deleteItem(long id) {
        if (id != 0) {
            try {
                mScriptItemDataSource.delete(id);
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
            }
            updateList();
        }
    }

    private void importItems(String selectedItem) {
        if (selectedItem.isEmpty()) {
            Toast.makeText(getActivity(), R.string.error_file_not_chosen, Toast.LENGTH_SHORT).show();
        } else {
            if (mInteractionListener != null) {
                getLoaderManager().destroyLoader(0);
                mInteractionListener.runImport(selectedItem);
            }
        }
    }

    private void exportItems() {
        if (mInteractionListener != null) {
            getLoaderManager().destroyLoader(0);
            mInteractionListener.runExport();
        }
    }

    private void clearItems() {
        try {
            mScriptItemDataSource.deleteAll();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        updateList();
    }

    private void doActionImport() {
        File[] files;
        try {
            BackupHelper backupHelper = new BackupHelper(getActivity());
            files = backupHelper.getFiles();
        } catch (BackupException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        String[] fileItems = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileItems[i] = files[i].getName();
        }
        Arrays.sort(fileItems, ALPHABETICAL_ORDER);
        ImportDialogFragment importDialog = ImportDialogFragment.newInstance(fileItems);
        importDialog.setTargetFragment(this, REQUEST_IMPORT_DIALOG);
        importDialog.show(getFragmentManager(), importDialog.getClass().getName());
    }

    private void doActionExport() {
        ExportDialogFragment exportDialog = ExportDialogFragment.newInstance();
        exportDialog.setTargetFragment(this, REQUEST_EXPORT_DIALOG);
        exportDialog.show(getFragmentManager(), exportDialog.getClass().getName());
    }

    private boolean checkStoragePermissions() {
        return (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermissionForImport() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mRoot, R.string.permission_storage_import_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.button_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_STORAGE_FOR_IMPORT);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_STORAGE_FOR_IMPORT);
        }
    }

    private void requestStoragePermissionForExport() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mRoot, R.string.permission_storage_export_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.button_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_STORAGE_FOR_EXPORT);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_STORAGE_FOR_EXPORT);
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                AddScriptDialogFragment addScriptDialog = AddScriptDialogFragment.newInstance();
                addScriptDialog.setTargetFragment(this, REQUEST_ADD_SCRIPT_DIALOG);
                addScriptDialog.show(getFragmentManager(), addScriptDialog.getClass().getName());
                return true;
            case R.id.action_import:
                if (isExternalStorageWritable()) {
                    if (checkStoragePermissions()) {
                        requestStoragePermissionForImport();
                    } else {
                        doActionImport();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.error_external_storage_access, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_export:
                if (isExternalStorageWritable()) {
                    if (checkStoragePermissions()) {
                        requestStoragePermissionForExport();
                    } else {
                        doActionExport();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.error_external_storage_access, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_clear:
                ClearDialogFragment clearDialog = ClearDialogFragment.newInstance();
                clearDialog.setTargetFragment(this, REQUEST_CLEAR_DIALOG);
                clearDialog.show(getFragmentManager(), clearDialog.getClass().getName());
                return true;
            case R.id.action_about:
                AboutDialogFragment aboutDialog = AboutDialogFragment.newInstance();
                aboutDialog.show(getFragmentManager(), aboutDialog.getClass().getName());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ScriptItem selectedScriptItem = (ScriptItem) getListAdapter().getItem(position);
        RunScriptDialogFragment dialogFragment = RunScriptDialogFragment.newInstance(selectedScriptItem);
        dialogFragment.setTargetFragment(this, REQUEST_RUN_SCRIPT_DIALOG);
        dialogFragment.show(getFragmentManager(), dialogFragment.getClass().getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_SCRIPT_DIALOG:
                    ScriptItem newScriptItem = data.getParcelableExtra(AddScriptDialogFragment.TAG_ITEM);
                    addItem(newScriptItem);
                    break;
                case REQUEST_RUN_SCRIPT_DIALOG:
                    ScriptItem runScriptItem = data.getParcelableExtra(RunScriptDialogFragment.TAG_ITEM);
                    runItem(runScriptItem);
                    break;
                case REQUEST_EDIT_SCRIPT_DIALOG:
                    ScriptItem editScriptItem = data.getParcelableExtra(EditScriptDialogFragment.TAG_ITEM);
                    editItem(editScriptItem);
                    break;
                case REQUEST_COPY_SCRIPT_DIALOG:
                    ScriptItem copyScriptItem = data.getParcelableExtra(CopyScriptDialogFragment.TAG_ITEM);
                    copyItem(copyScriptItem);
                    break;
                case REQUEST_DELETE_SCRIPT_DIALOG:
                    long id = data.getLongExtra(DeleteScriptDialogFragment.TAG_DELETE_SCRIPT_DIALOG_ID, 0);
                    deleteItem(id);
                    break;
                case REQUEST_IMPORT_DIALOG:
                    String selectedItem = data.getStringExtra(ImportDialogFragment.TAG_IMPORT_DIALOG_SELECTED_ITEM);
                    importItems(selectedItem);
                    break;
                case REQUEST_EXPORT_DIALOG:
                    exportItems();
                    break;
                case REQUEST_CLEAR_DIALOG:
                    ProtectDialogFragment protectDialog = ProtectDialogFragment.newInstance();
                    protectDialog.setTargetFragment(this, REQUEST_PROTECT_DIALOG);
                    protectDialog.show(getFragmentManager(), protectDialog.getClass().getName());
                    break;
                case REQUEST_PROTECT_DIALOG:
                    clearItems();
                    break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo aMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ScriptItem selectedScriptItem = (ScriptItem) getListAdapter().getItem(aMenuInfo.position);
        switch (item.getItemId()) {
            case R.id.action_edit:
                EditScriptDialogFragment editScriptDialog = EditScriptDialogFragment.newInstance(selectedScriptItem);
                editScriptDialog.setTargetFragment(this, REQUEST_EDIT_SCRIPT_DIALOG);
                editScriptDialog.show(getFragmentManager(), editScriptDialog.getClass().getName());
                break;
            case R.id.action_copy:
                CopyScriptDialogFragment copyScriptDialog = CopyScriptDialogFragment.newInstance(selectedScriptItem);
                copyScriptDialog.setTargetFragment(this, REQUEST_COPY_SCRIPT_DIALOG);
                copyScriptDialog.show(getFragmentManager(), copyScriptDialog.getClass().getName());
                break;
            case R.id.action_delete:
                DeleteScriptDialogFragment deleteScriptDialog = DeleteScriptDialogFragment.newInstance(selectedScriptItem);
                deleteScriptDialog.setTargetFragment(this, REQUEST_DELETE_SCRIPT_DIALOG);
                deleteScriptDialog.show(getFragmentManager(), deleteScriptDialog.getClass().getName());
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void setItemListShown(boolean shown) {
        if (shown) {
            setMenuVisibility(true);
            mListProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            mListProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            setMenuVisibility(false);
            mListProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mListProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }

    private void updateList() {
        setItemListShown(false);
        Loader loader = getLoaderManager().getLoader(0);
        if (loader != null && loader.isStarted()) {
            loader.forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<LoadResult> onCreateLoader(int id, @Nullable Bundle args) {
        return new DatabaseLoader(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<LoadResult> loader, LoadResult data) {
        if (data.isSuccess()) {
            int position = mList.getFirstVisiblePosition();
            View v = mList.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - mList.getPaddingTop());
            mAdapter.clear();
            mAdapter.addAll(data.getScriptItems());
            mList.setSelectionFromTop(position, top);
        } else {
            mAdapter.clear();
            Toast.makeText(getActivity(), R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        setItemListShown(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<LoadResult> loader) {
    }

    public interface OnFragmentInteractionListener {
        void runProcess(String[] cmd);
        void runImport(String file);
        void runExport();
    }
}