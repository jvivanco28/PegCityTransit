package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
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
import jessevivanco.com.pegcitytransit.data.util.CacheUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;


public class BusRoutesRepository {

    private static final int ALL_BUS_ROUTES_CACHE_EXPIRY = CacheManager.ExpiryTimes.ONE_WEEK.asSeconds() * 2;
    private static final int BUS_ROUTES_AT_BUS_STOP_CACHE_EXPIRY = CacheManager.ExpiryTimes.ONE_DAY.asSeconds();

    private final String CACHE_KEY_ALL_ROUTES;

    private Context context;
    private RestApi restApi;
    @Nullable
    private
    CacheManager cacheManager;
    private final Type routesTypeToken;

    public BusRoutesRepository(Context context, RestApi restApi, @Nullable CacheManager cacheManager) {
        this.context = context;
        this.restApi = restApi;
        this.cacheManager = cacheManager;

        this.CACHE_KEY_ALL_ROUTES = context.getString(R.string.cache_key_all_routes);
        this.routesTypeToken = new TypeToken<List<BusRouteViewModel>>() {
        }.getType();
    }

    public Single<List<BusRouteViewModel>> getAllBusRoutes() {

        return Single.defer(() -> {

            List<BusRouteViewModel> cachedBusRoutes = CacheUtil.getFromCache(cacheManager, CACHE_KEY_ALL_ROUTES, routesTypeToken);

            if (cachedBusRoutes != null) {
                return Single.just(cachedBusRoutes);
            } else {
                // Cached routes are expired or never existed so we'll need to fetch them.
                return restApi.getAllRoutes()
                        .map(WinnipegTransitResponse::getElement)
                        .flatMapObservable(Observable::fromIterable)
                        .map(BusRouteViewModel::createFromBusRoute)
                        .toList()
                        .doOnSuccess(busRouteViewModels -> {
                            if (cacheManager != null) {
                                cacheManager.put(CACHE_KEY_ALL_ROUTES, busRouteViewModels, ALL_BUS_ROUTES_CACHE_EXPIRY, false);
                            }
                        });
            }
        });
    }

    public Single<List<BusRouteViewModel>> getRoutesForBusStop(@NonNull Long busStopKey) {

        if (busStopKey == null) {
            throw new IllegalArgumentException("Bus stop key must not be null!");
        }

        return Single.defer(() -> {

            // Grab the cached list of bus routes for the
            final String CACHE_KEY = Phrase.from(context, R.string.cache_key_routes_at_stop).put("stop", String.valueOf(busStopKey)).format().toString();

            List<BusRouteViewModel> cachedBusRoutes = CacheUtil.getFromCache(cacheManager, CACHE_KEY, routesTypeToken);

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
                                cacheManager.put(CACHE_KEY, busRouteViewModels, BUS_ROUTES_AT_BUS_STOP_CACHE_EXPIRY, false);
                            }
                        });
            }
        });
    }
}
