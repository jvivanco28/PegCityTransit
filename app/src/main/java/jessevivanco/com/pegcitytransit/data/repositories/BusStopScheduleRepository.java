package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.ui.util.RouteCoverage;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopScheduleRepository {

    private Context context;
    private RestApi restApi;

    public BusStopScheduleRepository(Context context, RestApi restApi) {
        this.context = context;
        this.restApi = restApi;
    }

    public Single<List<ScheduledStopViewModel>> getBusStopSchedule(Long busStopKey) {

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
                                scheduledStop)
                        )
                )
                // Assemble into a list of view models.
                .toList();
    }
}
