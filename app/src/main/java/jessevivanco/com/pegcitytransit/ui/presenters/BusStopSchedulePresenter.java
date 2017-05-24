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
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopScheduleRepository;
import jessevivanco.com.pegcitytransit.data.util.DisposableUtil;
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
    Context context;

    private ViewContract viewContract;
    private Disposable loadScheduleSubscription;
    private Disposable saveBusStopSubscription;

    public BusStopSchedulePresenter(AppComponent injector,
                                    ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    public void loadScheduleForBusStop(Long busStopKey) {
        DisposableUtil.dispose(loadScheduleSubscription);

        loadScheduleSubscription = scheduleRepository.getBusStopSchedule(busStopKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.showScheduledStops(null);
                    viewContract.showErrorMessage(null);
                    viewContract.showLoadingScheduleIndicator(true);
                })
                .doFinally(() -> viewContract.showLoadingScheduleIndicator(false))
                .subscribe(
                        scheduledStops -> {
                            if (scheduledStops.size() == 0) {
                                viewContract.showErrorMessage(context.getString(R.string.no_schedule));
                            } else {
                                viewContract.showScheduledStops(scheduledStops);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "Error loading bus stop schedule", throwable);
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_schedule));
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

    public void tearDown() {
        DisposableUtil.dispose(loadScheduleSubscription);
        DisposableUtil.dispose(saveBusStopSubscription);
    }

    public interface ViewContract extends BaseViewContract {

        void showLoadingScheduleIndicator(boolean visible);

        void showScheduledStops(List<ScheduledStopViewModel> scheduledStops);
    }
}
