package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class MainActivity extends BaseActivity implements BusStopsPresenter.ViewContract,
        BusRoutesPresenter.ViewContract,
        TransitMapFragment.TransitMapCallbacks,
        BusRouteCellViewHolder.OnBusRouteCellClickedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATE_KEY_SELECTED_TAB = "selected_tab";

    @BindView(R.id.map_fragment_container)
    FrameLayout mapFragmentContainer;

    @BindView(R.id.my_location_button)
    FloatingActionButton myLocationButton;

    @BindView(R.id.refresh_bus_stops)
    FloatingActionButton refreshBusStopsButton;

    @BindView(R.id.tab_navigation)
    BottomNavigationView bottomNavigation;

    private int mapSearchRadius;

    private TransitMapFragment transitMapFragment;

    private BusStopsPresenter stopsPresenter;
    private BusRoutesPresenter routesPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        // TODO check service advisories on startup
        mapSearchRadius = getResources().getInteger(R.integer.default_map_search_radius);
        setupMap(savedInstanceState);
        setupPresenters();
        setupBottomNav(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    private void setupMap(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            transitMapFragment = new TransitMapFragment();
            transitMapFragment.setTransitMapCallbacks(this);
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment_container, transitMapFragment).commit();
        } else {
//            initialLoadFinished = savedInstanceState.getBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, false);
//            googleApiClientInitialized = savedInstanceState.getBoolean(STATE_KEY_GOOGLE_API_CLIENT_INITIALIZED, false);

            transitMapFragment = (TransitMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_container);
            transitMapFragment.setTransitMapCallbacks(this);
        }
    }

    private void setupBottomNav(@Nullable Bundle savedInstanceState) {
        // Select the first tab on initial launch.
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.map);
        } else {
            bottomNavigation.setSelectedItemId(savedInstanceState.getInt(STATE_KEY_SELECTED_TAB));
        }

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.map:
                    // TODO

                    break;
                case R.id.my_stops:
                    // TODO

                    break;
                case R.id.routes:
                    // TODO

                    break;
            }
            return false;

            // TODO peek up bottom sheet
            // TODO kill previous subscriptions when switching tabs.
        });
    }

    private void setupPresenters() {
        stopsPresenter = new BusStopsPresenter(getInjector(), this);
        routesPresenter = new BusRoutesPresenter(getInjector(), this);
    }

    /**
     * Bus routes for the provided <code>busStop</code> have been loaded. Find the bus stop in
     * our Marker-BusStop HashMap, plug the routes into that BusStop, then refresh the info window
     * for the bus stop marker. This will end up displaying the bus routes in the info window.
     */
    @Override
    public void showAllBusRoutes(List<BusRouteViewModel> busRoutes) {
//        busRoutesAdapter.setBusRoutes(busRoutes);
    }

    @Override
    public void showBusRoutesForStop(BusStopViewModel busStop) {
        // The BusStopViewModel should now contain the list of bus routes that stop at that bus stop.
        transitMapFragment.refreshBusStopInfoWindow(busStop);
    }

    @Override
    public void showBusStops(List<BusStopViewModel> busStops) {
        transitMapFragment.showBusStops(busStops, true);
    }

    @Override
    public void onMapReady() {
        // TODO
    }

    @Override
    public void onBusStopMarkerClicked(BusStopViewModel busStopViewModel) {

        // TODO
        // Allow the bottom sheet to peek up slightly so we can see the bus stop schedule.

        // If the bus stop doesn't have the routes loaded yet, then fetch them and refresh the info window.
        if (busStopViewModel.getRoutes() == null || busStopViewModel.getRoutes().size() == 0) {
            routesPresenter.loadBusRoutesForStop(busStopViewModel);
        }
    }

    @Override
    public void onBusStopInfoWindowClicked(BusStopViewModel busStopViewModel) {
        // TODO show bottom sheet with schedule
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(mapFragmentContainer, msg, Snackbar.LENGTH_LONG);
    }

    @Override
    public void onBusRouteCellClicked(BusRouteViewModel busRoute) {
        // TODO load bus stops for bus route and display them in the map.
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_KEY_SELECTED_TAB, bottomNavigation.getSelectedItemId());
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

        if (transitMapFragment.isMapReady()) {
            LatLng cameraPosition = transitMapFragment.getCameraPosition();
            stopsPresenter.loadBusStopsAroundCoordinates(cameraPosition.latitude, cameraPosition.longitude, mapSearchRadius);
            transitMapFragment.drawSearchRadius(cameraPosition.latitude, cameraPosition.longitude, mapSearchRadius);
        } else {
            Log.w(TAG, "Map not ready!");
        }
    }
}
