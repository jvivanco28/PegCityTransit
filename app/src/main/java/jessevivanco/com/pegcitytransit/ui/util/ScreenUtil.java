package jessevivanco.com.pegcitytransit.ui.util;

import android.content.Context;

final public class ScreenUtil {

    private ScreenUtil() {
    }

    /**
     * @return the height of the status bar. NOTE: this was taken from
     * <a href="http://stackoverflow.com/questions/3407256/height-of-status-bar-in-android">this</a>
     * StackOverflow post.
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
