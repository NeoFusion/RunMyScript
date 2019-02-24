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
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import neofusion.runmyscript.R;
import neofusion.runmyscript.model.ScriptItem;

public class AddScriptDialogFragment extends ScriptDialogFragment {
    public static final String TAG_ITEM = "add_script_item";

    public AddScriptDialogFragment() {
    }

    public static AddScriptDialogFragment newInstance() {
        return new AddScriptDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPositiveButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScriptItem scriptItem = getAllFields();
                if (scriptItem != null) {
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
        RadioGroup radioGroupType = (RadioGroup) view.findViewById(R.id.radioGroupType);
        initRadioGroupType(view);
        radioGroupType.check(R.id.radioSingle);
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