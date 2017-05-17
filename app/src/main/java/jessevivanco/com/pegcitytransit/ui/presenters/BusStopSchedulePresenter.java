package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.util.Log;

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
                    viewContract.showLoadingScheduleIndicator(true);
                })
                .doFinally(() -> viewContract.showLoadingScheduleIndicator(false))
                .subscribe(
                        scheduledStops -> {
                            // TODO handle nothing in schedule
                            viewContract.showScheduledStops(scheduledStops);
                        }, throwable -> {
                            // TODO handle error
                            Log.e("DEBUG", "wtf!", throwable);
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
                    Log.v("DEBUG", "SAVED. New list " + busStopViewModels);

                    // Ignored.
                }, throwable -> {
                    // TODO handle error
                    Log.e(TAG, "Error saving bus stop.", throwable);
                });
    }

    public void removeSavedBusStop(BusStopViewModel busStop) {
        DisposableUtil.dispose(saveBusStopSubscription);

        saveBusStopSubscription = busStopRepository.removeSavedStop(busStop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busStopViewModels -> {
                    // Ignored.
                    Log.v("DEBUG", "REMOVED. New list " + busStopViewModels);

                }, throwable -> {
                    // TODO handle error
                    Log.e(TAG, "Error saving bus stop.", throwable);
                });
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
