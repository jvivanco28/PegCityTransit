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
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

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
    BusStopViewModel busStopFilter;

    public BusRoutesPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    @Nullable
    public BusStopViewModel getBusStopFilter() {
        return busStopFilter;
    }

    public void setBusStopFilter(@Nullable BusStopViewModel busStopFilter) {
        this.busStopFilter = busStopFilter;
    }

    public void loadBusRoutes() {
        dispose(loadBusRoutesSubscription);

        loadBusRoutesSubscription = routesRepository.getRoutesForBusStop(busStopFilter != null ? busStopFilter.getKey() : null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> viewContract.showBusRoutes(busRoutes),
                        throwable -> viewContract.onLoadBusRoutesError(context.getString(R.string.error_loading_bus_routes))
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract {

        void showBusRoutes(List<BusRouteViewModel> busRoutes);

        void onLoadBusRoutesError(String message);
    }
}
