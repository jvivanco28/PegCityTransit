package jessevivanco.com.pegcitytransit.ui.util;

import android.content.pm.PackageManager;

/**
 * <b>NOTE: Some of this was taken from https://github.com/googlemaps/android-samples</b>
 * <p>
 * Utility class for access to runtime permissions.
 */
final public class PermissionUtils {

    private PermissionUtils() {
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
