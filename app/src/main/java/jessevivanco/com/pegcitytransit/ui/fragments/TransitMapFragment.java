package jessevivanco.com.pegcitytransit.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

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

import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.ui.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusStopInfoView;

// TODO Need to retain opened info window on orientation changes.
public class TransitMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.InfoWindowAdapter {

    private static final String STATE_KEY_MAP_CAMERA = "camera_position";
    private static final String STATE_KEY_SEARCH_AREA = "search_area_circle";

    private AppComponent injector;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;
    private TransitMapCallbacks transitMapCallbacks;

    // Just re-use the same view and change its contents.
    private BusStopInfoView busStopInfoWindow;

    private
    @Nullable
    Circle searchArea;

    private
    @Nullable
    LatLng restoredSearchAreaCoordinates;

    private
    @Nullable
    CameraPosition restoredCameraPosition;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        injector = ((PegCityTransitApp) getActivity().getApplication()).getInjector();
        injector.injectInto(this);

        setupMap(savedInstanceState);
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

        // We're re-using the same info window when tapping on a marker.
        busStopInfoWindow = new BusStopInfoView(getActivity(), BusStopInfoView.WidgetSize.SMALL);
    }

    private void setupAdapters(Bundle savedInstanceState) {
        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(savedInstanceState);
    }

    /**
     * Gets the LatLngBounds of a circle given the center and radius of the circle.
     * See <a href="http://stackoverflow.com/questions/15319431/how-to-convert-a-latlng-and-a-radius-to-a-latlngbounds-in-android-google-maps-ap">this</a> Stack Overflow post.
     *
     * @param center
     * @param radius
     * @return
     */
    private LatLngBounds getLatLngBoundsOfCircle(LatLng center, double radius) {
        // See the stackoverflow post in the javadoc to understand what these constants really mean.
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    /**
     * Clears all markers on the map.
     */
    public void clearMarkers() {
        busStopInfoWindowAdapter.clearMarkers();
    }

    public void setTransitMapCallbacks(TransitMapCallbacks transitMapCallbacks) {
        this.transitMapCallbacks = transitMapCallbacks;
    }

    public boolean isMapReady() {
        return googleMap != null;
    }

    public LatLng getCameraPosition() {
        return googleMap.getCameraPosition().target;
    }

    public void refreshBusStopInfoWindow(BusStopViewModel busStop) {
        busStopInfoWindowAdapter.showInfoWindowForBusStop(busStop);
    }

    /**
     * Draws a circle on the map representing a search radius for bus stops.
     */
    public void drawSearchRadius(@Nullable Double latitude, @Nullable Double longitude, int radius) {
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
    }

    /**
     * Show the list of bus stops as markers on the map.
     */
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

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        if (transitMapCallbacks == null) {
            throw new IllegalArgumentException("TransitMapCallbacks must not be null!");
        }

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
            drawSearchRadius(restoredSearchAreaCoordinates.latitude,
                    restoredSearchAreaCoordinates.longitude,
                    getResources().getInteger(R.integer.default_map_search_radius));
        }

        // Redraw bus stop markers if we changed orientation.
        if (busStopInfoWindowAdapter.getBusStops() != null) {
            showBusStops(busStopInfoWindowAdapter.getBusStops(), false);
        }

        googleMap.setInfoWindowAdapter(this);
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

        transitMapCallbacks.onMapReady();
    }

    /**
     * The user tapped on an info window for a bus stop/marker.
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        transitMapCallbacks.showBusStopSchedule(busStopInfoWindowAdapter.getBusStopForMarker(marker));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // We're just using the default window. We have out own layout for its contents though. See getInfoContents(Marker).
        return null;
    }

    /**
     * A marker was clicked. Get the {code BusStopViewModel} that maps to that specific marker, and
     * display its info along with which bus routes stop at the bus stop.
     */
    @Override
    public View getInfoContents(Marker marker) {
        BusStopViewModel busStop = busStopInfoWindowAdapter.getBusStopForMarker(marker);

        // Display the bus stop info
        busStopInfoWindow.showBusStopInfo(busStop);
        transitMapCallbacks.showBusStopSchedule(busStop);

        // If the bus stop doesn't have the routes loaded yet, then fetch them and refresh the info window.
        if (busStop.getRoutes() == null || busStop.getRoutes().size() == 0) {
            transitMapCallbacks.loadBusRoutesForStop(busStop);
        }
        return busStopInfoWindow;
    }

    // TODO DOCUMENT THIS
    public interface TransitMapCallbacks {

        void onMapReady();

        void showBusStopSchedule(BusStopViewModel busStopViewModel);

        void loadBusRoutesForStop(BusStopViewModel busStopViewModel);
    }
}
