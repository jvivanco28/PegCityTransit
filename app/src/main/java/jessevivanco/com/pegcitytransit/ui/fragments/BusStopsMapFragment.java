package jessevivanco.com.pegcitytransit.ui.fragments;

import android.util.Log;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class BusStopsMapFragment extends BaseFragment {

    public static BusStopsMapFragment newInstance() {
        Log.v("DEBUG", "Created new BSMF");

        return new BusStopsMapFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }
}
