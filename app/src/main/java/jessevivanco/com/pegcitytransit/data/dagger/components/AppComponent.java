package jessevivanco.com.pegcitytransit.data.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import jessevivanco.com.pegcitytransit.data.dagger.modules.AppModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RepositoriesModule;
import jessevivanco.com.pegcitytransit.data.dagger.modules.RestModule;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;

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

    void injectInto(BusStopsPresenter busStopsProvider);
}
