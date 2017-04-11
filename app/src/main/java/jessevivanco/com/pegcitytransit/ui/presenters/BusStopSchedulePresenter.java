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
import jessevivanco.com.pegcitytransit.data.repositories.BusStopScheduleRepository;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopSchedulePresenter {

    @Inject
    BusStopScheduleRepository scheduleRepository;
    @Inject
    Context context;

    private ViewContract viewContract;

    private Disposable loadScheduleSubscription;

    public BusStopSchedulePresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);

        this.viewContract = viewContract;
    }

    // TODO we might want to convert the schedule into a list of scheduled stops
    public void loadScheduleForBusStop(Long busStopKey) {
        dispose(loadScheduleSubscription);

        loadScheduleSubscription = scheduleRepository.getBusStopSchedule(busStopKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        scheduledStops -> {
                            // TODO handle nothing in schedule
                            viewContract.showScheduledStops(scheduledStops);
                        }, throwable -> {
                            // TODO handle error
                            Log.e("DEBUG", "wtf!", throwable);
                            viewContract.showErrorLoadingScheduleMessage(context.getString(R.string.error_loading_schedule));
                        }
                );
    }

    // TODO move this into a util method
    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract {

        void showScheduledStops(List<ScheduledStopViewModel> scheduledStops);

        void showErrorLoadingScheduleMessage(String message);
    }
}
