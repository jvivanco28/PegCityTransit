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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

import java.util.List;

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

    private static final String STATE_KEY_MAP_CAMERA = "camera_position";
    private static final String STATE_KEY_SEARCH_AREA = "search_area_circle";

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

    private
    @Nullable
    LatLng restoredSearchAreaCoordinates;

    private
    @Nullable
    CameraPosition restoredCameraPosition;

    public static TransitMapFragment newInstance(OnMapReadyListener onMapReadyListener) {

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

        setupMap(savedInstanceState);
        setupPresenters();
        setupAdapters(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_transit_map;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        busStopInfoWindowAdapter.onSaveInstanceState(outState);

        if (searchArea != null) {
            outState.putParcelable(STATE_KEY_SEARCH_AREA, searchArea.getCenter());
        }
        if (googleMap != null) {
            outState.putParcelable(STATE_KEY_MAP_CAMERA, googleMap.getCameraPosition());
        }
    }

    private void setupMap(Bundle savedInstanceState) {
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            restoredCameraPosition = savedInstanceState.getParcelable(STATE_KEY_MAP_CAMERA);
            restoredSearchAreaCoordinates = savedInstanceState.getParcelable(STATE_KEY_SEARCH_AREA);
        }
    }

    private void setupPresenters() {
        stopsPresenter = new BusStopsPresenter(injector, this);
        routesPresenter = new BusRoutesPresenter(injector, this);
    }

    private void setupAdapters(Bundle savedInstanceState) {
        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(getActivity(), routesPresenter, savedInstanceState);
    }

    /**
     * Clears all markers on the map.
     */
    private void clearMarkers() {
        // Clear markers, list, and filters.
        routesPresenter.setBusStopFilter(null);
        busStopInfoWindowAdapter.clearMarkers();
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
        showSearchAreaAtCoordinates(latitude, longitude, radius);

        // Load the new set of markers at the current camera position
        stopsPresenter.loadBusStopsAroundCoordinates(latitude, longitude, radius);
    }

    /**
     * Draws a circle on the map which represents the searched area for bus stops.
     *
     * @param latitude
     * @param longitude
     * @param radius
     */
    private void showSearchAreaAtCoordinates(Double latitude, Double longitude, int radius) {

        // Display the area that we're searching for bus stops.
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(radius)
                .strokeColor(getResources().getColor(R.color.map_search_border))
                .strokeWidth(getResources().getDimensionPixelSize(R.dimen.map_search_border_width));

        searchArea = googleMap.addCircle(circleOptions);
    }

    public void loadBusStopsForBusRoute(BusRouteViewModel route) {

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

        // Restore the camera position if we just changed orientation.
        if (restoredCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(restoredCameraPosition));
        } else {
            // Default coordinates if we don't have user's location permission.
            LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.downtown_winnipeg_latitude)), Double.valueOf(getString(R.string.downtown_winnipeg_longitude)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(downtownWinnipeg, getResources().getInteger(R.integer.default_city_wide_map_zoom)));
        }

        // Redraw the searched bus stop area if we just changed orientation
        if (restoredSearchAreaCoordinates != null) {
            showSearchAreaAtCoordinates(restoredSearchAreaCoordinates.latitude,
                    restoredSearchAreaCoordinates.longitude,
                    getResources().getInteger(R.integer.default_map_search_radius));
        }

        // Redraw bus stop markers if we changed orientation.
        if (busStopInfoWindowAdapter.getBusStops() != null) {
            showBusStops(busStopInfoWindowAdapter.getBusStops(), false);
        }

        googleMap.setInfoWindowAdapter(busStopInfoWindowAdapter);
        googleMap.setOnInfoWindowClickListener(this);

        // Hide the "my location" button.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Hide the "toolbar" buttons
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        // Hide zoom controls.
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        // Show the user's location on the map if we have permission.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
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
        BusStopViewModel busStop = busStopInfoWindowAdapter.getBusStopForMarker(marker);

        appRouter.goToStopScheduleScreen(getActivity(), busStop);
    }

    /**
     * Show the list of bus stops as markers in the map.
     *
     * @param busStops
     */
    @Override
    public void showBusStops(List<BusStopViewModel> busStops, boolean animateCamera) {

        clearMarkers();

        // Add the bus stops as markers to the map. We're given the bounding box of all of the
        // markers such that we can zoom to fit all markers on the map.
        LatLngBounds bounds = busStopInfoWindowAdapter.showBusStopsAsMarkers(googleMap, busStops);

        if (animateCamera) {

            // If we've drawn a circular "search" area, then zoom in on the map such that we fit the
            // entire circle within the view bounds.
            if (searchArea != null) {
                bounds = getLatLngBoundsOfCircle(searchArea.getCenter(), searchArea.getRadius());
            }
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        }
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

        // TODO get this working again.
        // TODO null check busstop?
        BusStopViewModel busStop = routesPresenter.getBusStopFilter();
        busStopInfoWindowAdapter.showRoutesAtBusStopInfoWindow(busStop, busRoutes);
    }

    @Override
    public void onLoadBusRoutesError(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }

    public interface OnMapReadyListener {

        void onMapReady();
    }
}
