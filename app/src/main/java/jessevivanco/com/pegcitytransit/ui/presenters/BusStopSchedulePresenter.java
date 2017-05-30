package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopScheduleRepository;
import jessevivanco.com.pegcitytransit.data.repositories.PreferencesRepository;
import jessevivanco.com.pegcitytransit.data.util.DisposableUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.BusStopScheduleBottomSheet;

public class BusStopSchedulePresenter {

    private static final String TAG = BusStopSchedulePresenter.class.getSimpleName();

    @Inject
    BusStopScheduleRepository scheduleRepository;
    @Inject
    BusStopRepository busStopRepository;
    @Inject
    BusRoutesRepository busRoutesRepository;
    @Inject
    PreferencesRepository preferencesRepository;
    @Inject
    Context context;

    private ViewContract viewContract;
    private Disposable loadScheduleSubscription;
    private Disposable saveBusStopSubscription;
    private Disposable loadBusRouteSubscription;

    public BusStopSchedulePresenter(AppComponent injector,
                                    ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    public void loadScheduleForBusStop(Long busStopKey) {
        DisposableUtil.dispose(loadScheduleSubscription);

        loadScheduleSubscription = scheduleRepository.getBusStopSchedule(busStopKey, preferencesRepository.isUsing24HourClock())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.setScheduledStops(null);
                    viewContract.showErrorMessage(null);
                    viewContract.showViewState(ViewState.LOADING);
                })
                .subscribe(
                        scheduledStops -> {
                            if (scheduledStops.size() == 0) {
                                viewContract.showErrorMessage(context.getString(R.string.no_schedule));
                                viewContract.showViewState(ViewState.ERROR);
                            } else {
                                viewContract.setScheduledStops(scheduledStops);
                                viewContract.showViewState(ViewState.LIST);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "Error loading bus stop schedule", throwable);
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_schedule));
                            viewContract.showViewState(ViewState.ERROR);
                        }
                );
    }

    public void saveBusStop(BusStopViewModel busStop) {
        DisposableUtil.dispose(saveBusStopSubscription);

        saveBusStopSubscription = busStopRepository.saveBusStop(busStop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busStopViewModels -> {
                    // Ignored.
                }, throwable -> {
                    Crashlytics.logException(throwable);
                    Log.e(TAG, "Error saving bus stop.", throwable);
                });
    }

    public void removeSavedBusStop(BusStopViewModel busStop, BusStopScheduleBottomSheet.OnFavStopRemovedListener onFavStopRemovedListener) {
        DisposableUtil.dispose(saveBusStopSubscription);

        if (onFavStopRemovedListener == null) {
            throw new IllegalStateException("OnFavStopRemovedListener must not be null");
        }

        saveBusStopSubscription = busStopRepository.removeSavedStop(busStop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busStopViewModels -> {
                            busStop.setSavedStop(false);
                            onFavStopRemovedListener.onFavStopRemoved(busStop);
                        },
                        throwable -> {
                            Crashlytics.logException(throwable);
                            Log.e(TAG, "Error saving bus stop.", throwable);
                        }
                );
    }

    public void loadBusRoute(Long busRouteKey) {
        DisposableUtil.dispose(loadBusRouteSubscription);

        loadBusRouteSubscription = busRoutesRepository.getBusRoute(busRouteKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRouteViewModel -> viewContract.onBusRouteLoaded(busRouteViewModel),
                        throwable -> {
                            Crashlytics.logException(throwable);
                            Log.e(TAG, "Errow loading bus route.", throwable);
                        }
                );
    }

    public void tearDown() {
        DisposableUtil.dispose(loadScheduleSubscription);
        DisposableUtil.dispose(saveBusStopSubscription);
        DisposableUtil.dispose(loadBusRouteSubscription);
    }

    public interface ViewContract extends ErrorMessageViewContract, BaseListViewContract {

        void setScheduledStops(List<ScheduledStopViewModel> scheduledStops);

        void onBusRouteLoaded(BusRouteViewModel busRouteViewModel);
    }
}
