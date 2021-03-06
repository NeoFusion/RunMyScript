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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import neofusion.runmyscript.R;
import neofusion.runmyscript.model.ScriptItem;

public class RunScriptDialogFragment extends DialogFragment {
    private static final String ARG_ITEM = "run_item";

    public static final String TAG_ITEM = "run_script_item";

    private ScriptItem mScriptItem;

    public static RunScriptDialogFragment newInstance(ScriptItem scriptItem) {
        RunScriptDialogFragment fragment = new RunScriptDialogFragment();
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
        builder.setTitle(R.string.dialog_run_message);
        builder.setMessage(mScriptItem.getName());
        builder.setPositiveButton(R.string.button_run, new DialogInterface.OnClickListener() {
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