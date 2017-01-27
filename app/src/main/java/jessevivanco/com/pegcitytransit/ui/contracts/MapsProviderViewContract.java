package jessevivanco.com.pegcitytransit.ui.contracts;

import android.support.v4.app.ActivityCompat;

public interface MapsProviderViewContract extends ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Sets the static map cover image with the image URL.
     *
     * @param imageUrl
     */
    void setStaticMapCoverImage(String imageUrl);

    /**
     * Requests a device permission. If permission has been rejected once before, then asks the user
     * displaying rationale.
     *
     * @param intentRequestCode
     * @param permission
     * @param dialogTitle
     * @param dialogRationale
     */
    void onRequestPermission(int intentRequestCode, String permission, String dialogTitle, String dialogRationale);

    /**
     * Resets all data and starts loading from the beginning.
     */
    void refreshList();
}
