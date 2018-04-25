package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.ui.util.RouteCoverage;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopScheduleRepository {

    private final int MAX_RELATIVE_MINUTES;

    private Context context;
    private RestApi restApi;

    public BusStopScheduleRepository(Context context, RestApi restApi) {
        this.context = context;
        this.restApi = restApi;
        this.MAX_RELATIVE_MINUTES = context.getResources().getInteger(R.integer.max_relative_minutes);
    }

    public Single<List<ScheduledStopViewModel>> getBusStopSchedule(Long busStopKey, boolean use24HourTime) {

        return restApi.getBusStopSchedule(busStopKey)
                .map(WinnipegTransitResponse::getElement)
                // Iterate all routes...
                .flatMapObservable(stopSchedule -> Observable.fromIterable(stopSchedule.getRouteSchedules()))
                // ... and each scheduled stop...
                .flatMap(routeSchedule -> Observable.fromIterable(routeSchedule.getScheduledStops())
                        // ... and convert the scheduled stop into a view model.
                        .map(scheduledStop -> ScheduledStopViewModel.createFromRouteSchedule(
                                context,
                                routeSchedule.getRoute().getNumber(),
                                RouteCoverage.getCoverage(routeSchedule.getRoute().getCoverage(), routeSchedule.getRoute().getName()),
                                scheduledStop,
                                MAX_RELATIVE_MINUTES,
                                use24HourTime)
                        )
                )
                // Assemble into a list of view models sorted by departure time
                .toSortedList((stop1, stop2) -> stop1.getDepartureTime().compareTo(stop2.getDepartureTime()));
    }
}
