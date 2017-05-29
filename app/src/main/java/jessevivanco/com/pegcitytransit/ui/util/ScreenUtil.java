package jessevivanco.com.pegcitytransit.ui.util;

import android.content.Context;
import android.os.Build;

final public class ScreenUtil {

    private ScreenUtil() {
    }

    /**
     * @return the height of the status bar <b>if running LOLLIPOP or later</b>. Returns {@code 0}
     * for any OS version less than LOLLIPOP. NOTE: this was taken from
     * <a href="http://stackoverflow.com/questions/3407256/height-of-status-bar-in-android">this</a>
     * StackOverflow post.
     */
    public static int getStatusBarHeightIfNeeded(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
}
