package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.AppRouter;
import jessevivanco.com.pegcitytransit.ui.adapters.ScheduledStopAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.item_decorations.HorizontalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopsMapFragment extends BaseFragment implements TransitMapFragment.OnMapReadyListener, BusStopSchedulePresenter.ViewContract {

    @Inject
    AppRouter appRouter;

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_stop_schedule_recycler_view)
    RecyclerView busStopsRecyclerView;

    private TransitMapFragment transitMapFragment;

    // TODO REMOVE THIS!
    private BusStopSchedulePresenter schedulePresenter;
    private ScheduledStopAdapter scheduleAdapter;

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

        setupAdapter();
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

    protected void setupAdapter() {
        schedulePresenter = new BusStopSchedulePresenter(getInjector(), this);
        scheduleAdapter = new ScheduledStopAdapter(schedulePresenter);
    }

    private void setupRecyclerView() {
        busStopsRecyclerView.setVisibility(View.GONE);

        busStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        busStopsRecyclerView.addItemDecoration(new HorizontalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        busStopsRecyclerView.setAdapter(scheduleAdapter);
    }

    @Override
    public void showScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        Log.v("DEBUG", "GOT RESPONSE! " + scheduledStops.size());
        scheduleAdapter.setScheduledStops(scheduledStops);

        busStopsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorLoadingScheduleMessage(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

        busStopsRecyclerView.setVisibility(View.GONE);

        // TODO TEST
        schedulePresenter.loadScheduleForBusStop(10643L);

        if (transitMapFragment.isMapReady()) {
            transitMapFragment.loadBusStopsAroundCameraCoordinates();
        } else {
            // TODO show msg
        }
    }
}
