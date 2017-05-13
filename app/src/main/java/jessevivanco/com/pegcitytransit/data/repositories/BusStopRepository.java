package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.squareup.phrase.Phrase;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.data.util.CacheHelper;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopRepository {

    // Routes change from time to time.
    private static final int CACHE_EXPIRY = CacheManager.ExpiryTimes.ONE_DAY.asSeconds();

    private Context context;
    private RestApi restApi;
    @Nullable
    private CacheManager cacheManager;
    private final Type butStopsTypeToken;

    public BusStopRepository(Context context, RestApi restApi, @Nullable CacheManager cacheManager) {
        this.context = context;
        this.restApi = restApi;
        this.cacheManager = cacheManager;

        this.butStopsTypeToken = new TypeToken<List<BusStopViewModel>>() {
        }.getType();
    }

    public Single<List<BusStopViewModel>> getBusStopsNearLocation(Double latitude,
                                                                  Double longitude,
                                                                  @Nullable Integer radius) {

        return restApi.getBusStopsNearLocation(latitude, longitude, radius)
                .map(WinnipegTransitResponse::getElement)
                .flatMapObservable(Observable::fromIterable)
                .map(BusStopViewModel::createFromBusStop)
                .toList();

        // Notes for myself:
        // - Method references are the same as this:
        // .map(busStopsList -> busStopsList.getElement();
        // - the map() function just converts the previous return type to a new type.
    }

    public Single<List<BusStopViewModel>> getBusStopsForRoute(Long routeKey) {

        return Single.defer(() -> {

            final String CACHE_KEY = Phrase.from(context, R.string.cache_key_stops_for_route).put("route", String.valueOf(routeKey)).format().toString();

            // Grab the cached list of bus stops.
            List<BusStopViewModel> cachedBusStops = CacheHelper.getFromCache(cacheManager,
                    CACHE_KEY,
                    butStopsTypeToken);

            if (cachedBusStops != null) {
                return Single.just(cachedBusStops);
            } else {
                return restApi.getBusStopsForRoute(routeKey)
                        .map(WinnipegTransitResponse::getElement)
                        .flatMapObservable(Observable::fromIterable)
                        .map(BusStopViewModel::createFromBusStop)
                        .toList()
                        .doOnSuccess(busStopViewModels -> {
                            if (cacheManager != null) {
                                cacheManager.put(CACHE_KEY, busStopViewModels, CACHE_EXPIRY, false);
                            }
                        });
            }
        });
    }
}
