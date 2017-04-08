package jessevivanco.com.pegcitytransit.ui.fragments;

import android.util.Log;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class BusStopTimetablesFragment extends BaseFragment {

    public static BusStopTimetablesFragment newInstance() {
        Log.v("DEBUG", "Created new BSTF");

        return new BusStopTimetablesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_timetables;
    }
}
