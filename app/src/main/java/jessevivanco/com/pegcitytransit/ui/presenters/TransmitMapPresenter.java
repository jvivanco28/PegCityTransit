package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class TransmitMapPresenter {

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

    private ViewContract viewContract;

    private Disposable subscription;

    public TransmitMapPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);

        this.viewContract = viewContract;

        // Load out default lat and long values.
        DEFAULT_LAT = Double.parseDouble(context.getString(R.string.downtown_winnipeg_latitude));
        DEFAULT_LONG = Double.parseDouble(context.getString(R.string.downtown_winnipeg_longitude));
        DEFAULT_RADIUS = context.getResources().getInteger(R.integer
                .default_map_search_radius);
    }

    public void loadBusStopsAroundCoordinates(@Nullable Double latitude, @Nullable Double longitude, @Nullable Integer radius) {

        dispose(subscription);

        // NOTE: If lat, long, and radius are not supplied, then we just resort to the default values.
        subscription = stopsRepository.getBusStopsNearLocation(
                latitude != null ? latitude : DEFAULT_LAT,
                longitude != null ? longitude : DEFAULT_LONG,
                radius != null ? radius : DEFAULT_RADIUS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busStops -> {
                            if (busStops != null && busStops.size() > 0) {
                                viewContract.showBusStops(busStops);
                            } else {
                                viewContract.showErrorMessage(context.getString(R.string.no_bus_stops_in_that_area));
                            }
                        },
                        throwable -> {
                            // TODO handle error
                            viewContract.showErrorMessage(context.getString(R.string.generic_error));
                        }
                );
    }

    public void loadBusRoutesForStop(@NonNull BusStopViewModel busStopFilter) {
        dispose(subscription);

        subscription = routesRepository.getRoutesForBusStop(busStopFilter.getKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> {
                            busStopFilter.setRoutes(busRoutes);
                            viewContract.showBusRoutesForStop(busStopFilter);
                        },
                        throwable -> viewContract.showErrorMessage(context.getString(R.string.error_loading_bus_routes))
                );
    }

    public void loadBusStopsForBusRoute(@NonNull BusRouteViewModel route) {
        dispose(subscription);

        subscription = stopsRepository.getBusStopsForRoute(route.getKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busStops -> {
                            if (busStops != null && busStops.size() > 0) {
                                viewContract.showBusStops(busStops);
                            } else {
                                viewContract.showErrorMessage(context.getString(R.string.no_bus_stops_in_that_area));
                            }
                        },
                        throwable -> {
                            // TODO handle error
                            viewContract.showErrorMessage(context.getString(R.string.generic_error));
                        }
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract extends BaseViewContract {

        void showBusStops(List<BusStopViewModel> busStops);

        void showBusRoutesForStop(BusStopViewModel busStop);

    }
}