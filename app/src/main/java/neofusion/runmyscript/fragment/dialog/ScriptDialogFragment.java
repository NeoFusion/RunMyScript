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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import neofusion.runmyscript.R;
import neofusion.runmyscript.model.ScriptItem;

public class ScriptDialogFragment extends DialogFragment {
    private static final String KEY_NAME_ERROR_STATE = "name_error";
    private static final String KEY_PATH_ERROR_STATE = "path_error";

    private static final int ERROR_STATE_NONE = 0;
    private static final int ERROR_STATE_EMPTY = 1;
    private static final int ERROR_STATE_MAX = 2;

    AlertDialog mDialog;
    View.OnClickListener mPositiveButtonOnClickListener;

    private int mNameErrorState;
    private int mPathErrorState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNameErrorState = savedInstanceState.getInt(KEY_NAME_ERROR_STATE);
            mPathErrorState = savedInstanceState.getInt(KEY_PATH_ERROR_STATE);
        } else {
            mNameErrorState = ERROR_STATE_NONE;
            mPathErrorState = ERROR_STATE_NONE;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mDialog = (AlertDialog) getDialog();
        if (mDialog != null) {
            Button positiveButton = mDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(mPositiveButtonOnClickListener);
            if (mNameErrorState != ERROR_STATE_NONE) {
                TextInputLayout nameLayout = mDialog.findViewById(R.id.layoutName);
                switch (mNameErrorState) {
                    case ERROR_STATE_EMPTY:
                        nameLayout.setError(getString(R.string.empty_field_message));
                        break;
                    case ERROR_STATE_MAX:
                        nameLayout.setError(String.format(getString(R.string.max_message), getResources().getInteger(R.integer.name_max_length)));
                        break;
                    default:
                        break;
                }
            }
            if (mPathErrorState != ERROR_STATE_NONE) {
                TextInputLayout pathLayout = mDialog.findViewById(R.id.layoutPath);
                switch (mPathErrorState) {
                    case ERROR_STATE_EMPTY:
                        pathLayout.setError(getString(R.string.empty_field_message));
                        break;
                    case ERROR_STATE_MAX:
                        pathLayout.setError(String.format(getString(R.string.max_message), getResources().getInteger(R.integer.path_max_length)));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_NAME_ERROR_STATE, mNameErrorState);
        outState.putInt(KEY_PATH_ERROR_STATE, mPathErrorState);
    }

    void initRadioGroupType(final View view) {
        RadioGroup radioGroupType = view.findViewById(R.id.radioGroupType);
        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                TextInputLayout pathLayout = view.findViewById(R.id.layoutPath);
                switch (checkedId) {
                    case R.id.radioSingle:
                        pathLayout.setHint(getString(R.string.dialog_edit_command));
                        break;
                    case R.id.radioPath:
                        pathLayout.setHint(getString(R.string.dialog_edit_path));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    ScriptItem getAllFields() {
        boolean errors = false;
        TextInputLayout nameLayout = mDialog.findViewById(R.id.layoutName);
        EditText nameText = mDialog.findViewById(R.id.editName);
        TextInputLayout pathLayout = mDialog.findViewById(R.id.layoutPath);
        EditText pathText = mDialog.findViewById(R.id.editPath);
        RadioGroup radioGroupType = mDialog.findViewById(R.id.radioGroupType);
        int type = 0;
        if (radioGroupType.getCheckedRadioButtonId() != -1) {
            switch (radioGroupType.getCheckedRadioButtonId()) {
                case R.id.radioSingle:
                    type = ScriptItem.TYPE_SINGLE_COMMAND;
                    break;
                case R.id.radioPath:
                    type = ScriptItem.TYPE_PATH_TO_FILE;
                    break;
            }
        }
        CheckBox checkBoxSu = mDialog.findViewById(R.id.checkBoxSu);
        String name = nameText.getText().toString();
        String path = pathText.getText().toString();
        int nameMaxLength = getResources().getInteger(R.integer.name_max_length);
        int pathMaxLength = getResources().getInteger(R.integer.path_max_length);
        if (name.length() < 1 || name.length() > nameMaxLength) {
            errors = true;
            if (name.length() < 1) {
                nameLayout.setError(getString(R.string.empty_field_message));
                mNameErrorState = ERROR_STATE_EMPTY;
            } else {
                nameLayout.setError(String.format(getString(R.string.max_message), nameMaxLength));
                mNameErrorState = ERROR_STATE_MAX;
            }
        } else {
            nameLayout.setError("");
            mNameErrorState = ERROR_STATE_NONE;
        }
        if (path.length() < 1 || path.length() > pathMaxLength) {
            errors = true;
            if (path.length() < 1) {
                pathLayout.setError(getString(R.string.empty_field_message));
                mPathErrorState = ERROR_STATE_EMPTY;
            } else {
                pathLayout.setError(String.format(getString(R.string.max_message), pathMaxLength));
                mPathErrorState = ERROR_STATE_MAX;
            }
        } else {
            pathLayout.setError("");
            mPathErrorState = ERROR_STATE_NONE;
        }
        if (type != ScriptItem.TYPE_SINGLE_COMMAND && type != ScriptItem.TYPE_PATH_TO_FILE) {
            errors = true;
        }
        if (errors) {
            return null;
        } else {
            return new ScriptItem(name, path, type, checkBoxSu.isChecked());
        }
    }
}