package jessevivanco.com.pegcitytransit.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.adapters.ScheduledStopAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.dialog.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

@Deprecated
public class BusStopsMapFragment extends BaseFragment implements TransitMapFragment.TransitMapCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BusStopSchedulePresenter.ViewContract {

    private static final String TAG = BusStopsMapFragment.class.getSimpleName();
    private static final String PERMISSION_DIALOG_TAG = "dialog";
    private static final String STATE_KEY_INITIAL_LOAD_FINISHED = "initial_load_finished";
    private static final String STATE_KEY_GOOGLE_API_CLIENT_INITIALIZED = "client_initialized";

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.my_location_button)
    FloatingActionButton myLocationButton;

    @BindView(R.id.bottom_sheet_container)
    ViewGroup stopScheduleBottomSheetContainer;
    BottomSheetBehavior stopScheduleBottomSheetBehaviour;

    @BindView(R.id.bottom_sheet_recycler_view)
    RecyclerView stopScheduleRecyclerView;
    ScheduledStopAdapter scheduledStopAdapter;
    BusStopSchedulePresenter stopSchedulePresenter;

    private GoogleApiClient googleApiClient;
    private TransitMapFragment transitMapFragment;

    private boolean initialLoadFinished;

    private boolean googleApiClientInitialized;

    public static BusStopsMapFragment newInstance() {
        return new BusStopsMapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInjector().injectInto(this);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
//            transitMapFragment = TransitMapFragment.newInstance(this);
            transitMapFragment = new TransitMapFragment();
            getChildFragmentManager().beginTransaction().add(R.id.map_fragment_container, transitMapFragment).commit();
        } else {
            initialLoadFinished = savedInstanceState.getBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, false);
            googleApiClientInitialized = savedInstanceState.getBoolean(STATE_KEY_GOOGLE_API_CLIENT_INITIALIZED, false);

            transitMapFragment = (TransitMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment_container);
//            transitMapFragment.setTransitMapCallbacks(this);
        }
        transitMapFragment.setTransitMapCallbacks(this);
        setupBottomSheet(savedInstanceState);
        setupGoogleApiClient();
        setupMyLocationButton(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }

    private void setupBottomSheet(@Nullable Bundle savedInstanceState) {
        stopScheduleBottomSheetBehaviour = BottomSheetBehavior.from(stopScheduleBottomSheetContainer);
        stopScheduleBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

        stopSchedulePresenter = new BusStopSchedulePresenter(getInjector(), this);
        scheduledStopAdapter = new ScheduledStopAdapter(savedInstanceState);

        stopScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        stopScheduleRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        stopScheduleRecyclerView.setAdapter(scheduledStopAdapter);
    }

    private void setupGoogleApiClient() {
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Shows the "my location" button if we have location privileges.
     *
     * @param savedInstanceState
     */
    private void setupMyLocationButton(@Nullable Bundle savedInstanceState) {
        // If we have access to the user's location, then show the "my location" button.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && myLocationButton.getVisibility() != View.VISIBLE) {
//            showMyLocationButton(savedInstanceState == null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_KEY_INITIAL_LOAD_FINISHED, initialLoadFinished);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleApiClientInitialized = true;

        // If permission to get user's location has not yet been granted, then ask the user.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this,
                    IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    getString(R.string.location_permission_dialog_title),
                    getString(R.string.location_permission_rational),
                    PERMISSION_DIALOG_TAG);
        }
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

        Snackbar.make(rootContainer, getString(R.string.error_finding_location), Snackbar.LENGTH_LONG).show();

        loadBusStopsAtUserLocationIfReady(false);
    }

    @Override
    public void onMapReady() {
        loadBusStopsAtUserLocationIfReady(false);
    }

//    @Override
//    public void showStopSchedule(BusStopViewModel busStopViewModel) {
//        stopSchedulePresenter.loadScheduleForBusStop(busStopViewModel.getKey());
//        stopScheduleBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then show the "my location" button, and load the bus stops around the user's location.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            myLocationButton.show();
            loadBusStopsAtUserLocationIfReady(false);
        } else {
            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            FragmentUtils.showFragment(this,
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
    }

    @Override
    public void showScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        scheduledStopAdapter.setScheduledStops(scheduledStops);
    }

//    @Override
//    public void showErrorLoadingScheduleMessage(String message) {
//        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
//    }

    /**
     * Once the map has loaded AND google API client has been setup, then searches for bus stops
     * around the user's location. If {@code forceLoad} is set to {@code false}, then we only execute the
     * load once (additional calls will be ignored). Else, we execute the load as long as the map is
     * ready and we have the user's last known location.
     *
     * @param forceLoad
     */
    private void loadBusStopsAtUserLocationIfReady(boolean forceLoad) {
//        if (transitMapFragment.isMapReady() && googleApiClientInitialized && (!initialLoadFinished || forceLoad)) {
//
//            Location lastKnownLocation = null;
//
//            // Use the user's last known location if we have access to that information. Else just
//            // defaults to downtown Winnipeg.
//            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//            }
//            transitMapFragment.loadBusStopsAtCoordinates(lastKnownLocation != null ? lastKnownLocation.getLatitude() : null,
//                    lastKnownLocation != null ? lastKnownLocation.getLongitude() : null,
//                    getResources().getInteger(R.integer.default_map_search_radius));
//
//            // Raise this flag. We don't need to search for bus stops on every orientation change.
//            initialLoadFinished = true;
//        }
    }

    @OnClick(R.id.my_location_button)
    public void goToMyLocation() {

        loadBusStopsAtUserLocationIfReady(true);
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

//        if (transitMapFragment.isMapReady()) {
//            transitMapFragment.loadBusStopsAtCameraCoordinates(getResources().getInteger(R.integer.default_map_search_radius));
//        } else {
//            Log.w(TAG, "Map not ready!");
//        }
    }

    @Override
    public void showErrorMessage(String msg) {

    }

    @Override
    public void showBusStopSchedule(BusStopViewModel busStopViewModel) {

    }

    @Override
    public void loadBusRoutesForStop(BusStopViewModel busStopViewModel) {

    }
}
