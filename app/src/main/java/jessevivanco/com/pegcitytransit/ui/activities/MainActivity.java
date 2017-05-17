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
import android.util.Log;
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
import jessevivanco.com.pegcitytransit.ui.fragments.BusRoutesDialogFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.FragmentUtils;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.dialog.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.presenters.TransmitMapPresenter;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.views.BusStopScheduleBottomSheet;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class MainActivity extends BaseActivity implements TransmitMapPresenter.ViewContract,
        TransitMapFragment.TransitMapCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BusRoutesDialogFragment.OnBusRouteSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String STATE_KEY_INITIAL_LOAD_FINISHED = "initial_load_finished";
    private static final String STATE_KEY_SELECTED_TAB = "selected_tab";
    private static final String STATE_KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state";

    private static final String PERMISSION_DIALOG_TAG = "dialog";

    @BindView(R.id.map_fragment_container)
    FrameLayout mapFragmentContainer;

    @BindView(R.id.my_location_fab)
    FloatingActionButton myLocationFab;

    @BindView(R.id.search_bus_stops_fab)
    FloatingActionButton searchBusStopsFab;

    @BindView(R.id.tab_navigation)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.bottom_sheet_container)
    BusStopScheduleBottomSheet stopScheduleBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;

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
            bottomNavigation.setSelectedItemId(R.id.search);
        } else {
            bottomNavigation.setSelectedItemId(savedInstanceState.getInt(STATE_KEY_SELECTED_TAB));
        }

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {

            transmitMapPresenter.tearDown();

            switch (item.getItemId()) {
                case R.id.search:
                    loadBusStopsAtUserLocationIfReady(true);
                    break;
                case R.id.my_stops:
                    transmitMapPresenter.loadSavedBusStops();
                    break;
                case R.id.routes:
                    showBusRoutesModal();
                    break;
            }
            setupFabVisibility(item.getItemId());
            return true;
        });

        bottomNavigation.setOnNavigationItemReselectedListener(item -> {

            // Only re-selecting the bus routes tab will do something.
            if (item.getItemId() == R.id.routes) {
                showBusRoutesModal();
            }
        });
    }

    /**
     * Displays the floating action buttons for the "Search" tab if it is selected. The "my location"
     * floating action button stays hidden if we don't have access to the user's location.
     */
    private void setupFabVisibility(int selectedTabItemId) {
        if (selectedTabItemId != R.id.search) {
            searchBusStopsFab.hide();
            myLocationFab.hide();
        } else {
            searchBusStopsFab.show();

            // We can only show "my location" button if we have permission to access the user's location.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                myLocationFab.show();
            }
        }
    }

    private void setupBottomSheet(@Nullable Bundle savedInstanceState) {

        stopScheduleBottomSheet.initialize(savedInstanceState, getInjector());
        stopScheduleBottomSheet.setOnCloseButtonClickedListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        bottomSheetBehavior = BottomSheetBehavior.from(stopScheduleBottomSheet);
        bottomSheetBehavior.setState(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_HIDDEN) :
                BottomSheetBehavior.STATE_HIDDEN);

        // Set the bottom sheet peek height to half the height of the map view.
        mapFragmentContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomSheetBehavior.setPeekHeight(mapFragmentContainer.getHeight() / 2);
                mapFragmentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void showBusRoutesModal() {
        BusRoutesDialogFragment.newInstance().show(getSupportFragmentManager(), BusRoutesDialogFragment.TAG);
    }

    /**
     * Searches for bus stops around the user's location if:<br/>
     * 1. The map has loaded<br/>
     * 2. The google API client has been initialized.<br/>
     * 3. We have permission to access the user's location.<br/>
     * <p>
     * If {@code forceLoad} is set to {@code false}, then we only
     * load once (additional calls will be ignored). Otherwise, we load as long as the map is
     * ready and we have the user's last known location.
     */
    private void loadBusStopsAtUserLocationIfReady(boolean forceLoad) {
        Log.v("DEBUG", "loadBusStopsAtUserLocationIfReady");

        if (transitMapFragment.isMapReady() &&
                googleApiClientInitialized &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                (!initialLoadFinished || forceLoad)) {


            Log.v("DEBUG", "doing it!");

            // Use the user's last known location if we have access to that information. Else just
            // defaults to downtown Winnipeg.
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

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
        transitMapFragment.showInfoWindowForBusStop(busStop);
    }

    @Override
    public void showBusStops(List<BusStopViewModel> busStops, long markerVisibilityDelayMillis) {
        transitMapFragment.showBusStops(busStops, true, markerVisibilityDelayMillis);
    }

    @Override
    public void onMapReady() {
        loadBusStopsAtUserLocationIfReady(false);
    }

    @Override
    public void showBusStopSchedule(BusStopViewModel busStopViewModel) {

        // Slightly open the bottom sheet so that we can display the bus stop's schedule.
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Load the schedule or the bus stop.
        stopScheduleBottomSheet.loadScheduleForBusStop(busStopViewModel);
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

        stopScheduleBottomSheet.onSaveInstanceState(outState);

        outState.putBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, initialLoadFinished);
        outState.putInt(STATE_KEY_SELECTED_TAB, bottomNavigation.getSelectedItemId());
        outState.putInt(STATE_KEY_BOTTOM_SHEET_STATE, bottomSheetBehavior.getState());
    }

    @Override
    public void showSearchRadius(Double latitude, Double longitude, Integer searchRadius) {
        transitMapFragment.drawSearchRadius(latitude, longitude, mapSearchRadius);
    }

    @Override
    public void clearMarkers() {
        transitMapFragment.clearMarkers();
    }

    @Override
    public void clearSearchRadius() {
        transitMapFragment.clearSearchRadius();
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
            setupFabVisibility(bottomNavigation.getSelectedItemId());
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

        Log.v("DEBUG", "onRequestPermissionsResult");
        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then show the "my location" button, and load the bus stops around the user's location.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            setupFabVisibility(bottomNavigation.getSelectedItemId());
            loadBusStopsAtUserLocationIfReady(true);
        } else {
            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            FragmentUtils.showFragment(getSupportFragmentManager(),
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
    }

    @Override
    public void onBusRouteSelected(BusRouteViewModel busRoute) {
        transmitMapPresenter.loadBusStopsForBusRoute(busRoute);
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.search_bus_stops_fab)
    public void searchForBusStops() {

        if (transitMapFragment.isMapReady()) {
            LatLng cameraPosition = transitMapFragment.getCameraPosition();
            transmitMapPresenter.loadBusStopsAroundCoordinates(cameraPosition.latitude, cameraPosition.longitude, mapSearchRadius);
        } else {
            Log.w(TAG, "Map not ready!");
        }
    }

    @OnClick(R.id.my_location_fab)
    public void goToMyLocation() {
        loadBusStopsAtUserLocationIfReady(true);
    }
}
