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

package neofusion.runmyscript.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import neofusion.runmyscript.R;
import neofusion.runmyscript.model.ScriptItem;

public class EditScriptDialogFragment extends ScriptDialogFragment {
    private static final String ARG_ITEM = "edit_item";

    public static final String TAG_ITEM = "edit_script_item";

    private ScriptItem mScriptItem;

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
        EditText name = view.findViewById(R.id.editName);
        EditText path = view.findViewById(R.id.editPath);
        RadioGroup radioGroupType = view.findViewById(R.id.radioGroupType);
        initRadioGroupType(view);
        CheckBox checkBoxSu = view.findViewById(R.id.checkBoxSu);
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