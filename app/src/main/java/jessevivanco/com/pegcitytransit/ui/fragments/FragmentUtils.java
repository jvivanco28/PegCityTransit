package jessevivanco.com.pegcitytransit.ui.fragments;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class FragmentUtils {

    /**
     * Convenience method for displaying a dialog fragment. If the a dialog already exists with the given
     * <code>DIALOG_FRAGMENT_TAG</code>, then that dialog is removed before displaying <code>dialogToShow</code>.
     *
     * @param fragmentManager
     * @param dialogToShow
     * @param DIALOG_FRAGMENT_TAG
     */
    public static void showFragment(FragmentManager fragmentManager,
                                    DialogFragment dialogToShow,
                                    final String DIALOG_FRAGMENT_TAG) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment previousDialog = fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (previousDialog != null) {
            fragmentTransaction.remove(previousDialog);
        }
        fragmentTransaction.commitAllowingStateLoss();

        dialogToShow.show(fragmentManager, DIALOG_FRAGMENT_TAG);
    }
}
