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
import neofusion.runmyscript.model.ScriptItem;

public class CopyScriptDialogFragment extends DialogFragment {
    private static final String ARG_ITEM = "copy_item";

    public static final String TAG_ITEM = "copy_script_item";

    private ScriptItem mScriptItem;

    public CopyScriptDialogFragment() {
    }

    public static CopyScriptDialogFragment newInstance(ScriptItem scriptItem) {
        CopyScriptDialogFragment fragment = new CopyScriptDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, scriptItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScriptItem = getArguments().getParcelable(ARG_ITEM);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_copy_message);
        builder.setMessage(mScriptItem.getName());
        builder.setPositiveButton(R.string.button_copy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(TAG_ITEM, mScriptItem);
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