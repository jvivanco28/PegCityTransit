package jessevivanco.com.pegcitytransit.ui.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import static android.content.Intent.ACTION_VIEW;

final public class IntentUtil {

    private IntentUtil() {
    }

    /**
     * TODO we'll need to verify this works once the app is on the play store.
     * Ripped this from TippingCanoe's junkdrawer library.
     */
    public static Intent getAppIntent(Context context) {
        String packageName = context.getPackageName();

        String uri = "market://details?id=" + packageName;
        Intent intent = new Intent(ACTION_VIEW, Uri.parse(uri));

        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
            if (resolveInfos == null || resolveInfos.size() == 0) {
                uri = "http://play.google.com/store/apps/details?id=" + packageName;
                intent = new Intent(ACTION_VIEW, Uri.parse(uri));
            }
        }
        return intent;
    }
}
