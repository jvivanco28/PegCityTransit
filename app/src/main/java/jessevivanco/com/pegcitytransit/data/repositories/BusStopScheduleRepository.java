package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.RouteSchedule;
import jessevivanco.com.pegcitytransit.ui.util.RouteCoverage;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopScheduleViewModel;
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

    public Single<BusStopScheduleViewModel> getBusStopSchedule(Long busStopKey, boolean use24HourTime) {

        // Fetch the bus stop schedule
        return restApi.getBusStopSchedule(busStopKey)
                .flatMap(winnipegTransitResponse -> {

                            final List<RouteSchedule> routeSchedules = winnipegTransitResponse.getElement().getRouteSchedules();

                            // Sort the route schedules by route number.
                            Collections.sort(routeSchedules, (o1, o2) -> o1.getRoute().getNumber().compareTo(o2.getRoute().getNumber()));

                            // We're keeping a list of each bus route that we iterate.
                            final List<BusRouteViewModel> busRouteViewModels = new ArrayList<>();

                            // Iterate all routes...
                            return Observable.fromIterable(routeSchedules)
                                    .flatMap(routeSchedule -> {

                                                // Add this bus route to our routes list.
                                                busRouteViewModels.add(BusRouteViewModel.createFromBusRoute(routeSchedule.getRoute()));

                                                // ... and iterate each scheduled stop...
                                                return Observable.fromIterable(routeSchedule.getScheduledStops())
                                                        // ... and convert the scheduled stop into a view model.
                                                        .map(scheduledStop -> ScheduledStopViewModel.createFromRouteSchedule(
                                                                context,
                                                                routeSchedule.getRoute().getNumber(),
                                                                RouteCoverage.getCoverage(routeSchedule.getRoute().getCoverage(), routeSchedule.getRoute().getName()),
                                                                scheduledStop,
                                                                MAX_RELATIVE_MINUTES,
                                                                use24HourTime,
                                                                winnipegTransitResponse.getQueryTime())
                                                        );
                                            }
                                    )
                                    // Assemble into a list of view models sorted by departure time
                                    .toSortedList((stop1, stop2) -> stop1.getDepartureTime().compareTo(stop2.getDepartureTime()))

                                    // Finally, create our view model.
                                    .map(scheduledStopViewModels -> new BusStopScheduleViewModel(scheduledStopViewModels,
                                            busRouteViewModels,
                                            winnipegTransitResponse.getQueryTime(),
                                            use24HourTime,
                                            context)
                                    );
                        }
                );

    }
}
