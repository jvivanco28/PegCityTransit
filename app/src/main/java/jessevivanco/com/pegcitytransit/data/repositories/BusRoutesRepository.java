package jessevivanco.com.pegcitytransit.data.repositories;

import java.util.List;

import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;


public class BusRoutesRepository {

    private RestApi restApi;

    public BusRoutesRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<List<BusRoute>> getRoutesForBusStop(Integer busStop) {
        return restApi.getRoutesForBusStop(busStop)
                .map(WinnipegTransitResponse::getElement);
    }
}
