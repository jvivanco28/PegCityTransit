package jessevivanco.com.pegcitytransit.ui.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.data.util.DisposableUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopInfoWindowAdapter {

    private static final String STATE_BUS_STOPS = "bus_stops";
    private static final int MARKER_VISIBILITY_DELAY_MILLIS = 50;

    private List<BusStopViewModel> busStops;
    private Map<Marker, BusStopViewModel> markerToBusStopHashMap;
    private Disposable showMarkerVisibilitySubscription;

    public BusStopInfoWindowAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            busStops = Parcels.unwrap(savedInstanceState.getParcelable(STATE_BUS_STOPS));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_BUS_STOPS, Parcels.wrap(busStops));
    }

    public void onDestroy() {
        DisposableUtil.dispose(showMarkerVisibilitySubscription);
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
     * Takes a list of bus stops and displays them in the map.
     *
     * @return The camera bounds that surrounds all markers displayed on the map. Used so that we
     * can zoom in on the map as much as possible while still keeping all markers visible on screen.
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

                        // Don't display the markers just yet. We'll show them in a cascade effect
                        // once we have the full list of markers.
                        .visible(false)
                );
                markerToKeyHashMap.put(marker, stop);
                latLngBoundsBuilder.include(marker.getPosition());
            }
        }

        // Add our hashmap to our info window adapter. This is where we do the marker-to-bus-stop
        // lookup, and display the bus stop information when a marker is clicked.
        this.markerToBusStopHashMap = markerToKeyHashMap;

        // Now display all markers.
        cascadeMarkerVisibility(true);

        return latLngBoundsBuilder.build();
    }

    /**
     * Cascades the visibility of all markers within {@code markerToBusStopHashMap}.
     */
    private void cascadeMarkerVisibility(boolean visible) {
        DisposableUtil.dispose(showMarkerVisibilitySubscription);

        if (markerToBusStopHashMap != null) {
            final ArrayList<Marker> markerList = new ArrayList<>(markerToBusStopHashMap.keySet());

            showMarkerVisibilitySubscription = Observable.interval(MARKER_VISIBILITY_DELAY_MILLIS, MARKER_VISIBILITY_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                    .map(i -> markerList.get(i.intValue()))
                    .take(markerList.size())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            marker -> marker.setVisible(visible),
                            throwable -> {
                                // TODO report crash
                                Log.e("DEBUG", "asdf", throwable);
                            }
                    );
        }
    }

    public void showInfoWindowForBusStop(BusStopViewModel busStop) {

        // FYI: We have to do a reverse lookup b/c we don't currently know the marker for the bus stop.
        for (Map.Entry<Marker, BusStopViewModel> entry : markerToBusStopHashMap.entrySet()) {
            if (busStop.getKey().equals(entry.getValue().getKey())) {

                // Found the target marker-stop pair. Show the info window then bust out of here.
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
