package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.presenters.base.AdapterPresenter;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;

public class BusStopsAdapterPresenter implements AdapterPresenter<List<BusStop>> {

    private static final String STATE_KEY_LATITUDE = "STATE_KEY_LATITUDE";
    private static final String STATE_KEY_LONGITUDE = "STATE_KEY_LONGITUDE";
    private static final String STATE_KEY_RADIUS = "STATE_KEY_RADIUS";

    private final String LOG_TAG = getClass().getSimpleName();

    /**
     * Default lat and long will only be used if the user has GPS disabled.
     */
    private final double DEFAULT_LAT;
    private final double DEFAULT_LONG;
    private final int DEFAULT_RADIUS;

    @Inject
    BusStopRepository stopsRepository;
    @Inject
    BusRoutesRepository routesRepository;
    @Inject
    Context context;

    // Lat and Long might be null if we don't have GPS permission.
    private
    @Nullable
    Double latitude;
    private
    @Nullable
    Double longitude;
    private
    @Nullable
    Integer radius;

    public BusStopsAdapterPresenter(Context context, AppComponent injector) {
        injector.injectInto(this);

        // Load out default lat and long values.
        DEFAULT_LAT = Double.parseDouble(context.getString(R.string.default_lat));
        DEFAULT_LONG = Double.parseDouble(context.getString(R.string.default_long));

        DEFAULT_RADIUS = context.getResources().getInteger(R.integer
                .default_max_bus_stop_distance);
    }

    @Override
    public Observable<List<BusStop>> loadData() {
        // NOTE: If lat, long, and radius are not supplied, then we just resort to the default values.
        return stopsRepository.getBusStopsNearLocation(
                latitude != null ? latitude : DEFAULT_LAT,
                longitude != null ? longitude : DEFAULT_LONG,
                radius != null ? radius : DEFAULT_RADIUS)
                .toObservable();
    }

    /**
     * Loads the bus routes for each bus stop. Emits as each stop is populated with bus routes.
     *
     * @param stops
     * @return
     */
    public Observable<BusStop> getRoutesForBusStops(List<BusStop> stops) {
        return Observable.fromIterable(stops)
                .flatMap(busStop -> getRoutesForBusStop(busStop));
    }

    /**
     * Load the bus routes for the provided bus stop, then sets the routes list on the bus stop.
     *
     * @param stop
     * @return
     */
    private Observable<BusStop> getRoutesForBusStop(BusStop stop) {

        // TODO We should check the cache before sending a new API request.

        // Fetch the routes for the provided bus stop.
        return routesRepository.getRoutesForBusStop(stop.getNumber())
                .flatMap(busRoutes -> {

                    // Set the bus routes on the bus stop reference.
                    stop.setBusRoutes(busRoutes);
                    return Single.just(stop);
                }).toObservable();
    }

    @Override
    public Bundle onSaveInstanceState(Bundle outState) {

        if (latitude != null) {
            outState.putDouble(STATE_KEY_LATITUDE, latitude);
        }
        if (longitude != null) {
            outState.putDouble(STATE_KEY_LONGITUDE, longitude);
        }
        if (radius != null) {
            outState.putInt(STATE_KEY_RADIUS, radius);
        }
        return outState;
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null) {
            if (state.containsKey(STATE_KEY_LATITUDE)) {
                latitude = state.getDouble(STATE_KEY_LATITUDE);
            }
            if (state.containsKey(STATE_KEY_LONGITUDE)) {
                longitude = state.getDouble(STATE_KEY_LONGITUDE);
            }
            if (state.containsKey(STATE_KEY_RADIUS)) {
                radius = state.getInt(STATE_KEY_RADIUS);
            }
        }
    }

    /**
     * @return The long location of the user. If the long has not yet been recorded, then defaults to
     * <code>DEFAULT_LONG</code>.
     */
    public
    @NonNull
    Double getLongitude() {
        return longitude != null ? longitude : DEFAULT_LONG;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The lat location of the user. If the lat has not yet been recorded, then defaults to
     * <code>DEFAULT_LAT</code>.
     */
    public
    @NonNull
    Double getLatitude() {
        return latitude != null ? latitude : DEFAULT_LAT;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    /**
     * Returns <code>true</code> if current location is different than given <code>latitude</code> and
     * <code>longitude</code>.
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public boolean isDifferentLocation(@Nullable Double latitude, @Nullable Double longitude) {
        return latitude == null ||
                longitude == null ||
                !this.getLatitude().equals(latitude) ||
                !this.getLongitude().equals(longitude);
    }
}
