package neofusion.runmyscript.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import neofusion.runmyscript.R;
import neofusion.runmyscript.model.ScriptItem;

public class EditScriptDialogFragment extends ScriptDialogFragment {
    private static final String ARG_ITEM = "edit_item";

    public static final String TAG_ITEM = "edit_script_item";

    private ScriptItem mScriptItem;

    public EditScriptDialogFragment() {
    }

    public static EditScriptDialogFragment newInstance(ScriptItem scriptItem) {
        EditScriptDialogFragment fragment = new EditScriptDialogFragment();
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
        mPositiveButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScriptItem scriptItem = getAllFields();
                if (scriptItem != null) {
                    scriptItem.setId(mScriptItem.getId());
                    Intent intent = new Intent();
                    intent.putExtra(TAG_ITEM, scriptItem);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    mDialog.cancel();
                }
            }
        };
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        EditText name = (EditText) view.findViewById(R.id.editName);
        EditText path = (EditText) view.findViewById(R.id.editPath);
        RadioGroup radioGroupType = (RadioGroup) view.findViewById(R.id.radioGroupType);
        initRadioGroupType(view);
        CheckBox checkBoxSu = (CheckBox) view.findViewById(R.id.checkBoxSu);
        name.setText(mScriptItem.getName());
        path.setText(mScriptItem.getPath());
        switch (mScriptItem.getType()) {
            case ScriptItem.TYPE_SINGLE_COMMAND:
                radioGroupType.check(R.id.radioSingle);
                break;
            case ScriptItem.TYPE_PATH_TO_FILE:
                radioGroupType.check(R.id.radioPath);
                break;
            default:
                radioGroupType.clearCheck();
                break;
        }
        checkBoxSu.setChecked(mScriptItem.getSu());
        builder.setPositiveButton(R.string.button_save, null);
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}