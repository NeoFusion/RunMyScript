package neofusion.runmyscript.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import neofusion.runmyscript.R;

public class ProtectDialogFragment extends DialogFragment {
    private static final String KEY_NUMBER = "number";
    private static final String KEY_ERROR_STATE = "error";

    private static final int ERROR_STATE_NONE = 0;
    private static final int ERROR_STATE_EMPTY = 1;
    private static final int ERROR_STATE_MISMATCH = 2;

    private String mNumber;
    private int mErrorState;

    public ProtectDialogFragment() {
    }

    public static ProtectDialogFragment newInstance() {
        return new ProtectDialogFragment();
    }

    private String getRandomNumber() {
        Random random = new Random();
        return String.valueOf(random.nextInt(899999999) + 100000000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String number = savedInstanceState.getString(KEY_NUMBER);
            if (number != null) {
                mNumber = number;
            } else {
                mNumber = getRandomNumber();
            }
            mErrorState = savedInstanceState.getInt(KEY_ERROR_STATE);
        } else {
            mNumber = getRandomNumber();
            mErrorState = ERROR_STATE_NONE;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextInputLayout textInputLayout = (TextInputLayout) dialog.findViewById(R.id.textInputLayout);
                    EditText editText = (EditText) dialog.findViewById(R.id.editText);
                    String inputNumber = editText.getText().toString();
                    if (inputNumber.isEmpty()) {
                        textInputLayout.setError(getString(R.string.empty_field_message));
                        mErrorState = ERROR_STATE_EMPTY;
                        return;
                    }
                    if (inputNumber.equals(mNumber)) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                        dialog.cancel();
                    } else {
                        textInputLayout.setError(getString(R.string.code_mismatch_message));
                        mErrorState = ERROR_STATE_MISMATCH;
                    }
                }
            });
            if (mErrorState != ERROR_STATE_NONE) {
                TextInputLayout textInputLayout = (TextInputLayout) dialog.findViewById(R.id.textInputLayout);
                switch (mErrorState) {
                    case ERROR_STATE_EMPTY:
                        textInputLayout.setError(getString(R.string.empty_field_message));
                        break;
                    case ERROR_STATE_MISMATCH:
                        textInputLayout.setError(getString(R.string.code_mismatch_message));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NUMBER, mNumber);
        outState.putInt(KEY_ERROR_STATE, mErrorState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.protect_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        builder.setTitle(String.format(
                getResources().getString(R.string.dialog_clear_confirm_message),
                mNumber
        ));
        builder.setPositiveButton(R.string.button_ok, null);
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}