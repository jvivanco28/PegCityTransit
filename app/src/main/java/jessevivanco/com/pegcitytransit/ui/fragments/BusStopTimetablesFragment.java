package jessevivanco.com.pegcitytransit.ui.fragments;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class BusStopTimetablesFragment extends BaseFragment {

    public static BusStopTimetablesFragment newInstance() {
        return new BusStopTimetablesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_timetables;
    }
}
