package jessevivanco.com.pegcitytransit.data.repositories;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.StopSchedule;

public class BusStopScheduleRepository {

    private RestApi restApi;

    public BusStopScheduleRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<StopSchedule> getBusStopSchedule(Long busStopKey) {
        return restApi.getBusStopSchedule(busStopKey)
                .subscribeOn(Schedulers.io())
                .map(listWinnipegTransitResponse -> listWinnipegTransitResponse.getElement());
    }
}
