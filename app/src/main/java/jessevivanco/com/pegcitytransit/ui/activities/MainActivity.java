package jessevivanco.com.pegcitytransit.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteSelectedListener;
import jessevivanco.com.pegcitytransit.ui.fragments.BusRoutesDialogFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.FragmentUtils;
import jessevivanco.com.pegcitytransit.ui.fragments.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.fragments.SettingsDialogFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.TransmitMapPresenter;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.util.ScreenUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusRouteCell;
import jessevivanco.com.pegcitytransit.ui.views.BusStopScheduleBottomSheet;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        TransmitMapPresenter.ViewContract,
        TransitMapFragment.TransitMapCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnBusRouteSelectedListener,
        BusStopScheduleBottomSheet.OnFavStopRemovedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String STATE_KEY_INITIAL_LOAD_FINISHED = "initial_load_finished";
    private static final String STATE_KEY_SELECTED_TAB = "selected_tab";
    private static final String STATE_KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state";
    private static final String STATE_KEY_ROUTE_CELL_VISIBILITY = "route_cell_visibility";

    private static final String PERMISSION_DIALOG_TAG = "dialog";

    @BindView(R.id.map_fragment_container)
    FrameLayout mapFragmentContainer;

    @BindView(R.id.bus_route_info)
    BusRouteCell busRouteCell;

    @BindView(R.id.my_location_fab)
    FloatingActionButton myLocationFab;

    @BindView(R.id.search_bus_stops_fab)
    FloatingActionButton searchBusStopsFab;

    @BindView(R.id.map_loading_indicator)
    ProgressBar mapLoadingIndicator;

    @BindView(R.id.tab_navigation)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.bottom_sheet_container)
    BusStopScheduleBottomSheet stopScheduleBottomSheet;
    BottomSheetBehavior bottomSheetScheduleBehavior;

    private GoogleApiClient googleApiClient;

    private TransitMapFragment transitMapFragment;
    private TransmitMapPresenter transmitMapPresenter;

    private BusRoutesDialogFragment busRoutesModal;
    private SettingsDialogFragment settingsModal;

    private boolean initialActivityLoadFinished;
    private boolean initialMapLoadFinished;
    private boolean googleApiClientInitialized;
    private boolean showPermissionRationalDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        // TODO check service advisories on startup
        initialActivityLoadFinished = savedInstanceState != null;
        setSearchFabMargin();
        setupMap(savedInstanceState);
        setupBusRouteCell(savedInstanceState);
        setupGoogleApiClient();
        setupBottomNav(savedInstanceState);
        setupBottomSheet(savedInstanceState);

        // If google play services aren't available, then just remove the bottom nav callbacks.
        // It shouldn't matter that the above code has run; nothing will have busted.
        if (!isGooglePlayServicesAvailable()) {
            bottomNavigation.setOnNavigationItemSelectedListener(null);
            Snackbar.make(mapFragmentContainer, getString(R.string.error_google_play_services_missing), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (showPermissionRationalDialog) {
            showPermissionRationalDialog = false;

            FragmentUtils.showFragment(getSupportFragmentManager(),
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
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

    @Override
    protected void onDestroy() {
        transmitMapPresenter.tearDown();
        stopScheduleBottomSheet.tearDown();
        super.onDestroy();
    }

    private void setSearchFabMargin() {
        ViewGroup.MarginLayoutParams searchFabLayoutParams = (ViewGroup.MarginLayoutParams) searchBusStopsFab.getLayoutParams();

        // GPS Enabled.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            searchFabLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.top_fab_margin_bottom_gps_enabled);
        } else {
            searchFabLayoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.top_fab_margin_bottom_gps_disabled);
        }
        searchBusStopsFab.setLayoutParams(searchFabLayoutParams);
    }

    private void setupMap(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            transitMapFragment = new TransitMapFragment();
            transitMapFragment.setTransitMapCallbacks(this);
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment_container, transitMapFragment).commit();
        } else {
            initialMapLoadFinished = savedInstanceState.getBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, false);

            transitMapFragment = (TransitMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_container);
            transitMapFragment.setTransitMapCallbacks(this);
        }
        transmitMapPresenter = new TransmitMapPresenter(getInjector(), this);
    }

    private void setupBusRouteCell(@Nullable Bundle savedInstanceState) {

        // Adding a top margin so that we can avoid being occluded by the status bar.
        // NOTE: Was having issues getting fitsSystemWindow to work for this in the xml layout file.
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) busRouteCell.getLayoutParams();
        lp.topMargin += ScreenUtil.getStatusBarHeightIfNeeded(this);
        busRouteCell.setLayoutParams(lp);

        busRouteCell.onRestoreInstanceState(savedInstanceState);
        busRouteCell.setVisibility(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_ROUTE_CELL_VISIBILITY, View.GONE) :
                View.GONE);

        // Just re-open the bus routes modal if we tap on this cell.
        busRouteCell.setOnCellClickedListener(busRoute -> onRoutesTabSelected(true));
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
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * Displays the floating action buttons for the "Search" tab if it is selected. The "my location"
     * floating action button stays hidden if we don't have access to the user's location.
     */
    private void setupFabVisibility(int selectedTabItemId) {
        if (selectedTabItemId != R.id.search) {
            searchBusStopsFab.hide();
        } else {
            searchBusStopsFab.show();
        }

        // We can only show "my location" button if we have permission to access the user's location.
        // We can only show "my location" button if we have permission to access the user's location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLocationFab.show();
        }
    }

    private void setupBottomSheet(@Nullable Bundle savedInstanceState) {

        stopScheduleBottomSheet.initialize(this, this, savedInstanceState, getInjector());
        stopScheduleBottomSheet.setOnCloseButtonClickedListener(v -> {
            bottomSheetScheduleBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        bottomSheetScheduleBehavior = BottomSheetBehavior.from(stopScheduleBottomSheet);
        bottomSheetScheduleBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                handleBottomSheetStateChanged(newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Unused.
            }
        });

        int restoredBottomSheetState = savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_HIDDEN) :
                BottomSheetBehavior.STATE_HIDDEN;

        // Set the restored bottom sheet position. The default/initial state is for it to be hidden.
        bottomSheetScheduleBehavior.setState(restoredBottomSheetState);

        // For whatever reason, the bottomSheetScheduleBehavior callbacks don't get invoked from the above line,
        // so we have explicitly restore the state of the "status bar".
        handleBottomSheetStateChanged(restoredBottomSheetState);

        // Set the bottom sheet peek height to half the height of the map view.
        mapFragmentContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomSheetScheduleBehavior.setPeekHeight(mapFragmentContainer.getHeight() / 2);
                mapFragmentContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * NOTE: This was some serious headachery.
     * If the bottom sheet is all the way open, then we set bottom sheet's background color. This,
     * in effect, will create the illusion that we're changing the status bar color, but in truth
     * we're not. There's just enough space above the bottom sheet content that changing that color
     * makes it look like we tweaked the status bar. If the bottom sheet is NOT fully expanded, then
     * we reset that color back to transparent.
     */
    private void handleBottomSheetStateChanged(int newState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // If the bottom sheet is all the way opened, then change the status bar color to something opaque.
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                stopScheduleBottomSheet.setBackgroundResource(R.color.colorPrimary);
            } else {
                // Else we're back to a translucent status bar.
                stopScheduleBottomSheet.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    private void onRoutesTabSelected(boolean showRoutesModal) {

        // Clear the map.
        if (busRouteCell.getVisibility() != View.VISIBLE) {
            clearMarkers();
        }
        clearSearchRadius();

        if (showRoutesModal) {
            if (busRoutesModal == null) {
                busRoutesModal = BusRoutesDialogFragment.newInstance();
            }
            busRoutesModal.show(getSupportFragmentManager(), BusRoutesDialogFragment.TAG);
        }
    }

    private void showSettings() {

        // Clear the map.
        clearMarkers();
        clearSearchRadius();

        if (settingsModal == null) {
            settingsModal = SettingsDialogFragment.newInstance();
        }
        settingsModal.show(getSupportFragmentManager(), SettingsDialogFragment.TAG);
    }

    /**
     * Searches for bus stops around the user's location if:<br/>
     * 1. The map has loaded<br/>
     * 2. The google API client has been initialized.<br/>
     * 3. We have permission to access the user's location.<br/>
     * 4. The user's last known location is not null.
     * <p>
     * If {@code forceLoad} is set to {@code false}, then we only
     * load once (additional calls will be ignored). Otherwise, we load as long as the map is
     * ready and we have the user's last known location.
     */
    private void loadBusStopsAtUserLocationIfReady(boolean forceLoad) {

        if (transitMapFragment.isMapReady() &&
                googleApiClientInitialized &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                (!initialMapLoadFinished || forceLoad) &&
                LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {

            transitMapFragment.showUserLocationIfAllowed();

            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            transmitMapPresenter.loadBusStopsAroundCoordinates(lastKnownLocation != null ? lastKnownLocation.getLatitude() : null,
                    lastKnownLocation != null ? lastKnownLocation.getLongitude() : null);

            // Raise this flag. We don't need to search for bus stops on every orientation change.
            initialMapLoadFinished = true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                busRouteCell.setVisibility(View.GONE);
                loadBusStopsAtUserLocationIfReady(true);
                break;
            case R.id.my_stops:
                busRouteCell.setVisibility(View.GONE);
                transmitMapPresenter.loadSavedBusStops();
                break;
            case R.id.routes:
                onRoutesTabSelected(true);
                break;
            case R.id.settings:
                busRouteCell.setVisibility(View.GONE);
                showSettings();
                break;
        }
        setupFabVisibility(item.getItemId());
        return true;
    }

    @Override
    public void showBusRoutesForStop(BusStopViewModel busStop) {
        // The BusStopViewModel should now contain the list of bus routes that stop at that bus stop.
        transitMapFragment.showInfoWindowForBusStop(busStop);
    }

    @Override
    public void showBusStops(List<BusStopViewModel> busStops, long markerVisibilityDelayMillis, boolean focusInMap) {
        transitMapFragment.showBusStops(busStops, markerVisibilityDelayMillis, focusInMap);
    }

    @Override
    public void onMapReady() {
        loadBusStopsAtUserLocationIfReady(false);
    }

    @Override
    public void showBusStopSchedule(BusStopViewModel busStopViewModel) {

        // Slightly open the bottom sheet so that we can display the bus stop's schedule.
        bottomSheetScheduleBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
        busRouteCell.onSaveInstanceState(outState);

        outState.putInt(STATE_KEY_ROUTE_CELL_VISIBILITY, busRouteCell.getVisibility());
        outState.putBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, initialMapLoadFinished);
        outState.putInt(STATE_KEY_SELECTED_TAB, bottomNavigation.getSelectedItemId());
        outState.putInt(STATE_KEY_BOTTOM_SHEET_STATE, bottomSheetScheduleBehavior.getState());
    }

    @Override
    public void showSearchRadius(Double latitude, Double longitude, Integer searchRadius, boolean focusInMap) {
        transitMapFragment.drawSearchRadius(latitude, longitude, searchRadius, focusInMap);
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

            // We don't need to keep asking on every orientation change.
            if (!initialActivityLoadFinished) {
                PermissionUtils.requestPermission(this,
                        IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal(),
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        getString(R.string.location_permission_dialog_title),
                        getString(R.string.location_permission_rational),
                        PERMISSION_DIALOG_TAG);
            }
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

        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then show the "my location" button, and load the bus stops around the user's location.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            setSearchFabMargin();
            setupFabVisibility(bottomNavigation.getSelectedItemId());
            loadBusStopsAtUserLocationIfReady(true);
        } else {
            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            // NOTE: We can't show the dialog until onResume has been called.
            // See https://stackoverflow.com/questions/37164415/android-fatal-error-can-not-perform-this-action-after-onsaveinstancestate
            showPermissionRationalDialog = true;
        }
    }

    @Override
    public void onBusRouteSelected(BusRouteViewModel busRoute) {

        // Close any open bottom sheets (this method can be called from both the routes bottom sheet and the schedule bottom sheet).
        if (busRoutesModal != null) {
            busRoutesModal.dismissAllowingStateLoss();
        }
        if (bottomSheetScheduleBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetScheduleBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        // If we're not on the "Routes" bottom tab, then select it now since we're switching the
        // context over to displaying a bus route.
        if (bottomNavigation.getSelectedItemId() != R.id.routes) {

            // Temporarily disable the selection listener so that we don't re-open the bus routes modal.
            bottomNavigation.setOnNavigationItemSelectedListener(null);
            bottomNavigation.setSelectedItemId(R.id.routes);
            bottomNavigation.setOnNavigationItemSelectedListener(this);

            onRoutesTabSelected(false);
            setupFabVisibility(bottomNavigation.getSelectedItemId());
        }

        busRouteCell.setVisibility(View.VISIBLE);
        busRouteCell.bind(busRoute);

        transmitMapPresenter.loadBusStopsForBusRoute(busRoute);
    }

    /**
     * Progress bar map overlay which tells us that we're searching for bus stops in a search radius.
     */
    @Override
    public void showStopsLoadingIndicator(boolean visible) {
        mapLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Loading indicator in the bus route cell which tells us that we're loading all of the stops for
     * some bus route.
     */
    @Override
    public void showRouteLoadingIndicator(boolean visible) {
        busRouteCell.showLoadingIndicator(visible);
        mapLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * A bus stop was removed from the favorites list. If the selected tab is "My Stops" then
     * we need to remove the bus stop marker.
     */
    @Override
    public void onFavStopRemoved(BusStopViewModel busStop) {
        if (bottomNavigation.getSelectedItemId() == R.id.my_stops) {
            // Just Refresh the list.
            transmitMapPresenter.loadSavedBusStops();
            bottomSheetScheduleBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.search_bus_stops_fab)
    public void searchForBusStops() {
        if (transitMapFragment.isMapReady()) {
            LatLng cameraPosition = transitMapFragment.getCameraPosition();
            transmitMapPresenter.loadBusStopsAroundCoordinates(cameraPosition.latitude, cameraPosition.longitude);
        } else {
            Log.w(TAG, "Map not ready!");
        }
    }

    @OnClick(R.id.my_location_fab)
    public void goToMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastKnownLocation != null) {
                transitMapFragment.zoomAndReorientToLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), getResources().getInteger(R.integer.default_my_location_map_zoom), lastKnownLocation.getBearing());
            } else {
                showErrorMessage(getString(R.string.error_finding_location));
                Log.e(TAG, "Last known location is null.");
            }
        } else {
            // Shouldn't happen. The button should not be visible.
            throw new IllegalStateException("Can't go to user's location. Permission is not granted.");
        }
    }
}
