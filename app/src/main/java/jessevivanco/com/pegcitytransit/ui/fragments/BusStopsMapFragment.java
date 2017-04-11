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
import android.util.Log;
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
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.StopSchedule;
import jessevivanco.com.pegcitytransit.ui.AppRouter;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.fragments.dialog.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.item_decorations.HorizontalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.view_holders.BusRouteCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopsMapFragment extends BaseFragment implements OnMapReadyCallback,
        BusStopsPresenter.ViewContract,
        BusRoutesPresenter.ViewContract,
        BusStopSchedulePresenter.ViewContract,
        GoogleMap.OnInfoWindowCloseListener,
        BusRouteCellViewHolder.OnBusRouteCellClickedListener {

    private static final String PERMISSION_DIALOG_TAG = "dialog";

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_stop_schedule_recycler_view)
    RecyclerView busStopsRecyclerView;

    @Inject
    AppRouter appRouter;

    private BusRoutesPresenter routesPresenter;
    private BusStopSchedulePresenter schedulePresenter;

    private BusStopsPresenter stopsPresenter;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;

    public static BusStopsMapFragment newInstance() {
        return new BusStopsMapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInjector().injectInto(this);
        ButterKnife.bind(this, view);

        setupPresenters();
        setupAdapters();
        setupRecyclerView();
        setupMap();
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
        LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.downtown_winnipeg_latitude)), Double.valueOf(getString(R.string.downtown_winnipeg_longitude)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(downtownWinnipeg, getResources().getInteger(R.integer.default_map_zoom)));
        googleMap.setInfoWindowAdapter(busStopInfoWindowAdapter);
        googleMap.setOnInfoWindowCloseListener(this);

        // Hide the "my location" button.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        showUserLocation();

        // TODO use your coordinates
        stopsPresenter.loadBusStopsAroundCoordinates(null, null, null);
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
    }

    private void setupPresenters() {
        schedulePresenter = new BusStopSchedulePresenter(getInjector(), this);

        routesPresenter = new BusRoutesPresenter(getInjector(), this);
        stopsPresenter = new BusStopsPresenter(getInjector(), this);
    }

    private void setupAdapters() {
        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(getActivity(), routesPresenter);
    }

    private void setupRecyclerView() {
        busStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        busStopsRecyclerView.addItemDecoration(new HorizontalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_x_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
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

    /**
     * Show the list of bus stops as markers in the map.
     *
     * @param busStops
     */
    @Override
    public void showBusStops(List<BusStopViewModel> busStops) {

        // Display each bus stop in the map with their GPS coordinates. Also keep a HashMap of
        // markers for each bus stop so we can figure out which marker points to which bus stop.
        HashMap<Marker, BusStopViewModel> markerToKeyHashMap = new HashMap<>();

        for (BusStopViewModel stop : busStops) {
            LatLng latLng = stop.getLatLng();
            if (latLng != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(String.valueOf(stop.getKey()))
                        .snippet(stop.getName())
                );
                markerToKeyHashMap.put(marker, stop);
            }
        }
        // Add our hashmap to our info window adapter. This is where we do the marker-to-bus-stop
        // lookup, and display the bus stop information when a marker is clicked.
        busStopInfoWindowAdapter.setMarkerToBusStopHashMap(markerToKeyHashMap);
    }

    /**
     * Bus routes for the provided <code>busStop</code> have been loaded. Find the bus stop in
     * our Marker-BusStop HashMap, plug the routes into that BusStop, then refresh the info window
     * for the bus stop marker. This will end up displaying the bus routes in the info window.
     *
     * @param busRoutes
     */
    @Override
    public void showBusRoutes(List<BusRouteViewModel> busRoutes) {

        // TODO null check busstop?
        BusStopViewModel busStop = routesPresenter.getBusStopFilter();

        // Add the routes to the bus stop POJO, then re-open the marker for that bus stop.
        HashMap<Marker, BusStopViewModel> markerBusStopHashMap = busStopInfoWindowAdapter.getMarkerToBusStopHashMap();

        // FYI: We have to do a reverse lookup b/c we don't currently know the marker for the bus stop.
        for (Map.Entry<Marker, BusStopViewModel> entry : markerBusStopHashMap.entrySet()) {
            if (busStop.getKey().equals(entry.getValue().getKey())) {

                // Found the target marker-stop pair. Attach the routes to the bus stop, refresh the
                // info window, then bust out of this loop.
                entry.getValue().setRoutes(busRoutes);
                entry.getKey().showInfoWindow();
                break;
            }
        }
    }

    @Override
    public void onLoadBusRoutesError(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();

        // TODO show error in recycler view?

    }

    @Override
    public void errorLoadingBusStops(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();

        // TODO show error in info-window?
    }

    /**
     * One of the bus route cells was clicked in the bottom recycler view. Open the schedule for
     * the bus route at that bus stop.
     *
     * @param busRoute
     */
    @Override
    public void onBusRouteCellClicked(BusRouteViewModel busRoute) {
        Log.v("DEBUG", "tapped on bus route " + busRoute.getNumber() + ", for stop " + routesPresenter.getBusStopFilter());
        appRouter.goToStopScheduleScreen(getActivity(), busRoute);
    }

    /**
     * Marker info window was closed. Clear out the bottom recycler view (shows the routes for the
     * selected bus stop) since we no longer have a marker selected.
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClose(Marker marker) {
//        scheduleAdapter.setSchedule(null);
    }

    @Override
    public void showSchedule(StopSchedule busStopSchedule) {
        Log.v("DEBUG", "Yooooo " + busStopSchedule);
//        scheduleAdapter.setSchedule(null);
    }

    @Override
    public void showErrorLoadingScheduleMessage(String message) {
        Log.v("DEBUG", "Awwwww " + message);
    }

    /**
     * Search the current map camera location for bus stops.
     */
    @OnClick(R.id.refresh_bus_stops)
    public void searchForBusStops() {
        if (googleMap != null) {

            // Clear markers, list, and filters.
            googleMap.clear();
//            scheduleAdapter.setSchedule(null);
            routesPresenter.setBusStopFilter(null);
            busStopInfoWindowAdapter.setMarkerToBusStopHashMap(null);

            // Load the new set of markers at the current camera position
            stopsPresenter.loadBusStopsAroundCoordinates(googleMap.getCameraPosition().target.latitude,
                    googleMap.getCameraPosition().target.longitude,
                    null);
        }
    }
}
