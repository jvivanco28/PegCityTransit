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
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;

public class BusRoutesPresenter {

    private static final String TAG = BusRoutesPresenter.class.getSimpleName();

    @Inject
    BusRoutesRepository routesRepository;
    @Inject
    Context context;

    private Disposable loadBusRoutesSubscription;
    private ViewContract viewContract;

    private
    @Nullable
    BusStop busStopFilter;

    public BusRoutesPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    @Nullable
    public BusStop getBusStopFilter() {
        return busStopFilter;
    }

    public void setBusStopFilter(@Nullable BusStop busStopFilter) {
        this.busStopFilter = busStopFilter;
    }

    public void loadBusRoutes() {
        dispose(loadBusRoutesSubscription);

        loadBusRoutesSubscription = routesRepository.getRoutesForBusStop(busStopFilter != null ? busStopFilter.getKey() : null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> viewContract.showBusRoutes(busRoutes, busStopFilter),
                        throwable -> viewContract.onLoadBusRoutesError(context.getString(R.string.error_loading_bus_routes))
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract {

        // TODO prob remove the second arg
        void showBusRoutes(List<BusRoute> busRoutes, @Nullable BusStop busStop);

        void onLoadBusRoutesError(String message);
    }
}
