package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

public class BusRoutesListPresenter {

    private static final String TAG = BusRoutesListPresenter.class.getSimpleName();

    @Inject
    BusRoutesRepository routesRepository;
    @Inject
    Context context;

    private Disposable loadBusRoutesSubscription;
    private ViewContract viewContract;

    public BusRoutesListPresenter(AppComponent injector, ViewContract viewContract) {
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
                        throwable -> viewContract.showErrorMessage(context.getString(R.string.error_loading_bus_routes))
                );
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract extends BaseViewContract {

        void showAllBusRoutes(List<BusRouteViewModel> busRoutes);
    }
}
