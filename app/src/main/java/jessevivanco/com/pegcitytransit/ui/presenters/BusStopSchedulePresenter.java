package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
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
    private Disposable filteredListAssembler;

    public BusStopSchedulePresenter(AppComponent injector,
                                    ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    public void loadScheduleForBusStop(Long busStopKey, boolean fromRefresh) {
        DisposableUtil.dispose(loadScheduleSubscription);

        loadScheduleSubscription = scheduleRepository.getBusStopSchedule(busStopKey, preferencesRepository.isUsing24HourClock())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.showNewFullScheduled(null, null, null);
                    viewContract.showErrorMessage(null);
                    viewContract.showViewState(ViewState.LOADING);
                })
                .subscribe(
                        busStopScheduleViewModel -> {
                            if (busStopScheduleViewModel.getScheduledStops().size() == 0) {
                                viewContract.showErrorMessage(context.getString(R.string.no_schedule));
                                viewContract.showViewState(ViewState.ERROR);
                            } else {
                                viewContract.showNewFullScheduled(busStopScheduleViewModel.getScheduledStops(),
                                        busStopScheduleViewModel.getBusRoutes(),
                                        busStopScheduleViewModel.getQueryTime());
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
                            Log.e(TAG, "Error loading bus route.", throwable);
                        }
                );
    }

    /**
     * Iterates and returns the list of stops that have a
     */
    public void refreshFilteredList(List<ScheduledStopViewModel> fullStopList, List<BusRouteViewModel> busRoutes) {
        DisposableUtil.dispose(filteredListAssembler);

        boolean filterApplied = false;

        // Could probably throw this in the RX chain if we wanted.
        final SparseArray<BusRouteViewModel> routesMap = new SparseArray<>();
        for (BusRouteViewModel route : busRoutes) {
            routesMap.put(route.getNumber(), route);

            if (route.isFilterApplied()) {
                filterApplied = true;
            }
        }

        // If no filters are applied, then return the full stop list
        if (!filterApplied) {
            viewContract.showFilteredSchedule(fullStopList);
        } else {
            // Else iterate the list and only return the stops with the routes that we care about.
            filteredListAssembler = Observable.fromIterable(fullStopList)
                    // Check if the current stop's route has the filter flag raised in our routes map. If it does, then we can show this stop.
                    .filter(scheduledStopViewModel -> routesMap.get(scheduledStopViewModel.getRouteNumber()).isFilterApplied())
                    .toList()
                    .subscribe(
                            newFilteredList -> viewContract.showFilteredSchedule(newFilteredList)
                    );
        }
    }

    public void tearDown() {
        DisposableUtil.dispose(loadScheduleSubscription);
        DisposableUtil.dispose(saveBusStopSubscription);
        DisposableUtil.dispose(loadBusRouteSubscription);
        DisposableUtil.dispose(filteredListAssembler);
    }

    public interface ViewContract extends ErrorMessageViewContract, BaseListViewContract {

        void showNewFullScheduled(List<ScheduledStopViewModel> scheduledStops, List<BusRouteViewModel> busRoutes, String queryTime);

        void showFilteredSchedule(List<ScheduledStopViewModel> filteredList);

        void onBusRouteLoaded(BusRouteViewModel busRouteViewModel);
    }
}
