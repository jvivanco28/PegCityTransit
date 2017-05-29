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
import jessevivanco.com.pegcitytransit.data.util.DisposableUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;

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
        DisposableUtil.dispose(loadBusRoutesSubscription);

        loadBusRoutesSubscription = routesRepository.getAllBusRoutes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.showErrorMessage(null);
                    viewContract.showViewState(ViewState.LOADING);
                })
                .subscribe(
                        busRoutes -> {
                            viewContract.showAllBusRoutes(busRoutes);
                            viewContract.showViewState(ViewState.LIST);
                        },
                        throwable -> {
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_bus_routes));
                            viewContract.showViewState(ViewState.ERROR);
                        }
                );
    }

    public void tearDown() {
        DisposableUtil.dispose(loadBusRoutesSubscription);
    }

    public interface ViewContract extends ErrorMessageViewContract, BaseListViewContract {

        void showAllBusRoutes(List<BusRouteViewModel> busRoutes);
    }
}
