package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.squareup.phrase.Phrase;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    private static final String CACHE_KEY_SAVED_STOPS = "saved_stops";

    private Context context;
    private RestApi restApi;
    @Nullable
    private CacheManager cacheManager;
    private final Type busStopsTypeToken;

    public BusStopRepository(Context context, RestApi restApi, @Nullable CacheManager cacheManager) {
        this.context = context;
        this.restApi = restApi;
        this.cacheManager = cacheManager;
        this.busStopsTypeToken = new TypeToken<List<BusStopViewModel>>() {
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
                    busStopsTypeToken);

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

    public Single<List<BusStopViewModel>> getSavedBusStops() {
        return Single.defer(() -> {

            // Grab the cached list of bus stops.
            List<BusStopViewModel> savedBusStops = CacheHelper.getFromCache(cacheManager,
                    CACHE_KEY_SAVED_STOPS,
                    busStopsTypeToken);

            return savedBusStops != null ?
                    Single.just(savedBusStops) :
                    // We can't return null, so just return an empty HashMap if we don't have any saved stops.
                    Single.just(new ArrayList<BusStopViewModel>());
        });
    }

    public Single<List<BusStopViewModel>> saveBusStop(BusStopViewModel busStop) {
        // Get the current list of saved stops.
        return getSavedBusStops()
                // Append the new stop
                .flatMap(busStopViewModels -> {
                    // If the key already exists in the list, then don't bother re-adding it.
                    if (!busStopViewModels.contains(busStop)) {
                        busStopViewModels.add(busStop);
                    }
                    // Return the new list of saved stops
                    return Single.just(busStopViewModels);
                });
    }

    public Single<List<BusStopViewModel>> removeSavedStop(BusStopViewModel busStop) {
        // Get the current list of saved stops.
        return getSavedBusStops()
                .flatMap(busStopViewModels -> {
                    // Remove the bus stop.
                    busStopViewModels.remove(busStop);
                    // Return the new list of saved stops.
                    return Single.just(busStopViewModels);
                });
    }
}
