package jessevivanco.com.pegcitytransit.repositories;

import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.rest.RestApi;
import jessevivanco.com.pegcitytransit.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.rest.models.base.WinnipegTransitResponse;

public class BusStopRepository {

    private RestApi restApi;

    public BusStopRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<List<BusStop>> getBusStopsNearLocation(Double latitude,
                                                         Double longitude,
                                                         @Nullable Integer radius) {

        return restApi.getBusStopsNearLocation(latitude, longitude, radius)
                .map(WinnipegTransitResponse::getElement);

        // ^FYI method reference above is the same as this
        // .map(busStopsList -> busStopsList.getBusStops();
        // ^FYI 2, the map() function just converts the previous return type to a new type.
        // In this case, we're converting from type BusStopList to type List<BusStop>.

    }
}
