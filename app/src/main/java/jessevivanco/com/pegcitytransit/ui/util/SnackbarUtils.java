package jessevivanco.com.pegcitytransit.ui.util;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import jessevivanco.com.pegcitytransit.R;

/**
 * Utility class for displaying Snackbar messages.
 */
abstract public class SnackbarUtils {

    private SnackbarUtils() {
    }

    public static void showError(@Nullable CharSequence error, View displayContext) {
        if (error == null) {
            Snackbar.make(displayContext, R.string.generic_error, Snackbar.LENGTH_LONG).show();
        } else {
            showSnackbar(error, displayContext);
        }
    }

    public static void showSnackbar(CharSequence message, View displayContext) {
        Snackbar.make(displayContext, message, Snackbar.LENGTH_LONG).show();
    }
}
