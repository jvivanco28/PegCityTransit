package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.AppRouter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class BusStopsMapFragment extends BaseFragment implements TransitMapFragment.OnMapReadyListener {

    @Inject
    AppRouter appRouter;

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    private TransitMapFragment transitMapFragment;

    public static BusStopsMapFragment newInstance() {
        return new BusStopsMapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInjector().injectInto(this);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            transitMapFragment = TransitMapFragment.newInstance(this);
            getChildFragmentManager().beginTransaction().add(R.id.map_fragment_container, transitMapFragment).commit();
        } else {
            transitMapFragment = (TransitMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment_container);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }

    @Override
    public void onMapReady() {
        // TODO need to handle orientation changes
        transitMapFragment.loadBusStopsAroundCameraCoordinates(getResources().getInteger(R.integer.default_map_search_radius));
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

        if (transitMapFragment.isMapReady()) {
            transitMapFragment.loadBusStopsAroundCameraCoordinates(getResources().getInteger(R.integer.default_map_search_radius));
        } else {
            // TODO show msg
        }
    }
}
