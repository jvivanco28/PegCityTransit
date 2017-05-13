package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

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

    public BusRoutesPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    public void loadAllBusRoutes() {
        dispose(loadBusRoutesSubscription);

        loadBusRoutesSubscription = routesRepository.getAllBusRoutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> viewContract.showAllBusRoutes(busRoutes),
                        throwable -> viewContract.onLoadBusRoutesError(context.getString(R.string.error_loading_bus_routes))
                );
    }


    public void loadBusRoutesForStop(@NonNull BusStopViewModel busStopFilter) {
        dispose(loadBusRoutesSubscription);

        loadBusRoutesSubscription = routesRepository.getRoutesForBusStop(busStopFilter.getKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> viewContract.showBusRoutesForStop(busRoutes),
                        throwable -> viewContract.onLoadBusRoutesError(context.getString(R.string.error_loading_bus_routes))
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract {

        void showAllBusRoutes(List<BusRouteViewModel> busRoutes);

        void showBusRoutesForStop(List<BusRouteViewModel> busRoutes);

        void onLoadBusRoutesError(String message);
    }
}
