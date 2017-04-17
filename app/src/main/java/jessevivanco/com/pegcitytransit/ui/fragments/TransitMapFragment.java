package jessevivanco.com.pegcitytransit.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.ui.AppRouter;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

// TODO Need to handle orientation changes
public class TransitMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        BusStopsPresenter.ViewContract,
        BusRoutesPresenter.ViewContract {

    @Inject
    AppRouter appRouter;

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    private AppComponent injector;

    private BusStopsPresenter stopsPresenter;
    private BusRoutesPresenter routesPresenter;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;
    private OnMapReadyListener onMapReadyListener;

    private
    @Nullable
    Circle searchArea;

    public static TransitMapFragment newInstance(OnMapReadyListener onMapReadyListener) {

        Log.v("DEBUG", "Creating new instance");

        TransitMapFragment fragment = new TransitMapFragment();
        fragment.setMapReadyListener(onMapReadyListener);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        injector = ((PegCityTransitApp) getActivity().getApplication()).getInjector();
        injector.injectInto(this);

        setupMap();
        setupPresenters();
        setupAdapters();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_transit_map;
    }

    private void setupMap() {
        mapFragment.getMapAsync(this);
    }

    private void setupPresenters() {
        stopsPresenter = new BusStopsPresenter(injector, this);
        routesPresenter = new BusRoutesPresenter(injector, this);
    }

    private void setupAdapters() {
        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(getActivity(), routesPresenter);
    }

    /**
     * Clear markers, routes, and filters, etc.
     */
    private void clearMap() {
        // Clear markers, list, and filters.
        routesPresenter.setBusStopFilter(null);
        busStopInfoWindowAdapter.clearMarkers();
        busStopInfoWindowAdapter.setMarkerToBusStopHashMap(null);
    }

    /**
     * Loads bus stops around the current camrea coordinates.
     *
     * @param radius
     */
    public void loadBusStopsAtCameraCoordinates(int radius) {
        loadBusStopsAtCoordinates(null, null, radius);
    }

    /**
     * Loads the bus stops at the provided coordinates. If no coordinates are provided, then uses
     * the current camera coordiantes.
     *
     * @param latitude
     * @param longitude
     * @param radius
     */
    public void loadBusStopsAtCoordinates(@Nullable Double latitude, @Nullable Double longitude, int radius) {

        Log.v("DEBUG", "Called loadBusStopsAtCoordinates");

        // Remove previous search area circle
        if (searchArea != null) {
            searchArea.remove();
        }

        if (latitude == null) {
            latitude = googleMap.getCameraPosition().target.latitude;
        }
        if (longitude == null) {
            longitude = googleMap.getCameraPosition().target.longitude;
        }

        // Display the area that we're searching for bus stops.
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(radius)
                .strokeColor(getResources().getColor(R.color.map_search_border))
                .strokeWidth(getResources().getDimensionPixelSize(R.dimen.map_search_border_width));

        searchArea = googleMap.addCircle(circleOptions);

        // Load the new set of markers at the current camera position
        stopsPresenter.loadBusStopsAroundCoordinates(latitude, longitude, radius);
    }


    public void loadBusStopsForBusRoute(BusRouteViewModel route) {

        Log.v("DEBUG", "Called loadBusStopsForBusRoute");
        stopsPresenter.loadBusStopsForBusRoute(route);
    }

    public void setMapReadyListener(OnMapReadyListener onMapReadyListener) {
        this.onMapReadyListener = onMapReadyListener;
    }

    public boolean isMapReady() {
        return googleMap != null;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        // Default coordinates if we don't have user's location permission.
        LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.downtown_winnipeg_latitude)), Double.valueOf(getString(R.string.downtown_winnipeg_longitude)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(downtownWinnipeg, getResources().getInteger(R.integer.default_city_wide_map_zoom)));
        googleMap.setInfoWindowAdapter(busStopInfoWindowAdapter);
        googleMap.setOnInfoWindowClickListener(this);

        // Hide the "my location" button.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Hide the "toolbar" buttons
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        // Hide zoom controls.
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        showUserLocation();

        // TODO use your coordinates
        // Just load downtown winnipeg until we get a location update.
//        stopsPresenter.loadBusStopsAroundCoordinates(null, null, null);

        // We're ready to do work.
        Log.v("DEBUG", "mapReady");

        onMapReadyListener.onMapReady();
    }

    /**
     * The user tapped on an info window for a stop/marker. Route the user to the schedule for that stop.
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {

        // Lookup the bus stop.
        BusStopViewModel busStop = busStopInfoWindowAdapter.getMarkerToBusStopHashMap().get(marker);

        appRouter.goToStopScheduleScreen(getActivity(), busStop);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void showUserLocation() {

        // If permission has not yet been granted, then ask the user.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Show the list of bus stops as markers in the map.
     *
     * @param busStops
     */
    @Override
    public void showBusStops(List<BusStopViewModel> busStops) {

        clearMap();

        // Display each bus stop in the map with their GPS coordinates. Also keep a HashMap of
        // markers for each bus stop so we can figure out which marker points to which bus stop.
        HashMap<Marker, BusStopViewModel> markerToKeyHashMap = new HashMap<>();

        // NOTE: while we're doing this, we're also calculating the bounds of all of our markers
        // so that we can figure out our zoom scale to fit all of the markers on screen.
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

        for (BusStopViewModel stop : busStops) {
            LatLng latLng = stop.getLatLng();

            if (latLng != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(String.valueOf(stop.getKey()))
                        .snippet(stop.getName())
                );
                markerToKeyHashMap.put(marker, stop);

                latLngBoundsBuilder.include(marker.getPosition());
            }
        }
        // Add our hashmap to our info window adapter. This is where we do the marker-to-bus-stop
        // lookup, and display the bus stop information when a marker is clicked.
        busStopInfoWindowAdapter.setMarkerToBusStopHashMap(markerToKeyHashMap);

        LatLngBounds bounds;

        // If we've drawn a circular "search" area, then zoom in on the map such that we fit the
        // entire circal within the view bounds.
        if (searchArea != null) {
            bounds = getLatLngBoundsOfCircle(searchArea.getCenter(), searchArea.getRadius());
        } else {
            // Else zoom to fit all of the markers within the view bounds.
            // Change the Camera zoom scale so that we can fit all markers within the view bounds.
            bounds = latLngBoundsBuilder.build();
        }
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    /**
     * gets the LatLngBounds of a circle given the center and radius of the circle.
     * See <a href="http://stackoverflow.com/questions/15319431/how-to-convert-a-latlng-and-a-radius-to-a-latlngbounds-in-android-google-maps-ap">this</a> Stack Overflow post.
     *
     * @param center
     * @param radius
     * @return
     */
    private LatLngBounds getLatLngBoundsOfCircle(LatLng center, double radius) {
        // See the stackoverflow post in te javadoc to understand what these constants really mean.
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    @Override
    public void errorLoadingBusStops(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
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
    }

    public interface OnMapReadyListener {

        void onMapReady();
    }
}
