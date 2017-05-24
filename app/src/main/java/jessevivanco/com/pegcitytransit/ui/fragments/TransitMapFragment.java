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

import org.parceler.Parcels;

import java.util.List;

import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.util.ScreenUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusStopInfoView;

public class TransitMapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.InfoWindowAdapter {

    private static final String STATE_KEY_MAP_CAMERA = "camera_position";
    private static final String STATE_KEY_SEARCH_AREA = "search_area_circle";
    private static final String STATE_KEY_SELECTED_BUS_STOP = "selected_stop";

    private static final double MARKER_PADDING_RATIO = 0.12;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;
    private TransitMapCallbacks transitMapCallbacks;

    // Just re-use the same view and change its contents.
    private BusStopInfoView busStopInfoWindow;

    // Keeping track of ths selected marker/bus stop so we can retain the opened info window on
    // orientation change.
    @Nullable
    private BusStopViewModel selectedBusStop;

    @Nullable
    private Circle searchArea;

    @Nullable
    private LatLng restoredSearchAreaCoordinates;

    @Nullable
    private CameraPosition restoredCameraPosition;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getInjector().injectInto(this);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        setupAdapters(savedInstanceState);
        setupMap(savedInstanceState);
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
        if (selectedBusStop != null) {
            outState.putParcelable(STATE_KEY_SELECTED_BUS_STOP, Parcels.wrap(selectedBusStop));
        }
    }

    private void setupMap(Bundle savedInstanceState) {
        mapFragment.getMapAsync(this);

        // We're re-using the same info window when tapping on a marker.
        busStopInfoWindow = new BusStopInfoView(getActivity());

        if (savedInstanceState != null) {
            restoredCameraPosition = savedInstanceState.getParcelable(STATE_KEY_MAP_CAMERA);
            restoredSearchAreaCoordinates = savedInstanceState.getParcelable(STATE_KEY_SEARCH_AREA);
            selectedBusStop = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_SELECTED_BUS_STOP));
        }
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

    /**
     * Shows an info window on the map for the provided bus stop.
     */
    public void showInfoWindowForBusStop(BusStopViewModel busStop) {
        busStopInfoWindowAdapter.showInfoWindowForBusStop(busStop);
    }

    /**
     * Shows the user's location on the map if we have permission to do so.
     */
    public void showUserLocationIfAllowed() {
        // Show the user's location on the map if we have permission.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Draws a circle on the map representing a search radius for bus stops.
     *
     * @param focusOnArea Pans the camera to focus on the circle that was drawn.
     */
    public void drawSearchRadius(@Nullable Double latitude, @Nullable Double longitude, int radius, boolean focusOnArea) {
        // Remove previous search area circle
        clearSearchRadius();

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
                .strokeColor(getResources().getColor(R.color.search_circle))
                .strokeWidth(getResources().getDimensionPixelSize(R.dimen.map_search_border_width));

        searchArea = googleMap.addCircle(circleOptions);

        if (focusOnArea) {
            zoomToBounds(getLatLngBoundsOfCircle(searchArea.getCenter(), searchArea.getRadius()));
        }
    }

    /**
     * Zooms to coordinates on the map.
     */
    public void zoomToLocation(double lat, double lng, float zoomScale) {

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoomScale));
    }

    /**
     * Zooms to the provided bounding box on the map. Zooms as close as possible to the bounding box
     * such that the entire box is still visible.
     */
    public void zoomToBounds(LatLngBounds bounds) {

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * MARKER_PADDING_RATIO); // offset from edges of the map 10% of screen

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    /**
     * Removes the search area circle if one was drawn.
     */
    public void clearSearchRadius() {
        if (searchArea != null) {
            searchArea.remove();
        }
        searchArea = null;
    }

    /**
     * Show the list of bus stops as markers on the map.
     *
     * @param busStops                    The list of bus stops to show as markers.
     * @param delayMarkerVisibilityMillis Number of milliseconds to delay when showing a marker as
     *                                    visible.
     * @param animateCamera               If set to {@code true} then pans & zooms the camera such that it can fit
     *                                    all markers on screen.
     */
    public void showBusStops(List<BusStopViewModel> busStops,
                             long delayMarkerVisibilityMillis,
                             boolean animateCamera) {

        clearMarkers();

        // Add the bus stops as markers to the map. We're given the bounding box of all of the
        // markers such that we can zoom to fit all markers on the map.
        LatLngBounds bounds = busStopInfoWindowAdapter.showBusStopsAsMarkers(googleMap, busStops, delayMarkerVisibilityMillis);

        if (animateCamera) {
            // If we've drawn a circular "search" area, then zoom in on the map such that we fit the
            // entire circle within the view bounds.
            if (searchArea != null) {
                bounds = getLatLngBoundsOfCircle(searchArea.getCenter(), searchArea.getRadius());
            }

            if (searchArea != null || busStops.size() > 1) {
                zoomToBounds(bounds);
            } else {
                // If there's only ony marker and no search area, then we definitely don't need to
                // zoom to MAX.
                zoomToLocation(busStops.get(0).getLatLng().latitude,
                        busStops.get(0).getLatLng().longitude,
                        getResources().getInteger(R.integer.default_my_location_map_zoom));
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        if (transitMapCallbacks == null) {
            throw new IllegalArgumentException("TransitMapCallbacks must not be null!");
        }
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnInfoWindowCloseListener(this);

        // TODO See if there is a better solution for this
        // The status bar occludes the compass unless we apply a top padding to the map view.
        // However, applying only a top padding will mess up the camera origin (it will be off-center)
        // so we also have to apply a bottom padding.
        // See https://stackoverflow.com/questions/15043006/how-to-move-the-android-google-maps-api-compass-position
        int statusBarHeight = ScreenUtil.getStatusBarHeight(getContext());
        googleMap.setPadding(0, statusBarHeight, 0, statusBarHeight);

        // Hide the "my location" button.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Hide the "toolbar" buttons
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        // Hide zoom controls.
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        showUserLocationIfAllowed();

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
                    getResources().getInteger(R.integer.default_map_search_radius),
                    false);
        }
        // Now that the map is ready, we can restore the state of the map as it was before the
        // the orientation change.
        if (busStopInfoWindowAdapter.getBusStops() != null) {
            showBusStops(busStopInfoWindowAdapter.getBusStops(), 0, false);

            // If we were displaying an info window prior to orientation change, then re-show that window.
            if (selectedBusStop != null) {
                showInfoWindowForBusStop(selectedBusStop);
            }
        }
        transitMapCallbacks.onMapReady();
    }

    /**
     * A marker was clicked. Get the {code BusStopViewModel} that maps to that specific marker and
     * load the upcoming schedule for that stop.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        BusStopViewModel busStop = busStopInfoWindowAdapter.getBusStopForMarker(marker);

        transitMapCallbacks.showBusStopSchedule(busStop);

        // If the bus stop doesn't have the routes loaded yet, then fetch them and refresh the info window.
        if (busStop.getRoutes() == null || busStop.getRoutes().size() == 0) {
            transitMapCallbacks.loadBusRoutesForStop(busStop);
        }
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // We're just using the default window. We have our own layout for its contents though. See getInfoContents(Marker).
        return null;
    }

    /**
     * We're about to display the info window for a clicked marker. Get the {code BusStopViewModel}
     * that maps to that specific marker, and display the bus stop information.
     */
    @Override
    public View getInfoContents(Marker marker) {
        BusStopViewModel busStop = busStopInfoWindowAdapter.getBusStopForMarker(marker);

        // Keep track of the selected bus stop just so we can retain the state of an open info window.
        selectedBusStop = busStop;

        // Display the bus stop info
        busStopInfoWindow.showBusStopInfo(busStop);

        return busStopInfoWindow;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        // We closed the info window, so we don't care about this selected bus stop anymore.
        selectedBusStop = null;
    }

    /**
     * The user tapped on an info window for a bus stop/marker.
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        transitMapCallbacks.showBusStopSchedule(busStopInfoWindowAdapter.getBusStopForMarker(marker));
    }

    // TODO DOCUMENT THIS
    public interface TransitMapCallbacks {

        void onMapReady();

        void showBusStopSchedule(BusStopViewModel busStopViewModel);

        void loadBusRoutesForStop(BusStopViewModel busStopViewModel);
    }
}
