package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.AppRouter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.item_decorations.HorizontalListItemDecoration;

public class BusStopsMapFragment extends BaseFragment implements TransitMapFragment.OnMapReadyListener {

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_stop_schedule_recycler_view)
    RecyclerView busStopsRecyclerView;

    @Inject
    AppRouter appRouter;

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
//            transitMapFragment.setMapReadyListener(this);
        }

        setupRecyclerView();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }

    @Override
    public void onMapReady() {
        // TODO need to handle orientation changes
        transitMapFragment.loadBusStopsAroundCameraCoordinates();
    }

    private void setupRecyclerView() {
        busStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        busStopsRecyclerView.addItemDecoration(new HorizontalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

        if (transitMapFragment.isMapReady()) {
            transitMapFragment.loadBusStopsAroundCameraCoordinates();
        } else {
            // TODO show msg
        }
    }
}
