package jessevivanco.com.pegcitytransit.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

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
    private static final String ARGUMENT_PERMISSION_ID = "permission_id";
    private static final String ARGUMENT_DIALOG_TITLE = "title";
    private static final String ARGUMENT_PERMISSION_RATIONALE = "rationale";

    private
    @Nullable
    Fragment fromFragment;

    /**
     * * Creates a new instance of a dialog displaying the rationale for the use of the location
     * permission.
     * <p>
     * The permission is requested after clicking 'ok'.
     *
     * @param requestCode         Id of the request that is used to request the permission. It is
     *                            returned to the
     *                            {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
     * @param fromFragment
     * @param permissionId
     * @param dialogTitle
     * @param permissionRationale
     * @return
     */
    public static RationaleDialog newInstance(int requestCode,
                                              @Nullable Fragment fromFragment,
                                              String permissionId,
                                              String dialogTitle,
                                              String permissionRationale) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
        arguments.putString(ARGUMENT_PERMISSION_ID, permissionId);
        arguments.putString(ARGUMENT_DIALOG_TITLE, dialogTitle);
        arguments.putString(ARGUMENT_PERMISSION_RATIONALE, permissionRationale);

        RationaleDialog dialog = new RationaleDialog();
        dialog.setArguments(arguments);
        dialog.setFromFragment(fromFragment);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
        final String permissionId = arguments.getString(ARGUMENT_PERMISSION_ID);

        return new AlertDialog.Builder(getActivity())
                .setTitle(arguments.getString(ARGUMENT_DIALOG_TITLE))
                .setMessage(arguments.getString(ARGUMENT_PERMISSION_RATIONALE))

                // After click on Ok, request the permission.
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                            // If supplied a fragment, then invoke the fragment's requestPermissions method.
                            // This way we actually get the callback in the fragment and not activity.
                            if (fromFragment != null) {
                                fromFragment.requestPermissions(new String[]{permissionId},
                                        requestCode);
                            } else {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{permissionId},
                                        requestCode);
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    public void setFromFragment(@Nullable Fragment fromFragment) {
        this.fromFragment = fromFragment;
    }
}