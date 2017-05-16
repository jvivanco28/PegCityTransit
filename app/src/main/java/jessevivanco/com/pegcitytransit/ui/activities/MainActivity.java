package jessevivanco.com.pegcitytransit.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.adapters.ScheduledStopAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.FragmentUtils;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.dialog.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.TransmitMapPresenter;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class MainActivity extends BaseActivity implements TransmitMapPresenter.ViewContract,
        TransitMapFragment.TransitMapCallbacks,
        BusStopSchedulePresenter.ViewContract,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String STATE_KEY_INITIAL_LOAD_FINISHED = "initial_load_finished";
    private static final String STATE_KEY_SELECTED_TAB = "selected_tab";
    private static final String STATE_KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state";

    private static final String PERMISSION_DIALOG_TAG = "dialog";

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

    @BindView(R.id.schedule_recycler_view)
    RecyclerView stopScheduleRecyclerView;
    ScheduledStopAdapter stopScheduleAdapter;
    BusStopSchedulePresenter stopSchedulePresenter;

    private GoogleApiClient googleApiClient;
    private int mapSearchRadius;

    private TransitMapFragment transitMapFragment;
    private TransmitMapPresenter transmitMapPresenter;

    private boolean initialLoadFinished;
    private boolean googleApiClientInitialized;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        // TODO check service advisories on startup
        mapSearchRadius = getResources().getInteger(R.integer.default_map_search_radius);
        setupMap(savedInstanceState);
        setupGoogleApiClient();
        setupBottomNav(savedInstanceState);
        setupBottomSheet(savedInstanceState);
        setupStopSchedule(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void setupMap(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            transitMapFragment = new TransitMapFragment();
            transitMapFragment.setTransitMapCallbacks(this);
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment_container, transitMapFragment).commit();
        } else {
            initialLoadFinished = savedInstanceState.getBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, false);

            transitMapFragment = (TransitMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_container);
            transitMapFragment.setTransitMapCallbacks(this);
        }
        transmitMapPresenter = new TransmitMapPresenter(getInjector(), this);
    }

    private void setupGoogleApiClient() {
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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

            transmitMapPresenter.tearDown();

            switch (item.getItemId()) {
                case R.id.map:
                    // TODO go to your location???
                    break;
                case R.id.my_stops:
                    transmitMapPresenter.loadSavedBusStops();
                    break;
                case R.id.routes:
                    showAllBusRoutes();
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

        // TODO ensure this works every time.
        // Set the bottom sheet peek height to half the height of the map view.
        mapFragmentContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomSheetBehavior.setPeekHeight((mapFragmentContainer.getHeight() / 2) + MainActivity.this.getResources().getDimensionPixelSize(R.dimen.action_bar_height));
                mapFragmentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setupStopSchedule(@Nullable Bundle savedInstanceState) {

        stopSchedulePresenter = new BusStopSchedulePresenter(getInjector(), this);
        stopScheduleAdapter = new ScheduledStopAdapter(savedInstanceState);

        stopScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        stopScheduleRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        stopScheduleRecyclerView.setAdapter(stopScheduleAdapter);
    }

    private void showAllBusRoutes() {
        // TODO
    }

    /**
     * Searches for bus stops around the user's location if the map has loaded AND the google API
     * client has been initialized. If {@code forceLoad} is set to {@code false}, then we only
     * load once (additional calls will be ignored). Otherwise, we load as long as the map is
     * ready and we have the user's last known location.
     *
     * @param forceLoad
     */
    private void loadBusStopsAtUserLocationIfReady(boolean forceLoad) {
        if (transitMapFragment.isMapReady() && googleApiClientInitialized && (!initialLoadFinished || forceLoad)) {

            Location lastKnownLocation = null;

            // Use the user's last known location if we have access to that information. Else just
            // defaults to downtown Winnipeg.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }

            transmitMapPresenter.loadBusStopsAroundCoordinates(lastKnownLocation != null ? lastKnownLocation.getLatitude() : null,
                    lastKnownLocation != null ? lastKnownLocation.getLongitude() : null,
                    mapSearchRadius);

            // Raise this flag. We don't need to search for bus stops on every orientation change.
            initialLoadFinished = true;
        }
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
        loadBusStopsAtUserLocationIfReady(false);
    }

    @Override
    public void showBusStopSchedule(BusStopViewModel busStopViewModel) {
        // Slightly open the bottom sheet so that we can display the bus stop's schedule.
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        stopScheduleAdapter.setBusStop(busStopViewModel);

        // Load the schedule or the bus stop.
        stopSchedulePresenter.loadScheduleForBusStop(busStopViewModel.getKey());
    }

    @Override
    public void loadBusRoutesForStop(BusStopViewModel busStopViewModel) {
        transmitMapPresenter.loadBusRoutesForStop(busStopViewModel);
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(mapFragmentContainer, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        stopScheduleAdapter.onSaveInstanceState(outState);

        outState.putBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, initialLoadFinished);
        outState.putInt(STATE_KEY_SELECTED_TAB, bottomNavigation.getSelectedItemId());
        outState.putInt(STATE_KEY_BOTTOM_SHEET_STATE, bottomSheetBehavior.getState());
    }

    @Override
    public void clearMarkersAndShowSearchRadius(Double latitude, Double longitude, Integer searchRadius) {
        transitMapFragment.clearMarkers();
        transitMapFragment.drawSearchRadius(latitude, longitude, mapSearchRadius);
    }

    @Override
    public void showScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        stopScheduleAdapter.setScheduledStops(scheduledStops);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClientInitialized = true;

        // If permission to get user's location has not yet been granted, then ask the user.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this,
                    IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    getString(R.string.location_permission_dialog_title),
                    getString(R.string.location_permission_rational),
                    PERMISSION_DIALOG_TAG);
        } else {
            // Permission is already granted. We can show the "my location" button.
            myLocationButton.show();
        }
        // Attempt to load near by bus stops regardless if the permission has been granted. If
        // permission isn't granted, we'll default to downtown Winnipeg.
        loadBusStopsAtUserLocationIfReady(false);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // If this failed, then still raise the flag and let's continue on without the user's location.
        googleApiClientInitialized = true;

        showErrorMessage(getString(R.string.error_finding_location));

        loadBusStopsAtUserLocationIfReady(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then show the "my location" button, and load the bus stops around the user's location.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            myLocationButton.show();
            loadBusStopsAtUserLocationIfReady(true);
        } else {
            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            FragmentUtils.showFragment(getSupportFragmentManager(),
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
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

    @OnClick(R.id.my_location_button)
    public void goToMyLocation() {
        loadBusStopsAtUserLocationIfReady(true);
    }
}
