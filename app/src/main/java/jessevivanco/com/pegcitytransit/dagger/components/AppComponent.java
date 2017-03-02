package jessevivanco.com.pegcitytransit.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import jessevivanco.com.pegcitytransit.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.dagger.modules.RepositoriesModule;
import jessevivanco.com.pegcitytransit.dagger.modules.RestModule;
import jessevivanco.com.pegcitytransit.provider.BusRoutesAdapterProvider;
import jessevivanco.com.pegcitytransit.provider.BusStopsAdapterProvider;

/**
 * The bridge between our dependencies and our targets
 */
@Singleton
@Component(modules = {
        AppModule.class,
        RestModule.class,
        RepositoriesModule.class
})
public interface AppComponent {

    // Add injection targets here.

    void injectInto(BusStopsAdapterProvider busStopsProvider);

    void injectInto(BusRoutesAdapterProvider busRoutesProvider);
}
