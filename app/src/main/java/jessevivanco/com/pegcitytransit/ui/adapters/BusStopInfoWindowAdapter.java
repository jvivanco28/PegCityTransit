package jessevivanco.com.pegcitytransit.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.presenters.BusRoutesPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusStopInfoWindow;

public class BusStopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = BusStopInfoWindowAdapter.class.getSimpleName();

    // Just re-use the same view and change its contents.
    private BusStopInfoWindow busStopInfoWindow;

    private BusRoutesPresenter busRoutesPresenter;

    private HashMap<Marker, BusStopViewModel> markerToBusStopHashMap = new HashMap<>();

    public BusStopInfoWindowAdapter(Context context, BusRoutesPresenter busRoutesPresenter) {
        this.busStopInfoWindow = new BusStopInfoWindow(context);
        this.busRoutesPresenter = busRoutesPresenter;
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

    public void setMarkerToBusStopHashMap(HashMap<Marker, BusStopViewModel> markerToBusStopHashMap) {
        this.markerToBusStopHashMap = markerToBusStopHashMap;
    }

    public HashMap<Marker, BusStopViewModel> getMarkerToBusStopHashMap() {
        return markerToBusStopHashMap;
    }
}
