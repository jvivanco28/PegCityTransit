package jessevivanco.com.pegcitytransit.ui.fragments;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class MyStopsListFragment extends BaseFragment {

    public static MyStopsListFragment newInstance() {
        return new MyStopsListFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_my_stops;
    }
}
