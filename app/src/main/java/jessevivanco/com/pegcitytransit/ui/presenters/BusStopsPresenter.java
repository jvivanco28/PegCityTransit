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
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopsPresenter {

    /**
     * Default lat and long will only be used if the user has GPS disabled.
     */
    private final double DEFAULT_LAT;
    private final double DEFAULT_LONG;
    private final int DEFAULT_RADIUS;

    @Inject
    BusStopRepository stopsRepository;
    @Inject
    Context context;

    private ViewContract viewContract;

    private Disposable loadBusStopsSubscription;

    public BusStopsPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);

        this.viewContract = viewContract;

        // Load out default lat and long values.
        DEFAULT_LAT = Double.parseDouble(context.getString(R.string.downtown_winnipeg_latitude));
        DEFAULT_LONG = Double.parseDouble(context.getString(R.string.downtown_winnipeg_longitude));
        DEFAULT_RADIUS = context.getResources().getInteger(R.integer
                .default_max_bus_stop_distance);
    }

    public void loadBusStopsAroundCoordinates(@Nullable Double latitude, @Nullable Double longitude, @Nullable Integer radius) {

        dispose(loadBusStopsSubscription);

        // NOTE: If lat, long, and radius are not supplied, then we just resort to the default values.
        loadBusStopsSubscription = stopsRepository.getBusStopsNearLocation(
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
                                viewContract.errorLoadingBusStops(context.getString(R.string.no_bus_stops_in_that_area));
                            }
                        },
                        throwable -> {
                            // TODO handle error
                            viewContract.errorLoadingBusStops(context.getString(R.string.generic_error));
                        }
                );
    }

    public void loadBusStopsForBusRoute(@NonNull BusRouteViewModel route) {
        dispose(loadBusStopsSubscription);

        loadBusStopsSubscription = stopsRepository.getBusStopsForRoute(route.getKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busStops -> {
                            if (busStops != null && busStops.size() > 0) {
                                viewContract.showBusStops(busStops);
                            } else {
                                viewContract.errorLoadingBusStops(context.getString(R.string.no_bus_stops_in_that_area));
                            }
                        },
                        throwable -> {
                            // TODO handle error
                            viewContract.errorLoadingBusStops(context.getString(R.string.generic_error));
                        }
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract {

        void showBusStops(List<BusStopViewModel> busStops);

        void errorLoadingBusStops(String message);
    }
}