package neofusion.runmyscript.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import neofusion.runmyscript.R;

public class ImportDialogFragment extends DialogFragment{
    private static final String ARG_FILE_ITEMS = "import_file_items";

    private static final String KEY_SELECTED_ITEM = "selected_item";

    public static final String TAG_IMPORT_DIALOG_SELECTED_ITEM = "import_selected_item";

    private String[] mFileItems;
    private String mSelectedItem = "";

    public ImportDialogFragment() {
    }

    public static ImportDialogFragment newInstance(String[] fileItems) {
        ImportDialogFragment fragment = new ImportDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_FILE_ITEMS, fileItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String selectedItem = savedInstanceState.getString(KEY_SELECTED_ITEM);
            if (selectedItem != null) {
                mSelectedItem = selectedItem;
            }
        }
        if (getArguments() != null) {
            mFileItems = getArguments().getStringArray(ARG_FILE_ITEMS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SELECTED_ITEM, mSelectedItem);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_choose_file_message);
        builder.setSingleChoiceItems(mFileItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectedItem = mFileItems[which];
            }
        });
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(TAG_IMPORT_DIALOG_SELECTED_ITEM, mSelectedItem);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}