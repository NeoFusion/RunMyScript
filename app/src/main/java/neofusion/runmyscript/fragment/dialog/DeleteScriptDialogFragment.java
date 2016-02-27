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

public class DeleteScriptDialogFragment extends DialogFragment {
    private static final String ARG_ID = "delete_id";
    private static final String ARG_NAME = "delete_name";

    public static final String TAG_DELETE_SCRIPT_DIALOG_ID = "delete_script_id";

    private long mId;
    private String mName;

    public DeleteScriptDialogFragment() {
    }

    public static DeleteScriptDialogFragment newInstance(ScriptItem scriptItem) {
        DeleteScriptDialogFragment fragment = new DeleteScriptDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, scriptItem.getId());
        args.putString(ARG_NAME, scriptItem.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_ID);
            mName = getArguments().getString(ARG_NAME);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_delete_message);
        builder.setMessage(mName);
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra(TAG_DELETE_SCRIPT_DIALOG_ID, mId);
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