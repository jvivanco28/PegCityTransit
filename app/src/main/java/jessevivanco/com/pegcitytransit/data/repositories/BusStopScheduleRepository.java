package jessevivanco.com.pegcitytransit.data.repositories;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopScheduleRepository {

    private RestApi restApi;

    public BusStopScheduleRepository(RestApi restApi) {
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
                        .map(scheduledStop -> ScheduledStopViewModel.createFromRouteSchedule(routeSchedule.getRoute().getNumber(), scheduledStop)))
                // Assemble into a list of view models.
                .toList();
    }
}
