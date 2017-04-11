package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusRouteMapActivity extends BaseActivity implements OnMapReadyCallback, BusStopsPresenter.ViewContract {

    public static final String ARG_KEY_BUS_ROUTE = "route";

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    private BusStopsPresenter stopsPresenter;
    private BusRoutesPresenter routesPresenter;
    private BusStopSchedulePresenter schedulePresenter;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInjector().injectInto(this);
        ButterKnife.bind(this);

        setupMap();
        setupPresenters();
        setupAdapters();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bus_route_map;
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    private void setupPresenters() {
        stopsPresenter = new BusStopsPresenter(getInjector(), this);
//        routesPresenter = new BusRoutesPresenter(getInjector(), this);
//        schedulePresenter = new BusStopSchedulePresenter(getInjector(), this);
    }

    private void setupAdapters() {
        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(this, routesPresenter);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        // Default coordinates if we don't have user's location permission.
        LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.downtown_winnipeg_latitude)), Double.valueOf(getString(R.string.downtown_winnipeg_longitude)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(downtownWinnipeg, getResources().getInteger(R.integer.default_city_wide_map_zoom)));
        googleMap.setInfoWindowAdapter(busStopInfoWindowAdapter);

        // Hide the "my location" button.
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        stopsPresenter.loadBusStopsForBusRoute(Parcels.unwrap(getIntent().getParcelableExtra(ARG_KEY_BUS_ROUTE)));
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

        // Change the Camera zoom scale so that we can fit all markers within the view bounds.
        LatLngBounds bounds = latLngBoundsBuilder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    @Override
    public void errorLoadingBusStops(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }
}
