package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.squareup.phrase.Phrase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopScheduleRepository;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopSchedulePresenter {

    @Inject
    BusStopScheduleRepository scheduleRepository;
    @Inject
    Context context;

    private ViewContract viewContract;
    private Disposable loadScheduleSubscription;

    public BusStopSchedulePresenter(AppComponent injector,
                                    ViewContract viewContract) {
        injector.injectInto(this);
        this.viewContract = viewContract;
    }

    public String generateMapImageUrl(Resources res, BusStopViewModel busStop) {
        double lat = busStop.getLatLng().latitude;
        double lng = busStop.getLatLng().longitude;

        return Phrase.from(res, R.string.static_map_url)
                .put("center", lat + "," + lng)
                .put("zoom", res.getInteger(R.integer.map_cover_image_zoom))
                .put("width", res.getDimensionPixelSize(R.dimen.map_cover_image_width))
                .put("height", res.getDimensionPixelSize(R.dimen.map_cover_image_height))
                .put("markers", "color:red|size:mid|" + lat + "," + lng)
                .put("key", res.getString(R.string.google_maps_key))
                .format()
                .toString();
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
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_schedule));
                        }
                );
    }

    // TODO move this into a util method
    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public interface ViewContract extends BaseViewContract {

        void showScheduledStops(List<ScheduledStopViewModel> scheduledStops);
    }
}
