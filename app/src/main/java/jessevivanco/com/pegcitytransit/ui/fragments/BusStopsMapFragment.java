package jessevivanco.com.pegcitytransit.ui.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;

public class BusStopsMapFragment extends BaseFragment implements TransitMapFragment.OnMapReadyListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = BusStopsMapFragment.class.getSimpleName();

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    private GoogleApiClient googleApiClient;
    private TransitMapFragment transitMapFragment;

    private Location lastKnownLocation;
    private boolean lastKnownLocationLoaded;

    public static BusStopsMapFragment newInstance() {
        return new BusStopsMapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInjector().injectInto(this);
        ButterKnife.bind(this, view);

        setupGoogleApiClient();

        if (savedInstanceState == null) {
            transitMapFragment = TransitMapFragment.newInstance(this);
            getChildFragmentManager().beginTransaction().add(R.id.map_fragment_container, transitMapFragment).commit();
        } else {
            transitMapFragment = (TransitMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment_container);
            transitMapFragment.setMapReadyListener(this);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
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
    public void onConnected(@Nullable Bundle bundle) {
        // TODO CHECK premission
        this.lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        this.lastKnownLocationLoaded = true;

        loadBusStopsIfReady();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.lastKnownLocationLoaded = true;

        Snackbar.make(rootContainer, getString(R.string.error_finding_location), Snackbar.LENGTH_LONG).show();

        loadBusStopsIfReady();
    }

    @Override
    public void onMapReady() {
        loadBusStopsIfReady();
    }

    /**
     * Once the map has loaded AND we've received the user's location, then searches for bus stops
     * around the user's location.
     */
    private void loadBusStopsIfReady() {
        if (transitMapFragment.isMapReady() && lastKnownLocationLoaded) {
            Log.v("DEBUG", "YOLO loading bus stops.");
            transitMapFragment.loadBusStopsAtCoordinates(lastKnownLocation != null ? lastKnownLocation.getLatitude() : null,
                    lastKnownLocation != null ? lastKnownLocation.getLongitude() : null,
                    getResources().getInteger(R.integer.default_map_search_radius));
        }
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {

        if (transitMapFragment.isMapReady()) {
            transitMapFragment.loadBusStopsAtCameraCoordinates(getResources().getInteger(R.integer.default_map_search_radius));
        } else {
            Log.e(TAG, "Map not ready!");
        }
    }
}
