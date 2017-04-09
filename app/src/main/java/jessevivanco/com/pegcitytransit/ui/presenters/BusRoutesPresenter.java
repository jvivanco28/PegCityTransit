package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;

public class BusRoutesPresenter {

    @Inject
    BusRoutesRepository routesRepository;
    @Inject
    Context context;

    private Disposable loadBusRoutesSubscription;

    public BusRoutesPresenter(AppComponent injector) {
        injector.injectInto(this);
    }

    public void loadBusRoutes(@NonNull Long busStopKey, ViewContract viewContract) {
        dispose(loadBusRoutesSubscription);

        loadBusRoutesSubscription = routesRepository.getRoutesForBusStop(busStopKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> viewContract.showBusRoutes(busRoutes),
                        throwable -> viewContract.onLoadBusRoutesError()
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract {

        void showBusRoutes(List<BusRoute> busRoutes);

        void onLoadBusRoutesError();
    }
}
