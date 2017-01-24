package jessevivanco.com.pegcitytransit.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A dialog that displays a permission denied message.
 */
public class PermissionDeniedDialog extends DialogFragment {

    private static final String ARGUMENT_PERMISSION_DENIED_MESSAGE = "permission_denied_message";

    /**
     * Creates a new instance of this dialog and optionally finishes the calling Activity
     * when the 'Ok' button is clicked.
     */
    public static PermissionDeniedDialog newInstance(String permissionDeniedMessage) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_PERMISSION_DENIED_MESSAGE, permissionDeniedMessage);

        PermissionDeniedDialog dialog = new PermissionDeniedDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(getArguments().getString(ARGUMENT_PERMISSION_DENIED_MESSAGE))
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
