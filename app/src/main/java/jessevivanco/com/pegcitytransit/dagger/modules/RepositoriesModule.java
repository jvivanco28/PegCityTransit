package jessevivanco.com.pegcitytransit.dagger.modules;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jessevivanco.com.pegcitytransit.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.rest.RestApi;

@Module
public class RepositoriesModule {

    @Provides
    @Singleton
    @Inject
    BusRoutesRepository provideBusRoutesRepository(Context context, RestApi restApi) {
        return new BusRoutesRepository(context, restApi);
    }

    @Provides
    @Singleton
    @Inject
    BusStopRepository provideBusStopRepository(Context context, RestApi restApi) {
        return new BusStopRepository(context, restApi);
    }
}
