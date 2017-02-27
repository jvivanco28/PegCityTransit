package jessevivanco.com.pegcitytransit.dagger.modules;

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
    BusRoutesRepository provideBusRoutesRepository(RestApi restApi) {
        return new BusRoutesRepository(restApi);
    }

    @Provides
    @Singleton
    @Inject
    BusStopRepository provideBusStopRepository(RestApi restApi) {
        return new BusStopRepository(restApi);
    }
}
