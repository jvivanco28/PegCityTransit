package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
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
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;

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
    BusRoutesRepository routesRepository;
    @Inject
    Context context;

    private ViewContract viewContract;
    private Disposable loadDataSubscription;

    public BusStopsPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);

        this.viewContract = viewContract;

        // Load out default lat and long values.
        DEFAULT_LAT = Double.parseDouble(context.getString(R.string.default_lat));
        DEFAULT_LONG = Double.parseDouble(context.getString(R.string.default_long));
        DEFAULT_RADIUS = context.getResources().getInteger(R.integer
                .default_max_bus_stop_distance);
    }

    public void loadBusStops(@Nullable Double latitude, @Nullable Double longitude, @Nullable Integer radius) {

        if (loadDataSubscription != null && !loadDataSubscription.isDisposed()) {
            loadDataSubscription.dispose();
        }

        // NOTE: If lat, long, and radius are not supplied, then we just resort to the default values.
        loadDataSubscription = stopsRepository.getBusStopsNearLocation(
                latitude != null ? latitude : DEFAULT_LAT,
                longitude != null ? longitude : DEFAULT_LONG,
                radius != null ? radius : DEFAULT_RADIUS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busStops -> viewContract.showBusStops(busStops),
                        throwable -> {
                            // TODO handle error
                            viewContract.showMessage(context.getString(R.string.generic_error));
                        }
                );
    }

    public interface ViewContract {

        void showBusStops(List<BusStop> busStops);

        void showMessage(String message);
    }
}