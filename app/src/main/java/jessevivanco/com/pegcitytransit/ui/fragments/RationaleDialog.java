package jessevivanco.com.pegcitytransit.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;

/**
 * A dialog that explains the use of the location permission and requests the necessary
 * permission.
 * <p>
 * The activity should implement
 * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
 * to handle permit or denial of this permission request.
 */
public class RationaleDialog extends DialogFragment {

    private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";
    private static final String ARGUMENT_DIALOG_TITLE = "title";
    private static final String ARGUMENT_PERMISSION_RATIONALE = "rationale";

    /**
     * * Creates a new instance of a dialog displaying the rationale for the use of the location
     * permission.
     * <p>
     * The permission is requested after clicking 'ok'.
     *
     * @param requestCode         Id of the request that is used to request the permission. It is
     *                            returned to the
     *                            {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
     * @param dialogTitle
     * @param permissionRationale
     * @return
     */
    public static RationaleDialog newInstance(int requestCode,
                                              String dialogTitle,
                                              String permissionRationale) {

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
        arguments.putString(ARGUMENT_DIALOG_TITLE, dialogTitle);
        arguments.putString(ARGUMENT_PERMISSION_RATIONALE, permissionRationale);
        RationaleDialog dialog = new RationaleDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(arguments.getString(ARGUMENT_DIALOG_TITLE))
                .setMessage(arguments.getString(ARGUMENT_PERMISSION_RATIONALE))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            // After click on Ok, request the permission.
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    requestCode);
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}