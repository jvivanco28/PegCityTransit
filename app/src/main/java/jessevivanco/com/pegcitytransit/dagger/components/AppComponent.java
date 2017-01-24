package jessevivanco.com.pegcitytransit.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import jessevivanco.com.pegcitytransit.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.dagger.modules.RepositoriesModule;
import jessevivanco.com.pegcitytransit.dagger.modules.RestModule;
import jessevivanco.com.pegcitytransit.ui.provider.BusRoutesProvider;
import jessevivanco.com.pegcitytransit.ui.provider.BusStopsProvider;

/**
 * TODO DOC
 */
@Singleton
@Component(modules = {
        AppModule.class,
        RestModule.class,
        RepositoriesModule.class
})
public interface AppComponent {

    void injectFields(BusStopsProvider busStopsProvider);

    void injectFields(BusRoutesProvider busRoutesProvider);
}
