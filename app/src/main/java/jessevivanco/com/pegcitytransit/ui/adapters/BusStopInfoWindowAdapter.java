package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopInfoWindowAdapter {

    private static final String STATE_BUS_STOPS = "bus_stops";

    private List<BusStopViewModel> busStops;
    private Map<Marker, BusStopViewModel> markerToBusStopHashMap;

    public BusStopInfoWindowAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            busStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_BUS_STOPS));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_BUS_STOPS, Parcels.wrap(busStops));
    }

    public void clearMarkers() {
        if (markerToBusStopHashMap != null) {
            for (Marker marker : markerToBusStopHashMap.keySet()) {
                // Remove the marker from the map.
                marker.remove();
            }
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

    // TODO
    public void showInfoWindowForBusStop(BusStopViewModel busStop) {
//                                            List<BusRouteViewModel> busRoutes) {
        // Add the routes to the bus stop POJO, then re-open the marker for that bus stop.

        // FYI: We have to do a reverse lookup b/c we don't currently know the marker for the bus stop.
        for (Map.Entry<Marker, BusStopViewModel> entry : markerToBusStopHashMap.entrySet()) {
            if (busStop.getKey().equals(entry.getValue().getKey())) {

                // Found the target marker-stop pair. Attach the routes to the bus stop, refresh the
                // info window, then bust out of this loop.
//                entry.getValue().setRoutes(busRoutes);
                entry.getKey().showInfoWindow();
                break;
            }
        }
    }

    @Nullable
    public List<BusStopViewModel> getBusStops() {
        return busStops;
    }

    public BusStopViewModel getBusStopForMarker(Marker marker) {
        return markerToBusStopHashMap.get(marker);
    }
}
