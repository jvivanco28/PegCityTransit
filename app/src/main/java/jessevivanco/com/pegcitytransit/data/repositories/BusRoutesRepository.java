package jessevivanco.com.pegcitytransit.data.repositories;

import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.data.util.CacheHelper;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;


public class BusRoutesRepository {

    private static final String TAG = BusRoutesRepository.class.getSimpleName();

    private static final String CACHE_KEY_ALL_ROUTES = "all_routes";
    private static final int CACHE_EXPIRY = CacheManager.ExpiryTimes.ONE_MONTH.asSeconds();

    private final Type routesTypeToken;

    private RestApi restApi;
    private @Nullable
    CacheManager cacheManager;

    public BusRoutesRepository(RestApi restApi, @Nullable CacheManager cacheManager) {
        this.restApi = restApi;
        this.cacheManager = cacheManager;
        this.routesTypeToken = new TypeToken<List<BusRouteViewModel>>() {
        }.getType();
    }

    public Single<List<BusRouteViewModel>> getRoutesForBusStop(Long busStopKey) {

        return Single.defer(() -> {

            // Grab the cached list of bus routes.
            List<BusRouteViewModel> cachedBusRoutes = CacheHelper.getFromCache(cacheManager, CACHE_KEY_ALL_ROUTES, routesTypeToken);

            if (cachedBusRoutes != null) {
                return Single.just(cachedBusRoutes);
            } else {
                // Cached routes are expired or never existed so we'll need to fetch them.
                return restApi.getRoutesForBusStop(busStopKey)
                        .map(WinnipegTransitResponse::getElement)
                        .flatMapObservable(Observable::fromIterable)
                        .map(BusRouteViewModel::createFromBusRoute)
                        .toList()
                        .doOnSuccess(busRouteViewModels -> {
                            if (cacheManager != null) {
                                cacheManager.put(CACHE_KEY_ALL_ROUTES, busRouteViewModels, CACHE_EXPIRY, false);
                            }
                        });
            }
        });
    }
}
