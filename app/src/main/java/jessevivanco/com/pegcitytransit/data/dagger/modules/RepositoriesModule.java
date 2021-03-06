package jessevivanco.com.pegcitytransit.data.dagger.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import com.iainconnor.objectcache.CacheManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopScheduleRepository;
import jessevivanco.com.pegcitytransit.data.repositories.PreferencesRepository;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;

@Module
public class RepositoriesModule {

    @Provides
    @Singleton
    @Inject
    BusRoutesRepository provideBusRoutesRepository(Context context, RestApi restApi, @Nullable CacheManager cacheManager) {
        return new BusRoutesRepository(context, restApi, cacheManager);
    }

    @Provides
    @Singleton
    @Inject
    BusStopRepository provideBusStopRepository(Context context, RestApi restApi, @Nullable CacheManager cacheManager) {
        return new BusStopRepository(context, restApi, cacheManager);
    }

    @Provides
    @Singleton
    @Inject
    BusStopScheduleRepository provideBusStopScheduleRepository(Context context, RestApi restApi) {
        return new BusStopScheduleRepository(context, restApi);
    }

    @Provides
    @Singleton
    @Inject
    PreferencesRepository providePreferenceRepository(Context context) {
        return new PreferencesRepository(context);
    }
}
