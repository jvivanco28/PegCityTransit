package jessevivanco.com.pegcitytransit.ui.util;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;

import jessevivanco.com.pegcitytransit.ui.fragments.FragmentUtils;
import jessevivanco.com.pegcitytransit.ui.fragments.dialog.RationaleDialog;

/**
 * <b>NOTE: Some of this was taken from https://github.com/googlemaps/android-samples</b>
 * <p>
 * Utility class for access to runtime permissions.
 */
public abstract class PermissionUtils {

    /**
     * Requests <code>permission</code>. If a rationale with an additional explanation should be shown to the user,
     * displays a dialog that triggers the request.
     */
    public static void requestPermission(Fragment fromFragment,
                                         int requestId,
                                         String permissionId,
                                         String permissionDialogTitle,
                                         String permissionRationale,
                                         String dialogFragmentTag) {

        // If the user has already denied the request, we'll show a different dialog with permission rationale.
        if (fromFragment.shouldShowRequestPermissionRationale(permissionId)) {

            FragmentUtils.showFragment(fromFragment, RationaleDialog.newInstance(requestId,
                    fromFragment,
                    permissionId,
                    permissionDialogTitle,
                    permissionRationale),
                    dialogFragmentTag);
        } else {
            // Location permission has not been granted yet, request it.
            fromFragment.requestPermissions(new String[]{permissionId}, requestId);
        }
    }

    /**
     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
     * permission from a runtime permissions request.
     *
     * @see android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
     */
    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }
}
