package jessevivanco.com.pegcitytransit.data.rest;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.data.rest.models.ScheduleStatus;
import jessevivanco.com.pegcitytransit.data.rest.models.StopSchedule;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Winnipeg Transits' open API.
 *
 * @see <a href="https://api.winnipegtransit.com/home/api/v2">https://api.winnipegtransit.com/home/api/v2</a>
 */
public interface RestApi {

    @GET("statuses/schedule.json")
    Single<ScheduleStatus> getScheduleStatus();

    @GET("stops/{id}.json")
    Single<BusStop> getStopInfo(@Path("id") long id);

    /**
     * Retrieves a list of bus stops at a location (given <code>longitude</code> and <code>latitude</code>) within
     * given <code>radius</code> in meters.
     *
     * @param latitude  The latitude of the point to find stops near. Use in conjunction with 'lon' and 'distance'.
     * @param longitude The longitude of the point to find stops near. Use in conjunction with 'lat' and 'distance'.
     * @param radius    The distance in metres from the given point which returned stops must fall within.
     * @return
     * @see <a href="https://api.winnipegtransit.com/home/api/v2/services/stops">https://api.winnipegtransit
     * .com/home/api/v2/services/stops</a>
     */
    @GET("stops.json")
    Single<WinnipegTransitResponse<List<BusStop>>> getBusStopsNearLocation(@Query("lat") Double latitude,
                                                                           @Query("lon") Double longitude,
                                                                           @Nullable @Query("distance") Integer radius);

    /**
     * Gets all bus stop for the provided bus route. We can use this info to display the entire
     * route on a map.
     *
     * @param busRouteKey
     * @return
     */
    @GET("stops.json")
    Single<WinnipegTransitResponse<List<BusStop>>> getBusStopsForRoute(@Query("route") Long busRouteKey);

    // TODO https://api.winnipegtransit.com/v2/stops/10064/features.json?api-key=Y8zxUk0g73hxmlkHoDh
    // getBusStopFeatures

    /**
     * Retrieves the but stop schedule for the given <code>busStopKey</code>. Each entry in the schedule
     * is an upcoming scheduled stop which is sorted by soonest to latest.
     *
     * @param busStopKey
     * @return
     */
    @GET("stops/{bus_stop}/schedule.json")
    Single<WinnipegTransitResponse<StopSchedule>> getBusStopSchedule(@Path("bus_stop") Long busStopKey);

    /**
     * Retrieves a list of bus routes for a given bus stop.
     *
     * @param busStop The bus stop we want to get the list of bus routes for.
     * @return
     * @see <a href="https://api.winnipegtransit.com/home/api/v2/services/routes">https://api.winnipegtransit
     * .com/home/api/v2/services/routes</a>
     */
    @GET("routes.json")
    Single<WinnipegTransitResponse<List<BusRoute>>> getRoutesForBusStop(@Query("stop") @NonNull Long busStop);

}
