package jessevivanco.com.pegcitytransit.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.views.BusStopInfoWindow;

public class BusStopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    // Just re-use the same view and change its contents.
    private BusStopInfoWindow busStopInfoWindow;

    private BusRoutesPresenter busRoutesPresenter;

    private HashMap<Marker, BusStop> markerToBusStopHashMap = new HashMap<>();

    public BusStopInfoWindowAdapter(Context context, AppComponent injector) {
        this.busStopInfoWindow = new BusStopInfoWindow(context);
        this.busRoutesPresenter = new BusRoutesPresenter(injector);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Use the default window frame.
        return null;
    }

    @Override
    public BusStopInfoWindow getInfoContents(Marker marker) {

        BusStop busStop = markerToBusStopHashMap.get(marker);

        // Display generic stop info
        busStopInfoWindow.showBusStopInfo(busStop);

        // If we don't have the routes loaded for this bus stop, then load and display them.
        if (busStop != null && busStop.getBusRoutes() == null) {
            busRoutesPresenter.loadBusRoutes(busStop.getKey(), new BusRoutesPresenter.ViewContract() {

                @Override
                public void showBusRoutes(List<BusRoute> busRoutes) {
                    // Set the routes on the bus stop, then re-load the info window.
                    busStop.setBusRoutes(busRoutes);

                    // NOTE: This will cause getInfoContents(Marker) to be called again.
                    marker.showInfoWindow();
                }

                @Override
                public void onLoadBusRoutesError() {

                    // Just set an empty list so we don't get stuck in loop loading the routes.
                    busStop.setBusRoutes(new ArrayList<>());

                    // NOTE: This will cause getInfoContents(Marker) to be called again.
                    marker.showInfoWindow();
                }
            });
        }
        return busStopInfoWindow;
    }

    public
    @Nullable
    Marker getMarkerForBusStop(BusStop busStop) {
        // FYI There is no reverse hashmap lookup.
        if (markerToBusStopHashMap != null) {

            for (Marker m : markerToBusStopHashMap.keySet()) {

                if (markerToBusStopHashMap.get(m).getKey().equals(busStop.getKey())) {
                    return m;
                }
            }
        }
        return null;
    }

    public void setMarkerToBusStopHashMap(HashMap<Marker, BusStop> markerToBusStopHashMap) {
        this.markerToBusStopHashMap = markerToBusStopHashMap;
    }
}
