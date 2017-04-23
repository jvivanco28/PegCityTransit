package jessevivanco.com.pegcitytransit.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusStopInfoWindow;

public class BusStopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = BusStopInfoWindowAdapter.class.getSimpleName();
    private static final String STATE_BUS_STOPS = "bus_stops";

    // Just re-use the same view and change its contents.
    private BusStopInfoWindow busStopInfoWindow;

    private BusRoutesPresenter busRoutesPresenter;

    private List<BusStopViewModel> busStops;
    private Map<Marker, BusStopViewModel> markerToBusStopHashMap;

    public BusStopInfoWindowAdapter(Context context, BusRoutesPresenter busRoutesPresenter, @Nullable Bundle savedInstanceState) {
        this.busStopInfoWindow = new BusStopInfoWindow(context);
        this.busRoutesPresenter = busRoutesPresenter;

        if ( savedInstanceState != null ) {
            busStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_BUS_STOPS));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_BUS_STOPS, Parcels.wrap(busStops));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Use the default window frame.
        return null;
    }

    /**
     * A marker has been clicked, so we show the bus stop information for that marker. Implicitly
     * loads the list of bus routes for that bus stop if {@link BusStop#getBusRoutes()} returns
     * <code>null</code> so that we can also display the routes.
     *
     * @param marker
     * @return
     */
    @Override
    public BusStopInfoWindow getInfoContents(Marker marker) {

        Log.v("DEBUG", "tapped marker");

        // Lookup the bus stop.
        BusStopViewModel busStop = markerToBusStopHashMap.get(marker);

        // Display the bus stop info
        busStopInfoWindow.showBusStopInfo(busStop);

        // If we don't have the routes loaded for this bus stop, then load and display them.
        if (busStop != null && busStop.getRoutes() == null) {
            busRoutesPresenter.setBusStopFilter(busStop);
            busRoutesPresenter.loadBusRoutes();
        } else {
            // TODO report this
            Log.e(TAG, "Error. No bus stop for selected marker!");
        }
        return busStopInfoWindow;
    }

    public void clearMarkers() {
        if (markerToBusStopHashMap != null) {
            markerToBusStopHashMap.forEach((marker, busStopViewModel) -> {
                marker.remove();
            });
            markerToBusStopHashMap.clear();
            markerToBusStopHashMap = null;
        }
    }

    /**
     * TODO DOCUMENT
     *
     * @param googleMap
     * @param busStops
     * @return
     */
    public LatLngBounds showBusStopsAsMarkers(GoogleMap googleMap, List<BusStopViewModel> busStops) {

        this.busStops = busStops;

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
        this.markerToBusStopHashMap = markerToKeyHashMap;

        return latLngBoundsBuilder.build();
    }

    /**
     * TODO DOC
     * @param busStop
     * @param busRoutes
     */
    public void showRoutesAtBusStopInfoWindow(BusStopViewModel busStop, List<BusRouteViewModel> busRoutes) {
        // Add the routes to the bus stop POJO, then re-open the marker for that bus stop.

        // FYI: We have to do a reverse lookup b/c we don't currently know the marker for the bus stop.
        for (Map.Entry<Marker, BusStopViewModel> entry : markerToBusStopHashMap.entrySet()) {
            if (busStop.getKey().equals(entry.getValue().getKey())) {

                // Found the target marker-stop pair. Attach the routes to the bus stop, refresh the
                // info window, then bust out of this loop.
                entry.getValue().setRoutes(busRoutes);
                entry.getKey().showInfoWindow();
                break;
            }
        }
    }

    public @Nullable List<BusStopViewModel> getBusStops() {
        return busStops;
    }

    public BusStopViewModel getBusStopForMarker(Marker marker) {
        return markerToBusStopHashMap.get(marker);
    }
}
