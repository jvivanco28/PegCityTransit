package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.TransmitMapPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class MainActivity extends BaseActivity implements TransmitMapPresenter.ViewContract,
        TransitMapFragment.TransitMapCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATE_KEY_SELECTED_TAB = "selected_tab";
    private static final String STATE_KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state";

    @BindView(R.id.map_fragment_container)
    FrameLayout mapFragmentContainer;

    @BindView(R.id.my_location_button)
    FloatingActionButton myLocationButton;

    @BindView(R.id.refresh_bus_stops)
    FloatingActionButton refreshBusStopsButton;

    @BindView(R.id.tab_navigation)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.bottom_sheet_container)
    ViewGroup bottomSheetContainer;
    BottomSheetBehavior bottomSheetBehavior;

    private int mapSearchRadius;

    private TransitMapFragment transitMapFragment;
    private TransmitMapPresenter transmitMapPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        // TODO check service advisories on startup
        mapSearchRadius = getResources().getInteger(R.integer.default_map_search_radius);
        setupMap(savedInstanceState);
        setupBottomNav(savedInstanceState);
        setupBottomSheet(savedInstanceState);
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
        transmitMapPresenter = new TransmitMapPresenter(getInjector(), this);
    }

    private void setupBottomNav(@Nullable Bundle savedInstanceState) {
        // Select the first tab on initial launch.
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.map);
        } else {
            bottomNavigation.setSelectedItemId(savedInstanceState.getInt(STATE_KEY_SELECTED_TAB));
        }

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {

            transmitMapPresenter.killSubscription();

            switch (item.getItemId()) {
                case R.id.map:
                    // TODO
                    Log.v("DEBUG", "tapped map");

                    break;
                case R.id.my_stops:
                    // TODO
                    Log.v("DEBUG", "tapped my stops");
                    break;
                case R.id.routes:
                    // TODO
                    Log.v("DEBUG", "tapped routes");

                    break;
            }
            return true;
        });

        bottomNavigation.setOnNavigationItemReselectedListener(item -> {
            // Not doing anything here. Setting a nav item reselected listener will prevent
            // reselects from triggering the above listener. We only want to kill a previous REST
            // call when switching tabs. Reselects shouldn't kill subscriptions.
        });
    }

    private void setupBottomSheet(@Nullable Bundle savedInstanceState) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer);
        bottomSheetBehavior.setState(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_HIDDEN) :
                BottomSheetBehavior.STATE_HIDDEN);

        // TODO TEST
        mapFragmentContainer.post(() -> {

            Log.v("DEBUG", "height " + mapFragmentContainer.getHeight() + " -- adf " + (mapFragmentContainer.getHeight() / 2));
            bottomSheetBehavior.setPeekHeight((mapFragmentContainer.getHeight() / 2) + getResources().getDimensionPixelSize(R.dimen.action_bar_height));
        });
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
    public void showBusStopSchedule(BusStopViewModel busStopViewModel) {
        // Slightly open the bottom sheet so that we can display the bus stop's schedule.
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void loadBusRoutesForStop(BusStopViewModel busStopViewModel) {
        transmitMapPresenter.loadBusRoutesForStop(busStopViewModel);
    }

    @Override
    public void onBusStopInfoWindowClicked(BusStopViewModel busStopViewModel) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(mapFragmentContainer, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_KEY_SELECTED_TAB, bottomNavigation.getSelectedItemId());
    }

    @Override
    public void clearMarkersAndShowSearchRadius(Double latitude, Double longitude, Integer searchRadius) {
        transitMapFragment.clearMarkers();
        transitMapFragment.drawSearchRadius(latitude, longitude, mapSearchRadius);
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

        if (transitMapFragment.isMapReady()) {
            LatLng cameraPosition = transitMapFragment.getCameraPosition();
            transmitMapPresenter.loadBusStopsAroundCoordinates(cameraPosition.latitude, cameraPosition.longitude, mapSearchRadius);
        } else {
            Log.w(TAG, "Map not ready!");
        }
    }
}
