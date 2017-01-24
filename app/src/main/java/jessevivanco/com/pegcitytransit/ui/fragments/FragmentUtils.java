package jessevivanco.com.pegcitytransit.ui.fragments;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class FragmentUtils {

    private static final String LOG_TAG = FragmentUtils.class.getSimpleName();

    /**
     * Convenience method for displaying a dialog fragment. If the a dialog already exists with the given
     * <code>DIALOG_FRAGMENT_TAG</code>,
     * then that dialog is removed before displaying <code>dialogToShow</code>.
     *
     * @param fromActivity
     * @param dialogToShow
     * @param DIALOG_FRAGMENT_TAG
     */
    public static void showFragment(AppCompatActivity fromActivity,
                                    DialogFragment dialogToShow,
                                    final String DIALOG_FRAGMENT_TAG) {

        if (fromActivity != null && !fromActivity.isFinishing()) {

            FragmentManager fragmentManager = fromActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment previousDialog = fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
            if (previousDialog != null) {
                fragmentTransaction.remove(previousDialog);
            }
            fragmentTransaction.commitAllowingStateLoss();

            dialogToShow.show(fragmentManager, DIALOG_FRAGMENT_TAG);
        } else {
            Log.e(LOG_TAG, "Couldn\'t start DialogFragment.");
        }
    }
}
