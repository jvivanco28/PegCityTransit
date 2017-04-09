package jessevivanco.com.pegcitytransit.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopsAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.dialog.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.item_decorations.BusStopListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusStopCellViewHolder;

public class BusStopsMapFragment extends BaseFragment implements OnMapReadyCallback, BusStopsPresenter.ViewContract, BusStopCellViewHolder.OnBusStopCellClickedListener {

    private static final String PERMISSION_DIALOG_TAG = "dialog";

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_stops_recycler_view)
    RecyclerView busStopsRecyclerView;

    private BusStopsAdapter busStopsAdapter;
    private BusStopsPresenter busStopsPresenter;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;

    public static BusStopsMapFragment newInstance() {
        return new BusStopsMapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupMap();
        setupAdapter();
        setupRecyclerView();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        // TODO get real GPS coordinates
        // TODO display your location with a circle or something.
        // Default coordinates if we don't have user's location permission.
        LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.default_lat)), Double.valueOf(getString(R.string.default_long)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(downtownWinnipeg, getResources().getInteger(R.integer.default_map_zoom)));
        googleMap.setInfoWindowAdapter(busStopInfoWindowAdapter);

        // Hide the "my location" button.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        showUserLocation();

        // TODO use your coordinates
        busStopsPresenter.loadBusStops(null, null, null);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void showUserLocation() {

        // If permission has not yet been granted, then ask the user.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(this,
                    IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    getString(R.string.location_permission_dialog_title),
                    getString(R.string.location_permission_rational),
                    PERMISSION_DIALOG_TAG);

        } else if (googleMap != null) {

            // Access to the location has been granted to the app.
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(getActivity(), getInjector());
    }

    protected void setupAdapter() {
        busStopsPresenter = new BusStopsPresenter(getInjector(), this);
        busStopsAdapter = new BusStopsAdapter(busStopsPresenter, this);
    }

    private void setupRecyclerView() {
        busStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        busStopsRecyclerView.addItemDecoration(new BusStopListItemDecoration(getActivity()));
        busStopsRecyclerView.setAdapter(busStopsAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then let's get the user's location.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Enable the my location layer if the permission has been granted.
            showUserLocation();

        } else {

            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            FragmentUtils.showFragment(this,
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
    }

    @Override
    public void showBusStops(List<BusStop> busStops) {

        // Display the list of bus stops in the RecyclerView
        busStopsAdapter.setBusStops(busStops);

        // Display each bus stop in the map with their GPS coordinates. Also keep a HashMap of
        // markers for each bus stop so we can figure out which marker points to which bus stop.
        HashMap<Marker, BusStop> markerToKeyHashMap = new HashMap<>();

        for (BusStop stop : busStops) {
            LatLng latLng = stop.getCentre().getGeographic().getLatLng();
            if (latLng != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(String.valueOf(stop.getKey()))
                        .snippet(stop.getName())
                );
                markerToKeyHashMap.put(marker, stop);
            }
        }
        busStopInfoWindowAdapter.setMarkerToBusStopHashMap(markerToKeyHashMap);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * We tapped on a cell within the recycler view. Look up the marker for that bus stop, then
     * animate the camera over to those coordinates.
     *
     * @param adapterPosition
     */
    @Override
    public void onBusStopCellClicked(int adapterPosition) {
        BusStop selectedBusStop = busStopsAdapter.getBusStops().get(adapterPosition);

        Marker selectedBusStopMarker = busStopInfoWindowAdapter.getMarkerForBusStop(selectedBusStop);
        if (selectedBusStopMarker != null) {
            selectedBusStopMarker.showInfoWindow();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedBusStopMarker.getPosition(), getResources().getInteger(R.integer.default_map_zoom)));
        }
    }

    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {
        if (googleMap != null) {

            googleMap.clear();

            busStopsPresenter.loadBusStops(googleMap.getCameraPosition().target.latitude,
                    googleMap.getCameraPosition().target.longitude,
                    null);
        }
    }
}
