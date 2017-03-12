package jessevivanco.com.pegcitytransit.data.dagger.modules;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;

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
