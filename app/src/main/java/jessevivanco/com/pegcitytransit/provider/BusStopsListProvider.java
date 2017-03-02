package jessevivanco.com.pegcitytransit.provider;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.provider.base.ListProvider;
import jessevivanco.com.pegcitytransit.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.repositories.OnRepositoryDataRetrievedListener;
import jessevivanco.com.pegcitytransit.rest.RetrofitResponseUtils;
import jessevivanco.com.pegcitytransit.rest.models.BusStop;

public class BusStopsListProvider implements ListProvider<List<BusStop>> {

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

    // TODO DOC
    private boolean shouldLoadRoutesForStops;

    public BusStopsListProvider(Context context, AppComponent injector) {
        injector.injectInto(this);

        // Load out default lat and long values.
        DEFAULT_LAT = Double.parseDouble(context.getString(R.string.default_lat));
        DEFAULT_LONG = Double.parseDouble(context.getString(R.string.default_long));

        DEFAULT_RADIUS = context.getResources().getInteger(R.integer
                .default_max_bus_stop_distance);
    }

    @Override
    public void loadData(final OnRepositoryDataRetrievedListener<List<BusStop>> onDataRetrievedCallback) {

        // NOTE: if lat, long, and radius are not supplied, then we just resort to the default values.
        stopsRepository.getBusStopsNearLocation(
                latitude != null ? latitude : DEFAULT_LAT,
                longitude != null ? longitude : DEFAULT_LONG,
                radius != null ? radius : DEFAULT_RADIUS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busStops -> {

                    // Callback with the list of bus stops
                    handleFetchedBusStops(busStops, onDataRetrievedCallback);

                }, throwable -> {
                    // TODO Use different error message
                    onDataRetrievedCallback.onError(throwable.getMessage());
                });
    }

    /**
     * // TODO re-doc?
     * Calls back to the adapter with the fetched list of bus stops like we normally would. Also
     * loads the routes for each bus stop which invokes callbacks to the adapter.
     *
     * @param busStops
     * @param onDataRetrievedCallback
     */
    private void handleFetchedBusStops(List<BusStop> busStops, final OnRepositoryDataRetrievedListener<List<BusStop>> onDataRetrievedCallback) {

        // Callback like we normally would.
        onDataRetrievedCallback.onDataRetrieved(busStops);

        // Iterate each bus stop and load the routes for each one. Load from cache if possible.
        // NOTE: I'm using two subscribes here b/c the callbacks to the UI need to
        // happen on the main thread.
        Observable.fromIterable(busStops).subscribe(busStop -> {
            Log.v("DEBUG", "load routes for stop " + busStop.getName());
            loadRoutesForBusStop(onDataRetrievedCallback, busStop);
        }, throwable -> {

        }, () -> {
            // TODO probably toss this

        });
    }

    /**
     * Load the bus routes for a given bus stop.
     *
     * @param onDataRetrievedCallback
     * @param stop
     */
    private void loadRoutesForBusStop(final OnRepositoryDataRetrievedListener<List<BusStop>> onDataRetrievedCallback, BusStop stop) {

        // Todo check if routes exist in cache before fetching.
        routesRepository.getRoutesForBusStop(stop.getNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((busRoutes, throwable) -> {
                    // TODO
                    if ( throwable != null ) {
                        // TODO remove this
                        Log.v("DEBUG", "Error", throwable);
                    } else {
                        Log.v("DEBUG", "Got Routes for " + stop.getName() + " -> " + busRoutes);
                    }
                });
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
