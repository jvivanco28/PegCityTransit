package jessevivanco.com.pegcitytransit.ui.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

/**
 * These helper methods are taken from TippingCanoe's junkdrawer library.
 */
final public class IntentUtil {

    private IntentUtil() {
    }

    public static Intent getSendEmailIntent(String mailTo, String subject, CharSequence body) {
        Intent intent = new Intent(ACTION_SENDTO);
        intent.setType("message/rfc822");

        if (mailTo == null) {
            mailTo = "";
        }
        intent.setData(Uri.parse("mailto:" + mailTo));
        intent.putExtra(EXTRA_SUBJECT, subject);
        intent.putExtra(EXTRA_TEXT, body);

        return intent;
    }

    // TODO we'll need to verify this works once the app is on the play store.
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
