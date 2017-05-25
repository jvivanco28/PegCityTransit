package jessevivanco.com.pegcitytransit.data.util;

import android.content.Context;
import android.os.Build;

import jessevivanco.com.pegcitytransit.BuildConfig;

final public class DeviceUtil {

    private DeviceUtil() {
    }

    /**
     * Returns basic debugging info about the app.
     * NOTE: This was taken from TippingCanoe's JunkDrawer library.
     */
    public static String getDebugInfo(Context context) {

        return "Version: " + BuildConfig.VERSION_NAME +
                "\nDevice: " + Build.MANUFACTURER + " " + Build.MODEL +
                "\nSDK Version: " + Build.VERSION.SDK_INT +
                "\nAndroid Version: " + Build.VERSION.RELEASE +
                "\nres: @" + context.getResources().getDisplayMetrics().density + "x";
    }
}
