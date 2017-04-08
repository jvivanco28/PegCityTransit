package jessevivanco.com.pegcitytransit.ui.fragments;

import android.util.Log;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class BusRoutesFragment extends BaseFragment {

    public static BusRoutesFragment newInstance() {
        Log.v("DEBUG", "Created new BRF");
        return new BusRoutesFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_routes;
    }
}
